package de.dvdb.web.item.action.v3;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.mediabase.MediabaseItem;
import de.dvdb.domain.model.mediabase.MediabaseItemCollectible;
import de.dvdb.domain.model.mediabase.MediabaseService;
import de.dvdb.web.Actor;

@Name("itemController")
@Scope(ScopeType.PAGE)
public class ItemController {

	@Logger
	Log log;

	@Out(required = false)
	Item selectedItem;

	MediabaseItem selectedMediabaseItem;

	@In
	Actor actor;

	@In
	MediabaseService mediabaseService;

	@In
	FacesMessages facesMessages;

	public Item getSelectedItem() {
		return selectedItem;
	}

	/**
	 * Select the item and load mediabase item (if user is authenticated)
	 * 
	 * @param selectedItem
	 */
	public void setSelectedItem(Item selectedItem) {
		this.selectedItem = selectedItem;
		this.selectedMediabaseItem = selectedItem.getMediabaseItem();
	}

	public MediabaseItemCollectible getSelectedMediabaseItem() {
		return (MediabaseItemCollectible) selectedMediabaseItem;
	}

	public void setSelectedMediabaseItem(
			MediabaseItemCollectible selectedMediabaseItem) {
		this.selectedMediabaseItem = selectedMediabaseItem;
		this.selectedItem = selectedMediabaseItem.getItem();
		this.selectedItem.setMediabaseItem(selectedMediabaseItem);
	}

	@Restrict(value = "#{identity.loggedIn}")
	public void updateMediabaseItem() {

		mediabaseService.persist(selectedMediabaseItem);

		facesMessages
				.addFromResourceBundle("mediabaseItemAction.update.success");

		// post();

	}

	@Restrict(value = "#{identity.loggedIn}")
	public void createMediabaseItem() {
		selectedMediabaseItem = new MediabaseItemCollectible();
		selectedMediabaseItem.setMediabase(actor.getUser().getMediabase());
		selectedMediabaseItem.setItem(selectedItem);
		mediabaseService.persist(selectedMediabaseItem);
		selectedItem.setMediabaseItem(selectedMediabaseItem);

	}

	@Restrict(value = "#{identity.loggedIn}")
	public void persistMediabaseItem() {

		mediabaseService.persist(selectedMediabaseItem);

		// Events.instance().raiseEvent(ItemRepository.EVENT_ITEMREFRESHREQUIRED,
		// item);
		// Events.instance().raiseEvent(
		// MediabaseService.EVENT_MEDIABASEREFRESHREQUIRED,
		// actor.getUser());
		//
		// post();

		facesMessages
				.addFromResourceBundle("mediabaseItemAction.persist.success");

	}

}
