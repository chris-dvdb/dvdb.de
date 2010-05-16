package de.dvdb.web.item.pricing.action;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.ItemNotFoundException;
import de.dvdb.domain.model.item.ItemRepository;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.pricing.Price;
import de.dvdb.domain.model.pricing.PriceManager;
import de.dvdb.web.Actor;

@Name("priceAction")
public class PriceAction implements Serializable {

	private static final long serialVersionUID = -2222102720904149239L;

	String asin;

	Long itemId;

	Boolean forcePriceUpdate = false;

	Boolean countClick = true;

	@In(create = true)
	Actor actor;

	@Logger
	Log log;

	@In
	FacesMessages facesMessages;

	@In
	EntityManager dvdb;

	@In
	ItemRepository itemRepository;

	@In(create = true)
	PriceManager priceManager;

	@Out(required = false)
	Item item;

	@Restrict(value = "#{identity.loggedIn}")
	public void removePrice(Price price) {

		priceManager.removePrice(item, price, true);
		item.getPrices().remove(price);

		price = null;

		facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
				"shopping.removePrice.success");

	}

	@Restrict(value = "#{identity.loggedIn}")
	public void invertFlagPriceDeal(Price price) {
		priceManager.invertFlagPriceDeal(price);
		facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
				"shopping.displayAsinDetails.invertFlagPriceDeal");
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	public void refreshPrices() {

		if (itemId != null) {
			try {
				item = itemRepository.getItem(itemId, actor.getUser());
				priceManager.updatePricesSync(item, forcePriceUpdate);
				log.info("-------------------------------------------");
			} catch (ItemNotFoundException e) {
				log.error("Item not found for ID " + itemId);
			}
			// } else if (asin != null) {
			// item = amazonConnector.getItemForASIN(asin);
			// if (item == null) {
			// log.error("Item not found for ASIN" + asin);
			// }
		}
	}

	public Boolean getForcePriceUpdate() {
		return forcePriceUpdate;
	}

	public void setForcePriceUpdate(Boolean forcePriceUpdate) {
		this.forcePriceUpdate = forcePriceUpdate;
	}

}
