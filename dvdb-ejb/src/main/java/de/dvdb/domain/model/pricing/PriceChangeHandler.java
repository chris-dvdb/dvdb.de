package de.dvdb.domain.model.pricing;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.MediabaseItemWish;
import de.dvdb.domain.model.message.PriceAlertMessage;
import de.dvdb.domain.shared.DvdbGlobals;

@Name("priceChangeHandler")
@Transactional
@AutoCreate
public class PriceChangeHandler implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In(create = true)
	Renderer renderer;

	@In
	EntityManager dvdb;

	@In(create = true)
	PriceManager priceManager;

	@In
	DvdbGlobals dvdbGlobals;

	@SuppressWarnings("unchecked")
	@Observer(value = PriceManager.EVENT_PRICECHANGED)
	public void priceChangeEventHandler(Price price) {

		if (price.getPrice() == null || price.getPrice().equals(0d))
			return;

		// hole alle user, die das item auf wunschliste haben und notification
		// aktiviert haben und deren preisgrenze kleiner gleich dem neuen preis
		// ist
		List<MediabaseItemWish> wishes = dvdb
				.createQuery(
						" select miw from MediabaseItemWish miw left join fetch miw.mediabase mediabase left join fetch mediabase.user user "
								+ " where miw.notify = true and "
								+ " miw.item = :item and "
								+ " miw.maximumPurchasePrice is not null and "
								+ " miw.maximumPurchasePrice > 0 and "
								+ " miw.maximumPurchasePrice >= :newprice and "
								+ " (miw.lastNotificationPrice > :newprice or miw.lastNotificationPrice is null) and "
								+ " user.newsletter = true").setParameter(
						"newprice", price.getPrice()).setParameter("item",
						price.getItem()).getResultList();

		log.info("Found " + wishes.size() + " wishes");

		// send out emails
		for (MediabaseItemWish mediabaseItemWish : wishes) {

			PriceAlertMessage pam = new PriceAlertMessage();
			pam.setPrice(price);
			pam.setRecipient(mediabaseItemWish.getMediabase().getUser());
			pam.setSender(dvdbGlobals.getChris());
			pam.setSubject("Preisgrenze erreicht fuer "
					+ price.getItem().getTitleWithMediatype());
			pam
					.setBody(String
							.format(
									"Hallo %s,\r\n\r\n%s hat Deine Preisgrenze unterschritten. \r\n\r\n Viel Spass beim Sparen",
									mediabaseItemWish.getMediabase().getUser()
											.getUsername(), price.getItem()
											.getTitleWithMediatype()));
			pam.getRecipient().setDateOfLastMessage(pam.getMessageDate());
			dvdb.persist(pam);

			// log.info("Notifying "
			// + mediabaseItemWish.getMediabase().getUser().getUsername()
			// + " about price change.");
			//
			// Contexts.getConversationContext().set("mediabaseItemWish",
			// mediabaseItemWish);
			// Contexts.getConversationContext().set("newPrice", price);
			// renderer
			// .render("/WEB-INF/mail/shopping/communicateNewBestPrice_de.xhtml")
			// ;

		}

		// update notification price
		if (wishes.size() > 0)
			dvdb
					.createQuery(
							"update MediabaseItemWish miw set miw.lastNotificationPrice = :newprice where miw in (:miws)")
					.setParameter("newprice", price.getPrice()).setParameter(
							"miws", wishes).executeUpdate();

	}
}
