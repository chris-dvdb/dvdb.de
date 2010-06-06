package de.dvdb.web.mediabase.action.v2;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.item.ItemRepository;
import de.dvdb.domain.model.item.UserItemRating;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.mediabase.MediabaseItem;
import de.dvdb.domain.model.mediabase.MediabaseItemCollectible;
import de.dvdb.domain.model.mediabase.MediabaseItemWish;
import de.dvdb.domain.model.mediabase.MediabaseService;
import de.dvdb.domain.model.social.Buddy;
import de.dvdb.domain.model.social.FacebookSession;
import de.dvdb.web.Actor;
import de.dvdb.web.facebook.FacebookController;
import de.dvdb.web.item.action.v3.ItemController;
import de.dvdb.web.social.buddy.SearchBuddyController;

@Name("mediabaseItemAction")
@Scope(ScopeType.PAGE)
public class MediabaseItemActionImpl implements Serializable {

	private static final long serialVersionUID = 5016519729387952736L;

	@In
	Actor actor;

	@In
	EntityManager dvdb;

	@Logger
	Log log;

	@In
	FacesContext facesContext;

	@In
	FacesMessages facesMessages;

	@In(value = "selectedItem", required=false)
	Item item;

	@In
	FacebookController facebookController;

	@In
	ApplicationSettings applicationSettings;

	@In(required = false)
	@Out(required = false, scope = ScopeType.CONVERSATION)
	UserItemRating userItemRatingDetails;

	Boolean postToFacebook = false;

	@In(required = false)
	Class<?> mediabaseItemClass;

	@In(required = false)
	SearchBuddyController searchBuddyController;

	@In(required = false)
	MediabaseItem mediabaseItem;

	@In
	MediabaseService mediabaseService;

	private void createMediabaseItemCollectible() {
		MediabaseItemCollectible mic = (MediabaseItemCollectible) Component
				.getInstance(MediabaseItemCollectible.class, true);
		mic.setMediabase(actor.getUser().getMediabase());
		mic.getEntityMetadata().setDateOfCreation(new Date());
		mic.setItem(item);
		mic.setDateOfPurchase(new Date());
		mic.setStatus(MediabaseItem.STATUS_NORMAL);

		if (userItemRatingDetails != null) {
			mic.setRatingContent(userItemRatingDetails.getRatingContent());
			mic.setRatingMastering(userItemRatingDetails.getRatingMastering());
		}

		item.setMediabaseItem(mic);
	}

	private void createMediabaseItemWish() {

		MediabaseItemWish miw = (MediabaseItemWish) Component.getInstance(
				MediabaseItemWish.class, true);
		miw.setMediabase(actor.getUser().getMediabase());
		miw.getEntityMetadata().setDateOfCreation(new Date());
		miw.setItem(item);
		miw.setStatus(MediabaseItem.STATUS_WISHLIST);

		if (userItemRatingDetails != null) {
			miw.setRatingContent(userItemRatingDetails.getRatingContent());
			miw.setRatingMastering(userItemRatingDetails.getRatingMastering());
		}

		item.setMediabaseItem(miw);
	}

//	@Factory("mediabaseItem")
//	public void init() {
//
//		mediabaseItem = mediabaseService.getMediabaseItem(actor.getUser()
//				.getMediabase(), item);
//
//		if (mediabaseItem != null)
//			return;
//
//		if (mediabaseItemClass == null)
//			throw new IllegalStateException("mediabaseItemClass is null");
//
//		if (mediabaseItemClass.equals(MediabaseItemCollectible.class))
//			createMediabaseItemCollectible();
//		else
//			createMediabaseItemWish();
//	}


	public MediabaseItem getMediabaseItem() {
		return mediabaseItem;
	}
	
	public void setMediabaseItem(MediabaseItem mediabaseItem) {
		this.mediabaseItem = mediabaseItem;
	}
		
	
	

	private void post() {
		if (item.getMediabaseItem() instanceof MediabaseItemWish)
			return;

		if (getPostToFacebook() && actor.getUser().getFacebookUserId() != null) {
			// lookup fb session
			List<FacebookSession> sessions = dvdb.createQuery(
					"from FacebookSession s where s.user = :u").setParameter(
					"u", actor.getUser().getFacebookUserId()).getResultList();
			if (sessions.size() > 0) {
				try {
					log.info("Posting to facebook");
					facebookController.publish(actor.getUser(),
							sessions.get(0), item);
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e);
				}
			}
		}
	}


	public void remove() {

		if (item.getMediabaseItem() instanceof MediabaseItemCollectible) {
			MediabaseItemCollectible mic = (MediabaseItemCollectible) item
					.getMediabaseItem();
			if (mic.getBorrowedToBuddy() != null) {
				facesMessages
						.addFromResourceBundle("mediabaseItemAction.remove.failed.borrowed");
				return;
			}
		}

		dvdb.remove(dvdb.find(MediabaseItem.class, item.getMediabaseItem()
				.getId()));
		item.setMediabaseItem(null);
		Contexts.getConversationContext().remove("mediabaseItemDetails");

		Events.instance().raiseEvent(ItemRepository.EVENT_ITEMREFRESHREQUIRED,
				item);
		Events.instance().raiseEvent(
				MediabaseService.EVENT_MEDIABASEREFRESHREQUIRED,
				actor.getUser());

		facesMessages
				.addFromResourceBundle("mediabaseItemAction.remove.success");

		Conversation.instance().end();
		Conversation.instance().redirectToRoot();
	}

	public void moveWishToCollection() {

		dvdb
				.createQuery(
						"delete from MediabaseItem mi where mi.item = :item and mi.mediabase = :mediabase")
				.setParameter("item", item).setParameter("mediabase",
						actor.getUser().getMediabase()).executeUpdate();

		Contexts.getConversationContext().remove("mediabaseItemDetails");

		createMediabaseItemCollectible();

		setPostToFacebook(false);

		dvdb.persist(item);

		facesMessages
				.addFromResourceBundle("mediabaseItemAction.moveWishToCollection.success");

	}

	public String borrowedReturned() {
		((MediabaseItemCollectible) item.getMediabaseItem())
				.setBorrowedToBuddy(null);
		dvdb.merge(item);
		facesMessages
				.addFromResourceBundle("mediabaseItemAction.borrowedReturned.success");
		return null;
	}

	public String borrowed() {

		// borrowed to anyone?
		if (searchBuddyController != null
				&& searchBuddyController.getSearchString() != null) {
			String username = searchBuddyController.getSearchString();
			if (!username.equals("")) {
				List<Buddy> users = (List<Buddy>) dvdb.createQuery(
						"from Buddy b where b.name = :name").setParameter(
						"name", username).getResultList();
				if (users.size() < 1) {
					facesMessages
							.addFromResourceBundle("mediabaseItemAction.persist.failed.userNotFound");
					return null;
				} else {
					((MediabaseItemCollectible) item.getMediabaseItem())
							.setBorrowedToBuddy(users.get(0));
				}
			} else {
				((MediabaseItemCollectible) item.getMediabaseItem())
						.setBorrowedToBuddy(null);
			}
		}

		if (item.getMediabaseItem().getId() != null)
			dvdb.merge(item.getMediabaseItem());
		else
			dvdb.persist(item.getMediabaseItem());
		facesMessages
				.addFromResourceBundle("mediabaseItemAction.borrowed.success");
		return null;
	}

	public Boolean getPostToFacebook() {
		return postToFacebook;
	}

	public void setPostToFacebook(Boolean postToFacebook) {
		this.postToFacebook = postToFacebook;
	}

}
