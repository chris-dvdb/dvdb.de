package de.dvdb.web.mediabase.action;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.item.ItemNotFoundException;
import de.dvdb.domain.model.item.ItemRepository;
import de.dvdb.domain.model.item.UserItemRating;
import de.dvdb.domain.model.mediabase.MediabaseItem;
import de.dvdb.domain.model.mediabase.MediabaseItemCollectible;
import de.dvdb.domain.model.mediabase.MediabaseService;
import de.dvdb.web.Actor;

@Name("importAction")
public class ImportAction implements Serializable {

	private static final long serialVersionUID = 1459434470822060294L;

	@In
	Actor actor;

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	@In
	FacesMessages facesMessages;

	String searchString;

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public void addToMoviebase(Item item) throws ItemNotFoundException {

		log.info("Adding to moviebase item #0", item);
		createMediabaseItemCollectible(item.getId());

	}

	public void addToMoviebase() throws ItemNotFoundException {

		log.info("Adding to moviebase item #0", selectedItem);
		createMediabaseItemCollectible(selectedItem.getId());

	}

	public void search() {
		importActionResults = suggestTitle(getSearchString());
		searchString = null;
	}

	@DataModel
	private List<Item> importActionResults;

	@DataModelSelection
	Item selectedItem;

	@In(create = true)
	ItemRepository itemService;

	@SuppressWarnings("unchecked")
	private void createMediabaseItemCollectible(Long itemId)
			throws ItemNotFoundException {

		Item item = itemService.getItem(itemId, actor.getUser());
		if (item.getMediabaseItem() != null) {
			facesMessages
					.addFromResourceBundle("importAction.reateMediabaseItem.failed.alreadyExits");
			return;
		}

		MediabaseItemCollectible mic = (MediabaseItemCollectible) Component
				.getInstance(MediabaseItemCollectible.class, true);
		mic.setMediabase(actor.getUser().getMediabase());
		mic.getEntityMetadata().setDateOfCreation(new Date());
		mic.setItem(item);
		mic.setDateOfPurchase(new Date());
		mic.setStatus(MediabaseItem.STATUS_NORMAL);

		// set rating
		UserItemRating userItemRatingDetails = null;
		List<UserItemRating> uirs = dvdb
				.createQuery(
						"from UserItemRating uir where uir.user = :user and uir.item = :item")
				.setParameter("user", actor.getUser()).setParameter("item",
						item).getResultList();

		if (uirs.size() == 1) {
			userItemRatingDetails = uirs.get(0);
		}

		// get rating and add
		if (userItemRatingDetails != null) {
			mic.setRatingContent(userItemRatingDetails.getRatingContent());
			mic.setRatingMastering(userItemRatingDetails.getRatingMastering());
		}

		dvdb.persist(mic);

		Events.instance().raiseAsynchronousEvent(
				ItemRepository.EVENT_ITEMREFRESHREQUIRED, item);
		Events.instance().raiseAsynchronousEvent(
				MediabaseService.EVENT_MEDIABASEREFRESHREQUIRED, actor);

		facesMessages
				.addFromResourceBundle("mediabaseItemAction.persist.success");

	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<Item> suggestTitle(Object fragment) {

		Map<String, Object> resMap = new HashMap<String, Object>();

		String[] tokens = fragment.toString().split("\\s");
		StringBuilder sb = new StringBuilder(
				"from PalaceDVDItem item where 1 = 1 ");

		int n = 0;
		for (String token : tokens) {
			n++;
			String tokname = "tok" + n;
			sb.append(" and (title like :" + tokname + " or ean like :"
					+ tokname + ") ");
			resMap.put(tokname, "%" + token + "%");
		}

		sb.append(" order by titleLex");

		Query query = dvdb.createQuery(sb.toString()).setMaxResults(25);

		for (String key : resMap.keySet()) {
			query.setParameter(key, resMap.get(key));
		}

		return query.getResultList();
	}
}
