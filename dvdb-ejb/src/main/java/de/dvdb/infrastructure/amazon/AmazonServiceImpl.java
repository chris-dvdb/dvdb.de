package de.dvdb.infrastructure.amazon;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import com.amazon.soap.ecs.AWSECommerceService;
import com.amazon.soap.ecs.AWSECommerceServicePortType;
import com.amazon.soap.ecs.BrowseNode;
import com.amazon.soap.ecs.BrowseNodes;
import com.amazon.soap.ecs.Errors;
import com.amazon.soap.ecs.Item;
import com.amazon.soap.ecs.ItemLookup;
import com.amazon.soap.ecs.ItemLookupRequest;
import com.amazon.soap.ecs.ItemLookupResponse;
import com.amazon.soap.ecs.Items;
import com.amazon.soap.ecs.Offer;
import com.amazon.soap.ecs.Request;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.Headers;
import com.sun.xml.ws.developer.WSBindingProvider;

import de.dvdb.PartnerSecrets;
import de.dvdb.domain.model.item.AmazonDVDItem;
import de.dvdb.domain.model.pricing.Price;
import de.dvdb.domain.service.AmazonService;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Name("amazonBridge")
@AutoCreate
public class AmazonServiceImpl implements AmazonService, PartnerSecrets,
		Serializable {

	private static final long serialVersionUID = 965755125849259771L;

	@PersistenceContext(unitName = "dvdb")
	EntityManager entityManager;

	private static Log log = LogFactory.getLog(AmazonServiceImpl.class
			.getName());

	/**
	 * Gets the current Amazon Price for an Amazon item.
	 */
	public Price getCurrentPriceForItem(
			de.dvdb.domain.model.item.Item amazonItem) {
		try {
			SignatureHelper sigo = new SignatureHelper();
			String time = sigo.getTimestamp();
			String sig = sigo.sign("ItemLookup" + time);

			AWSECommerceService service = new AWSECommerceService();
			AWSECommerceServicePortType port = service
					.getAWSECommerceServicePort();

			ItemLookup lookup = new ItemLookup();
			ItemLookupRequest request = new ItemLookupRequest();
			request.getResponseGroup().add("Offers");
			request.getResponseGroup().add("SalesRank");
			request.getResponseGroup().add("ItemIds");
			request.getItemId().add(amazonItem.getAsin());
			request.setIdType("ASIN");
			lookup.getRequest().add(request);

			WSBindingProvider bp = (WSBindingProvider) port;
			Header h1 = Headers.create(new QName(
					"http://security.amazonaws.com/doc/2007-01-01/",
					"AWSAccessKeyId"), AMAZON_ACCESSKEY);
			Header h2 = Headers.create(new QName(
					"http://security.amazonaws.com/doc/2007-01-01/",
					"Timestamp"), time);
			Header h3 = Headers.create(new QName(
					"http://security.amazonaws.com/doc/2007-01-01/",
					"Signature"), sig);
			bp.setOutboundHeaders(h1, h2, h3);

			ItemLookupResponse response = port.itemLookup(lookup);
			List<Items> itemsArray = response.getItems();

			handleErrors(itemsArray.get(0).getRequest());

			List<Item> items = itemsArray.get(0).getItem();

			if (items == null || items.size() != 1 || items.get(0) == null)
				return null;

			Item item = items.get(0);

			if (item.getOffers() == null)
				return null; // no offers for item

			List<Offer> offers = item.getOffers().getOffer(); // get offers
			Offer amaOff = getAmazonItem(offers); // what offers amazon?
			if (amaOff == null
					|| amaOff.getOfferListing() == null
					|| amaOff.getOfferListing().get(0) == null
					|| amaOff.getOfferListing().get(0).getPrice() == null
					|| amaOff.getOfferListing().get(0).getPrice().getAmount() == null)
				return null;

			int priceInCent = amaOff.getOfferListing().get(0).getPrice()
					.getAmount().intValue();

			Double price = new Double(priceInCent) / 100;
			String availability = amaOff.getOfferListing().get(0)
					.getAvailability();
			String productUrl = AMAZON_PRODUCTURL.replaceAll("#ASIN#", item
					.getASIN());

			Price p = new Price();
			p.setItem(amazonItem);
			p.setAvailability(availability);
			p.setPrice(price);
			p.setUrl(productUrl);
			return p;

		} catch (Exception e) {
			log.error("Error accessing Amazon Webservice for fetching a price "
					+ e);
		}
		return null;
	}

	/**
	 * Creates a new AmazonItem based on the retrieved item attributes for an
	 * ASIN.
	 */
	public AmazonDVDItem getAmazonDVDItemFull(String asin) {

		try {
			SignatureHelper sigo = new SignatureHelper();
			String time = sigo.getTimestamp();
			String sig = sigo.sign("ItemLookup" + time);

			log.info("Getting item details for asin " + asin + ". Sig " + sig);

			// Set the service:
			AWSECommerceService service = new AWSECommerceService();
			AWSECommerceServicePortType port = service
					.getAWSECommerceServicePort();

			ItemLookup lookup = new ItemLookup();
			ItemLookupRequest request = new ItemLookupRequest();

			request.getResponseGroup().add("Large");
			request.getResponseGroup().add("Images");
			request.getResponseGroup().add("SalesRank");
			request.getResponseGroup().add("ItemIds");
			request.getResponseGroup().add("ItemAttributes");
			request.getResponseGroup().add("BrowseNodes");
			request.getItemId().add(asin);
			request.setIdType("ASIN");
			lookup.getRequest().add(request);

			WSBindingProvider bp = (WSBindingProvider) port;
			Header h1 = Headers.create(new QName(
					"http://security.amazonaws.com/doc/2007-01-01/",
					"AWSAccessKeyId"), AMAZON_ACCESSKEY);
			Header h2 = Headers.create(new QName(
					"http://security.amazonaws.com/doc/2007-01-01/",
					"Timestamp"), time);
			Header h3 = Headers.create(new QName(
					"http://security.amazonaws.com/doc/2007-01-01/",
					"Signature"), sig);
			bp.setOutboundHeaders(h1, h2, h3);

			ItemLookupResponse response = port.itemLookup(lookup);

			List<Items> itemsArray = response.getItems();
			handleErrors(itemsArray.get(0).getRequest());

			List<Item> items = itemsArray.get(0).getItem();

			if (items == null || items.size() != 1 || items.get(0) == null)
				return null;

			Item item = items.get(0);

			AmazonDVDItem amazonItem = null;
			amazonItem = new AmazonDVDItem(item);

			log.info("Got details for asin " + asin + " " + amazonItem);

			return amazonItem;

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error accessing Amazon Webservice " + e);
		}
		return null;
	}

	/**
	 * Creates a new AmazonItem based on the retrieved item attributes for an
	 * ASIN.
	 */
	public AmazonDVDItem getAmazonDVDItemLight(String asin) {

		try {

			SignatureHelper sigo = new SignatureHelper();
			String time = sigo.getTimestamp();
			String sig = sigo.sign("ItemLookup" + time);

			log.info("Getting item details for asin " + asin);

			AWSECommerceService service = new AWSECommerceService();
			AWSECommerceServicePortType port = service
					.getAWSECommerceServicePort();

			ItemLookup lookup = new ItemLookup();
			ItemLookupRequest request = new ItemLookupRequest();
			request.getResponseGroup().add("Images");
			request.getResponseGroup().add("SalesRank");
			request.getResponseGroup().add("ItemIds");
			request.getResponseGroup().add("ItemAttributes");
			request.getResponseGroup().add("BrowseNodes");
			request.getItemId().add(asin);
			request.setIdType("ASIN");
			lookup.getRequest().add(request);

			WSBindingProvider bp = (WSBindingProvider) port;
			Header h1 = Headers.create(new QName(
					"http://security.amazonaws.com/doc/2007-01-01/",
					"AWSAccessKeyId"), AMAZON_ACCESSKEY);
			Header h2 = Headers.create(new QName(
					"http://security.amazonaws.com/doc/2007-01-01/",
					"Timestamp"), time);
			Header h3 = Headers.create(new QName(
					"http://security.amazonaws.com/doc/2007-01-01/",
					"Signature"), sig);
			bp.setOutboundHeaders(h1, h2, h3);

			ItemLookupResponse response = port.itemLookup(lookup);
			List<Items> itemsArray = response.getItems();

			handleErrors(itemsArray.get(0).getRequest());

			List<Item> items = itemsArray.get(0).getItem();

			if (items == null || items.size() != 1 || items.get(0) == null)
				return null;

			Item item = items.get(0);

			// browsenodes holen
			BrowseNodes bns = item.getBrowseNodes();
			List<BrowseNode> nods = bns.getBrowseNode();
			Set<String> allBrowseNodes = new HashSet<String>();
			for (BrowseNode browseNode : nods) {
				addNodes(allBrowseNodes, browseNode);
			}

			AmazonDVDItem amazonItem = null;

			if (AmazonDVDItem.isDVD(allBrowseNodes)) {
				amazonItem = new AmazonDVDItem();
			}

			// else if (AmazonGame.isGame(allBrowseNodes)) {
			// amazonItem = new AmazonGame();
			// String pf = "";
			// String[] pfs = item.getItemAttributes().getPlatform();
			// for (String string : pfs) {
			// pf = pf + "<li>" + string + "</li>";
			// }
			// ((AmazonGame) amazonItem).setPlattform(pf);
			// }
			//
			// else
			// amazonItem = new AmazonDVDItem();

			amazonItem.setEan(item.getItemAttributes().getEAN());
			amazonItem.setTitle(item.getItemAttributes().getTitle());
			amazonItem.setAsin(item.getASIN());
			amazonItem.setUrl(AMAZON_PRODUCTURL.replaceAll("#ASIN#", item
					.getASIN()));
			if (item.getLargeImage() != null)
				amazonItem.setUrlImageLarge(item.getLargeImage().getURL());
			if (item.getMediumImage() != null)
				amazonItem.setUrlImageMedium(item.getMediumImage().getURL());
			if (item.getSmallImage() != null)
				amazonItem.setUrlImageSmall(item.getSmallImage().getURL());
			if (item.getSalesRank() != null) {
				try {
					int salesRank = Integer.parseInt(item.getSalesRank()
							.replaceAll("\\.", "").replaceAll(",", ""));
					amazonItem.setSalesRank(salesRank);
				} catch (Exception e) {
					log.warn("Unable to parse sales rank "
							+ item.getSalesRank().replaceAll("\\.", "")
									.replaceAll(",", ""));
				}
			}

			String releaseDateString = item.getItemAttributes()
					.getReleaseDate();
			if (releaseDateString != null) {
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Date releaseDate = sdf.parse(releaseDateString);
					amazonItem.setReleaseDateAmazon(releaseDate);
				} catch (Exception e) {
					log.warn("Unable to parse release date "
							+ releaseDateString + ". " + e.getMessage());
				}
			}

			// amazonItem.setBrowseNodes(allBrowseNodes.toArray(new String[0]));
			log.info("Got details for asin " + asin + " " + amazonItem);

			return amazonItem;

		} catch (Exception e) {
			log.error("Error accessing Amazon Webservice " + e);
			e.printStackTrace();
		}
		return null;
	}

	// --- private helpers ---------------------------------------------------

	private void addNodes(Set<String> cats, BrowseNode node) {
		cats.add(node.getBrowseNodeId());
		if (node.getAncestors() != null
				&& node.getAncestors().getBrowseNode().size() > 0) {
			for (BrowseNode bn : node.getAncestors().getBrowseNode()) {
				addNodes(cats, bn);
			}
		}
	}

	private Offer getAmazonItem(List<Offer> offers) {
		if (offers == null)
			return null;
		for (int i = 0; i < offers.size(); i++) {
			if (offers.get(i).getMerchant() == null)
				continue;
			if (offers.get((i)).getMerchant().getMerchantId() == null)
				continue;

			if (!offers.get((i)).getMerchant().getMerchantId()
					.equalsIgnoreCase(AMAZON_AMAZONMERCHANTID))
				continue;
			else
				return offers.get(i);
		}
		return null;
	}

	private List<de.dvdb.domain.model.item.Item> replaceWithExistingItems(
			List<de.dvdb.domain.model.item.Item> items) {

		Map<String, de.dvdb.domain.model.item.Item> asins = new HashMap<String, de.dvdb.domain.model.item.Item>();
		for (de.dvdb.domain.model.item.Item item : items) {
			asins.put(item.getAsin(), item);
		}

		List<de.dvdb.domain.model.item.Item> dbItems = entityManager
				.createQuery("from Item i where i.asin in (:asins)")
				.setParameter("asins", asins.keySet()).getResultList();

		for (de.dvdb.domain.model.item.Item item : dbItems) {
			if (item.getAsin() != null)
				asins.put(item.getAsin(), item);
		}

		log.info("Found " + dbItems.size() + " in db " + dbItems);

		for (int i = 0; i < items.size(); i++) {
			items.set(i, asins.get(items.get(i).getAsin()));
		}

		return items;
	}

	private void handleErrors(Request request) {
		Errors errors = request.getErrors();
		if (errors != null) {
			for (Errors.Error error : errors.getError()) {
				log.warn("Error while retrieving item: " + error.getMessage());
			}
		}
	}

}
