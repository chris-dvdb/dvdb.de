package de.dvdb.domain.model.pricing;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.pricing.robot.PriceRobot;
import de.dvdb.domain.model.pricing.robot.RobotException;
import de.dvdb.domain.model.user.User;

@Stateless
@Name("priceManager")
@Local(PriceManager.class)
public class PriceManagerImpl implements PriceManager, Serializable {

	private static final long serialVersionUID = 965755125849259771L;

	public static final String EVENT_NEWBESTPRICE = "newBestPrice";

	@Logger
	Log log;

	@PersistenceContext(unitName = "dvdb")
	protected EntityManager dvdb;

	public Item updatePricesSync(Item item, boolean forceUpdate) {
		return this.updatePrices(item, forceUpdate);
	}

	/**
	 * Updates and manages the list prices for an amazonItem in our database. In
	 * order to do this the component calls each shops PriceFetcher component
	 * for this items. The name of the component that retrieves the prices is
	 * defined within the shop object in attribute priceFetcherComponentName. If
	 * this attribute is null we skip fetching prices for this shop.
	 * 
	 * @param amazonItem
	 *            ; the item to update its prices
	 */
	@SuppressWarnings("unchecked")
	public Item updatePrices(Item item, boolean forceUpdate) {

		log.info("Updating prices for " + item);

		if (!item.priceUpdateOk() && !forceUpdate) {
			log.info("Skipping not required price update for " + item);
			return item;
		}

		item.setDateOfLastPriceUpdate(new Date());
		dvdb.merge(item);
		dvdb.flush();

		User robot = (User) dvdb.createQuery(
				"from User u where u.username = :reporter").setParameter(
				"reporter", USERNAME_REPORTER).getSingleResult();

		List<Shop> shops = dvdb
				.createQuery(
						"from Shop shop where shop.priceFetcherComponentName is not null")
				.getResultList();

		for (Shop shop : shops) {

			if (shop.getPriceFetcherComponentName() == null
					|| shop.getPriceFetcherComponentName().equals(""))
				continue;

			Object o = Component.getInstance(shop
					.getPriceFetcherComponentName());

			if (o == null || !(o instanceof PriceRobot))
				continue;

			PriceRobot fetcher = (PriceRobot) o;

			// only handle shops where the price fetcher component is defined
			log.info("Searching shop " + shop.getName() + ", fetcher "
					+ fetcher);

			// get new price from shop

			Price shopPrice;
			try {
				shopPrice = fetcher.fetchRawPrice(item, shop);
			} catch (RobotException e) {
				log
						.error("Cannot get price for " + item + " from shop "
								+ shop);
				continue;
			}

			// get price currently in our database
			Price dbPrice = getDBPrice(item, shop);

			// add some attributes to our raw price data
			if (shopPrice != null) {
				shopPrice.setDateOfPrice(new Date());
				shopPrice.setItem(item);
				shopPrice.setShop(shop);
				shopPrice.setUser(robot);
				shopPrice.setDateOfPrice(new Date());
				shopPrice.setPriceValidated(true);
				shopPrice.setPriceValidatedByUser(robot);
				log.info("Found price " + shopPrice + " at " + shop.getName());
			}

			// add new price
			if (dbPrice == null && shopPrice != null) {
				addPrice(item, shopPrice, false);
			}

			// old price but no new price -> remove old price
			// remove existing price
			else if (dbPrice != null && shopPrice == null) {
				removePrice(item, dbPrice, false);
			}

			// compare existing old price with existing new price
			// udpate existing price
			else if (dbPrice != null && shopPrice != null) {
				if (dbPrice.getPrice().compareTo(shopPrice.getPrice()) != 0) {
					// price change
					dbPrice.setPreviousPrice(dbPrice.getPrice());
					dbPrice.setPrice(shopPrice.getPrice());
					dbPrice.setAvailability(shopPrice.getAvailability());
					dbPrice.setChangeDate(new Date());
					dbPrice.setDateOfPrice(new Date());
					dbPrice.setPriceValidated(true);
					dbPrice.setPriceValidatedByUser(robot);
					dbPrice.setDateOfPriceValidation(new Date());
					dbPrice.setDiscountAccepted(null);
					dbPrice.setDiscountFeedbackByUser(null);
					dbPrice.setDiscountFeedbackDate(null);
					if (dbPrice.getPrice().compareTo(shopPrice.getPrice()) < 0) {
						dbPrice.setTpgDeal(false);
						dbPrice.setPriceDeal(false);
					}

					Events.instance().raiseEvent(
							PriceManager.EVENT_PRICECHANGED, dbPrice);

					// priceChangeHandler.priceChangeEventHandler(1000l,
					// dbPrice);

				} else if (dbPrice.getPrice().compareTo(shopPrice.getPrice()) == 0) {
					dbPrice.setAvailability(shopPrice.getAvailability());
					dbPrice.setDateOfPrice(new Date());
					dbPrice.setDateOfPriceValidation(new Date());
					dbPrice.setPriceValidated(true);
					dbPrice.setPriceValidatedByUser(robot);
				}
				dvdb.merge(dbPrice);
			}
		}

		// update best prices
		item = updateBestPrice(item);

		log.info("Prices update for " + item);

		return item;

	}

	@In
	PriceChangeHandler priceChangeHandler;

	public void addPrice(Item item, Price price, boolean increaseReported) {
		item = dvdb.find(Item.class, item.getId());
		// price.setShop(dvdb.find(Shop.class, price.getShop().getId()));
		dvdb.persist(price);
		item.getPrices().add(0, price);

		item = updateBestPrice(item);
	}

	@SuppressWarnings("unchecked")
	public void cleanUpExpiredPrices() {
		User robot = (User) dvdb.createQuery(
				"from User u where u.username = :reporter").setParameter(
				"reporter", USERNAME_REPORTER).getSingleResult();

		List<Price> expiredPrices = dvdb
				.createQuery(
						"from Price nap where nap.validToDate < :now and nap.user != :luise")
				.setParameter("now", new Date()).setParameter("luise", robot)
				.getResultList();

		for (Price price : expiredPrices) {
			log.info("Removing expired price " + price);
			Item item = price.getItem();
			removePrice(item, price, false);
		}
	}

	public void invertFlagPriceDeal(Price price) {
		price = dvdb.find(Price.class, price.getId());
		if (price.getPriceDeal() == null)
			price.setPriceDeal(false);
		price.setPriceDeal(!price.getPriceDeal());
		if (price.getPriceDeal())
			price.setDealDiscoveryDate(new Date());
		dvdb.merge(price);
	}

	public void invertFlagTpgDeal(Price price) {
		price = dvdb.find(Price.class, price.getId());
		if (price.getTpgDeal() == null)
			price.setTpgDeal(false);
		price.setTpgDeal(!price.getTpgDeal());
		if (price.getTpgDeal())
			price.setDealDiscoveryDate(new Date());
		dvdb.merge(price);
	}

	public void removePrice(Item item, Price price, boolean decreaseReported) {
		item = dvdb.find(Item.class, item.getId());

		item.getPrices().remove(price);
		dvdb.merge(item);
		dvdb.remove(dvdb.find(Price.class, price.getId()));

		updateBestPrice(item);

	}


	// --- private helpers ----------------------------------------------------

	/**
	 * Calculates best online, best tpg price, best overall price and TPG
	 * discount.
	 * 
	 * @param item
	 */
	private Item updateBestPrice(Item item) {
		boolean fireAlarm = false;

		item.setAmazonPrice(null);
		item.setBestOnlinePrice(null);

		if (item.getPrices() != null || item.getPrices().size() > 0) {

			// set Amazon-Price
			for (Price price : item.getPrices()) {
				if (price.getShop() != null
						&& price.getShop().getId().equals(Shop.AMAZONID))
					item.setAmazonPrice(price);
			}

			// calculate best online Price
			for (Price price : item.getPrices()) {
				if ((item.getBestOnlinePrice() == null || price.compareTo(item
						.getBestOnlinePrice()) < 0)
						&& (price.getAvailableOnlineOrOnlineShop())) {
					item.setBestOnlinePrice(price);
					fireAlarm = true;
				}
			}
		}

		if (fireAlarm)
			Events.instance().raiseEvent(EVENT_NEWBESTPRICE,
					item.getBestOnlinePrice());

		return dvdb.merge(item);
	}

	private Price getDBPrice(Item item, Shop shop) {
		for (Price price : item.getPrices()) {
			if (price.getShop() == null)
				continue;
			if (price.getShop().equals(shop))
				return price;
		}
		return null;
	}
}
