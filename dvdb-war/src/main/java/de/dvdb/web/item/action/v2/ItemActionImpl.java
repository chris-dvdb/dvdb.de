package de.dvdb.web.item.action.v2;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.ItemNotFoundException;
import de.dvdb.domain.model.item.ItemRepository;
import de.dvdb.domain.model.item.UserItemRating;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.web.Actor;
import de.dvdb.web.social.blog.BlogAction;

@Name("itemAction")
@Scope(ScopeType.CONVERSATION)
public class ItemActionImpl implements Serializable {

	private static final long serialVersionUID = 5016519729387952736L;

	@In
	Actor actor;

	@In
	EntityManager dvdb;

	@Logger
	protected Log log;

	@In
	FacesContext facesContext;

	@In
	FacesMessages facesMessages;

	Long itemId;

	Long item2Id;

	Long dvdId;

	@Out(required = false, scope = ScopeType.CONVERSATION)
	Item itemDetails;

	@In(required = false)
	@Out(required = false, scope = ScopeType.CONVERSATION)
	UserItemRating userItemRatingDetails;

	@In
	ItemRepository itemRepository;

	@In(create = true)
	BlogAction blogAction;

	@SuppressWarnings("unchecked")
	@Factory("itemDetails")
	@Begin(join = true)
	public void loadItemDetails() throws ItemNotFoundException {

		if (itemDetails != null && itemDetails.getId() == itemId)
			return;

		if (itemId == null && dvdId == null && item2Id == null)
			return;

		if (item2Id != null)
			itemId = item2Id;

		log.info("Loading item " + itemId + "/" + dvdId);

		if (getDvdId() != null) {
			itemDetails = itemRepository.getItemByDvdId(getDvdId(), actor
					.getUser());
		} else {
			itemDetails = itemRepository.getItem(getItemId(), actor.getUser());
		}

		// set rating
		List<UserItemRating> uirs = dvdb
				.createQuery(
						"from UserItemRating uir where uir.user = :user and uir.item = :item")
				.setParameter("user", actor.getUser()).setParameter("item",
						itemDetails).getResultList();

		if (uirs.size() == 1) {
			userItemRatingDetails = uirs.get(0);
		} else if (uirs.size() == 0) {
			userItemRatingDetails = new UserItemRating();
			userItemRatingDetails.setUser(actor.getUser());
			userItemRatingDetails.setItem(itemDetails);
		}

		blogAction.searchForItem(itemDetails);
	}

	@Restrict(value = "#{identity.loggedIn}")
	public void updateUserItemRating() {
		if (userItemRatingDetails.getId() == null)
			dvdb.persist(userItemRatingDetails);
		else
			dvdb.merge(userItemRatingDetails);

		// TODO: update/test rating

		// if (mediabaseItemDetails != null
		// && mediabaseItemDetails.getId() != null) {
		// mediabaseItemDetails.setRatingContent(userItemRatingDetails
		// .getRatingContent());
		// mediabaseItemDetails.setRatingMastering(userItemRatingDetails
		// .getRatingMastering());
		// dvdb.merge(mediabaseItemDetails);
		// }

		Events.instance().raiseEvent(ItemRepository.EVENT_ITEMREFRESHREQUIRED,
				itemDetails);
	}

	public Long getDvdId() {
		return dvdId;
	}

	public void setDvdId(Long dvdId) {
		this.dvdId = dvdId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public Long getItem2Id() {
		return item2Id;
	}

	public void setItem2Id(Long item2Id) {
		this.item2Id = item2Id;
	}

}
