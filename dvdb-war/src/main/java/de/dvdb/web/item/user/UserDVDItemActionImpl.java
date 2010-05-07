package de.dvdb.web.item.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.ItemRepository;
import de.dvdb.domain.model.item.type.UserDVDItem;
import de.dvdb.domain.model.mediabase.MediabaseItemCollectible;
import de.dvdb.domain.model.mediabase.MediabaseService;
import de.dvdb.web.Actor;

@Name("userDVDItemAction")
public class UserDVDItemActionImpl implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	@In
	FacesMessages facesMessages;

	@In
	Actor actor;

	@In
	ItemRepository itemRepository;

	Long itemId;

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Factory("userDVDItem")
	public void initUserDVDItem() {
		log.info("initUserDVDItem()");

		if (getItemId() == null) {
			userDVDItem = createInstance();
		} else {
			userDVDItem = dvdb.find(UserDVDItem.class, getItemId());
		}

		if (!userDVDItem.getUser().equals(actor.getUser())) {
			userDVDItem = null;
			throw new org.jboss.seam.security.AuthorizationException(
					"Must be admin to perform this action");
		}

	}

	@In(required = false)
	@Out(scope = ScopeType.PAGE, required = false)
	UserDVDItem userDVDItem;

	protected UserDVDItem createInstance() {
		UserDVDItem udi = new UserDVDItem();
		udi.setItemDateOfData(new Date());
		udi.setItemDateOfUpdate(new Date());
		udi.setUser(actor.getUser());
		return udi;
	}

	public void update() {
		if (userDVDItem.getTitle() != null) {
			userDVDItem.setTitleLex(userDVDItem.calcTitleLex(userDVDItem
					.getTitle()));
		}

		dvdb.merge(userDVDItem);

		Conversation.instance().end();
		Conversation.instance().redirectToRoot();
	}

	public void remove() {

		userDVDItem = dvdb.find(UserDVDItem.class, userDVDItem.getId());

		if (userDVDItem != null
				&& !userDVDItem.getUser().equals(actor.getUser()))
			throw new org.jboss.seam.security.AuthorizationException(
					"Must be admin to perform this action");

		itemRepository.removeItem(userDVDItem);

		// Events.instance().raiseAsynchronousEvent(
		// MediabaseService.EVENT_MEDIABASEREFRESHREQUIRED, actor);

		Conversation.instance().end();
		Conversation.instance().redirectToRoot();
	}

	public void persist() {
		if (userDVDItem.getTitle() != null) {
			userDVDItem.setTitleLex(userDVDItem.calcTitleLex(userDVDItem
					.getTitle()));
		}

		dvdb.persist(userDVDItem);

		MediabaseItemCollectible mic = new MediabaseItemCollectible();
		mic.setMediabase(actor.getUser().getMediabase());
		mic.setItem(userDVDItem);
		mic.getEntityMetadata().setDateOfCreation(new Date());
		mic.getEntityMetadata().setDateOfLastUpdate(new Date());
		dvdb.persist(mic);

		Events.instance().raiseAsynchronousEvent(
				MediabaseService.EVENT_MEDIABASEREFRESHREQUIRED, actor);

		Conversation.instance().end();
		Conversation.instance().redirectToRoot();
	}
}
