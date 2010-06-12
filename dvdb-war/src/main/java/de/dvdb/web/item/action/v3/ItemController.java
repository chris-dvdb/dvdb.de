package de.dvdb.web.item.action.v3;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
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

	MediabaseItem mediabaseItem;

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
		this.mediabaseItem = selectedItem.getMediabaseItem();		
	}

	public void updateMediabaseItem() {

		mediabaseService.persist(mediabaseItem);

		facesMessages
				.addFromResourceBundle("mediabaseItemAction.update.success");

		// post();

	}

	public void createMediabaseItem() {

		mediabaseItem = new MediabaseItemCollectible();
		mediabaseItem.setMediabase(actor.getUser().getMediabase());
		mediabaseItem.setItem(selectedItem);
		mediabaseService.persist(mediabaseItem);
		selectedItem.setMediabaseItem(mediabaseItem);

	}

	public void persistMediabaseItem() {

		mediabaseService.persist(mediabaseItem);

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
