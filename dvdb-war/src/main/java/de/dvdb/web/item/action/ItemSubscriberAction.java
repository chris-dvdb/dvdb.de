package de.dvdb.web.item.action;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.item.ItemSubscriber;
import de.dvdb.domain.model.pricing.Price;
import de.dvdb.web.Actor;

@Name("itemSubscriberAction")
@Scope(ScopeType.CONVERSATION)
public class ItemSubscriberAction implements Serializable {

	private static final long serialVersionUID = 965755125849259771L;

	@Logger
	Log log;

	@In
	FacesMessages facesMessages;

	@In(create = true)
	Renderer renderer;

	@In(required = false)
	Actor actor;

	@In(required = false)
	Item itemDetails;

	@In(required = false)
	@Out(required = false)
	ItemSubscriber itemSubscriber;

	@In
	protected EntityManager dvdb;

	public String cleanUpSubscribers() {
		dvdb.createQuery(
				"delete from ItemSubscriber ais where ais.dateOfExpiration <= now()")
				.executeUpdate();
		return "ok";
	}

	@Restrict(value = "#{identity.loggedIn}")
	public void subscribe() {
		dvdb.persist(itemSubscriber);
		// facesMessages
		// .addFromResourceBundle("shopping.subscription.action.subscribe.success");

	}

	@Restrict(value = "#{identity.loggedIn}")
	public void unsubscribe() {
		if (itemSubscriber == null || itemSubscriber.getId() == null)
			return;
		dvdb.remove(dvdb.find(ItemSubscriber.class, itemSubscriber.getId()));
		create();
		// facesMessages
		// .addFromResourceBundle("shopping.subscription.action.unsubscribe.success");
	}

	@Restrict(value = "#{identity.loggedIn}")
	public void updateSubscription() {
		if (itemSubscriber == null || itemSubscriber.getId() == null)
			return;
		dvdb.merge(itemSubscriber);
		// facesMessages
		// .addFromResourceBundle("shopping.subscription.action.update.success");
	}

	@Begin(join = true)
	@Factory("itemSubscriber")
	public void create() {
		ItemSubscriber sub = getitemSubscriber();
		if (sub != null)
			itemSubscriber = sub;
		else {
			itemSubscriber = new ItemSubscriber();
			itemSubscriber.setItem(itemDetails);
			itemSubscriber.setUser(actor.getUser());
			itemSubscriber.setCommunicateStorePriceUpdates(true);
			if (itemDetails.getBestOnlinePrice() != null)
				itemSubscriber.setLastCommunicatedPrice(itemDetails
						.getBestOnlinePrice().getPrice());
		}
	}

	@SuppressWarnings("unchecked")
	@Observer("newBestPrice")
	public void communicateNewBestPrice(Price price) {

		List<ItemSubscriber> subscribers = dvdb
				.createQuery(
						"from ItemSubscriber ais where ais.item = :item and ais.lastCommunicatedPrice > :newPrice and ais.dateOfExpiration > now()")
				.setParameter("item", price.getItem()).setParameter("newPrice",
						price.getPrice()).getResultList();

		for (ItemSubscriber itemSubscriber : subscribers) {

			// skip notification if not live
			// if (!(new UrlController().getServername().equals("www.dvdb.de")))
			// continue;

			// Skip mail if subscriber don't want local prices
			if (!itemSubscriber.getCommunicateStorePriceUpdates()
					&& (price.getShop() == null || price.getShop().getStore()))
				continue;
			try {
				Contexts.getConversationContext().set("newBestPrice", price);
				Contexts.getConversationContext().set("itemSubscriberEmail",
						itemSubscriber);
				log.info("Communicating price #0 to subscriber #1", price,
						this.itemSubscriber);
				renderer.render("/WEB-INF/mail/shopping/communicateNewBestPrice_de.xhtml");
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			} finally {
				itemSubscriber.setLastCommunicatedPrice(price.getPrice());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private ItemSubscriber getitemSubscriber() {
		List<ItemSubscriber> list = dvdb
				.createQuery(
						"from ItemSubscriber ais where ais.user = :user and ais.item = :item")
				.setParameter("user", actor.getUser()).setParameter("item",
						itemDetails).getResultList();

		if (list.size() == 1)
			return list.get(0);

		return null;
	}
}
