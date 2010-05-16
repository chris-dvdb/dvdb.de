package de.dvdb.infrastructure.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.item.ItemNotFoundException;
import de.dvdb.domain.model.item.ItemRepository;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.mediabase.MediabaseItem;
import de.dvdb.domain.model.mediabase.MediabaseItemCollectible;
import de.dvdb.domain.model.pricing.PriceManager;
import de.dvdb.domain.model.user.User;
import de.dvdb.domain.service.AmazonService;

@Stateless
@Local(ItemRepository.class)
@Name("itemRepository")
@AutoCreate
public class ItemRepositoryJPA implements ItemRepository, Serializable {

	private static final long serialVersionUID = 965755125849259771L;

	@Logger
	Log log;

	@PersistenceContext(unitName = "dvdb")
	EntityManager entityManager;

	@EJB
	PriceManager priceManager;

	@EJB
	AmazonService amazonConnector;

	@In
	Renderer renderer;

	@In
	ApplicationSettings applicationSettings;

	// -------- Business Methods Impl --------------

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void maintainItemSync(Item i) {
		maintainItemData(i);
	}

	@SuppressWarnings("unchecked")
	public void removeItem(Item item) {

		item = entityManager.find(Item.class, item.getId());

		item.getTags().clear();
		item.getPrices().clear();

		item.setBestOnlinePrice(null);
		item.setAmazonPrice(null);

		List<MediabaseItemCollectible> items = entityManager.createQuery(
				"from MediabaseItemCollectible mic where mic.item = :item")
				.setParameter("item", item).getResultList();
		for (MediabaseItemCollectible mediabaseItemCollectible : items) {
			try {
				Contexts.getConversationContext().set(
						"email",
						mediabaseItemCollectible.getMediabase().getUser()
								.getEmail());

				if (!applicationSettings.isProduction()) {
					Contexts.getConversationContext().set("email",
							applicationSettings.getDevEmail());
				}

				Contexts.getConversationContext().set("mediabaseItem",
						mediabaseItemCollectible);
				renderer.render("/WEB-INF/mail/item/itemDeleted_de.xhtml");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		entityManager.createQuery(
				"delete from MediabaseItem e where e.item = :item")
				.setParameter("item", item).executeUpdate();

		entityManager.createQuery(
				"delete from ItemSubscriber e where e.item = :item")
				.setParameter("item", item).executeUpdate();
		entityManager.createQuery(
				"update BlogEntry e set e.item = null where e.item = :item")
				.setParameter("item", item).executeUpdate();

		entityManager.createQuery(
				"delete from UserItemRating uir where uir.item = :item")
				.setParameter("item", item).executeUpdate();
		// entityManager.createQuery("delete from Price p where p.item = :item")
		// .setParameter("item", item).executeUpdate();

		entityManager.remove(item);

		log.info("Removed item " + item);
	}

//	@Observer(value = EVENT_ITEMREFRESHREQUIRED)
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void maintainItemData(Item i) {
		if (i == null) {
			i = (Item) entityManager.createQuery(
					"from Item order by dateOfLastMaintenance asc")
					.setMaxResults(1).getSingleResult();
		}
		i.setDateOfLastMaintenance(new Date());

		log.info("Maintaining item " + i);

		// number of wishes
		Long wishes = (Long) entityManager
				.createQuery(
						"select count(mc) from MediabaseItemWish mc where mc.item = :item")
				.setParameter("item", i).getSingleResult();
		i.setNumberOfWishes(wishes);

		// number of owners
		Long cols = (Long) entityManager
				.createQuery(
						"select count(mc) from MediabaseItemCollectible mc where mc.item = :item")
				.setParameter("item", i).getSingleResult();
		i.setNumberOfOwners(cols);

		// number of ratings medium
		Object[] rats = (Object[]) entityManager
				.createQuery(
						"select sum(mi.ratingMastering), count(mi.ratingMastering) from UserItemRating mi where mi.item = :item and mi.ratingMastering is not null")
				.setParameter("item", i).getSingleResult();
		Long r = (Long) rats[0];
		Long nr = (Long) rats[1];

		if (r != null && nr != null && !nr.equals(0))
			i.setUserRatingMastering(((Double) r.doubleValue()) / nr);
		else
			i.setUserRatingMastering(null);
		i.setNumberOfUserRatingMastering(nr);

		// number of ratings content
		rats = (Object[]) entityManager
				.createQuery(
						"select sum(mi.ratingContent), count(mi.ratingContent) from UserItemRating mi where mi.item = :item and mi.ratingContent is not null")
				.setParameter("item", i).getSingleResult();
		r = (Long) rats[0];
		nr = (Long) rats[1];
		if (r != null && nr != null && !nr.equals(0))
			i.setUserRatingContent(((Double) r.doubleValue()) / nr);
		else
			i.setUserRatingContent(null);
		i.setNumberOfUserRatingContent(nr);

		// temp
		i.setTitleLex(i.calcTitleLex(i.getTitle()));

		entityManager.merge(i);
	}

	@SuppressWarnings("unchecked")
	public Item getItemByDvdId(Long id, User user) throws ItemNotFoundException {

		Item item = null;
		Query q = entityManager.createQuery(""
				+ "select distinct(i) from PalaceDVDItem i "
				+ " left join fetch i.amazonPrice "
				+ " left join fetch i.tags tags " + " where i.dvdId = :id");
		q.setParameter("id", id);
		List<Item> items = q.getResultList();

		if (items.size() == 0) {
			throw new ItemNotFoundException("Item with dvdid " + id
					+ " not found");
		}

		item = items.get(0);

		// do we need to do an price update?
		if (item.getMinutesSinceLastPriceUpdate() > 60 * 3
				&& applicationSettings.isProduction()) {
			log.info("Triggering Price update for " + item);
			priceManager.updatePrices(item, false);
		}

		if (user != null) {
			List<MediabaseItem> results = (List<MediabaseItem>) entityManager
					.createQuery(
							"from MediabaseItem mi where mi.mediabase = :mediabase and mi.item = :item")
					.setParameter("mediabase", user.getMediabase())
					.setParameter("item", item).getResultList();
			if (results.size() == 1)
				item.setMediabaseItem(results.get(0));
		}

		return item;
	}

	@SuppressWarnings("unchecked")
	public Item getItem(Long id, User user) throws ItemNotFoundException {

		log.info("Loading item #0", id);

		Item item = null;
		Query q = entityManager.createQuery(""
				+ "select distinct(i) from Item i "
				+ " left join fetch i.tags tags "
				+ " left join fetch i.amazonPrice " + " where i.id = :id");
		q.setParameter("id", id);
		List<Item> items = q.getResultList();

		if (items.size() == 0) {
			throw new ItemNotFoundException("Item " + id + " not found");
		}

		item = items.get(0);

		// do we need to do an price update?
		if (item.getMinutesSinceLastPriceUpdate() > 60 * 3) {
			log.info("Triggering Price update for " + item);
			priceManager.updatePrices(item, false);
		}

		if (user != null) {
			List<MediabaseItem> results = (List<MediabaseItem>) entityManager
					.createQuery(
							"from MediabaseItem mi where mi.mediabase = :mediabase and mi.item = :item")
					.setParameter("mediabase", user.getMediabase())
					.setParameter("item", item).getResultList();
			if (results.size() == 1) {
				item.setMediabaseItem(results.get(0));
				if (results.get(0) instanceof MediabaseItemCollectible) {
					log.info("Borrowed to "
							+ ((MediabaseItemCollectible) results.get(0))
									.getBorrowedToBuddy());
				}
			}
		}

		return item;
	}

	public void maintainItem(@Expiration Date date,
			@IntervalDuration Long interval) {
		maintainItemData(null);
	}
}
