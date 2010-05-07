package de.dvdb.web.social.conversation;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.item.type.DVDItem;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.social.Newsletter;
import de.dvdb.domain.model.user.User;
import de.dvdb.domain.shared.DateTimeQueryHelper;

@Name("weeklyNewsletter")
public class WeeklyNewsletterAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	@In
	ApplicationSettings applicationSettings;

	@In
	FacesMessages facesMessages;

	@Out
	List<Item> bluRays;

	@Out
	List<Item> dvds;

	@In
	DateTimeQueryHelper dateTimeQueryHelper;

	GregorianCalendar gc = new GregorianCalendar();

	public WeeklyNewsletterAction() {
		gc.setTime(new Date());
	}

	public int getCalendarWeek() {
		return gc.get(GregorianCalendar.WEEK_OF_YEAR);
	}

	public void preview() {
		previewAndSend(false);
	}

	public void send() {
		previewAndSend(true);
	}

	private void previewAndSend(boolean send) {
		
		bluRays = dvdb
				.createQuery(
						"from Item item join fetch item.amazonPrice where item.releaseDate >= :today and item.releaseDate < :nextWeek and item.mediaType = :type and item.amazonPrice != null order by item.numberOfWishes desc")
				.setParameter("today", dateTimeQueryHelper.getToday())
				.setParameter("nextWeek", dateTimeQueryHelper.getOneWeek())
				.setParameter("type", DVDItem.MEDIATYPE_BR).setMaxResults(10)
				.getResultList();

		Collections.sort(bluRays, new Comparator<Item>() {
			public int compare(Item o1, Item o2) {

				if (o1.getReleaseDate().equals(o2.getReleaseDate()))
					return 0;
				if (o1.getReleaseDate().before(o2.getReleaseDate()))
					return -1;
				else
					return 1;
			};
		});

		dvds = dvdb
				.createQuery(
						"from Item item join fetch item.amazonPrice where item.releaseDate >= :today and item.releaseDate < :nextWeek and item.mediaType = :type and item.amazonPrice != null order by item.numberOfWishes desc")
				.setParameter("today", dateTimeQueryHelper.getToday())
				.setParameter("nextWeek", dateTimeQueryHelper.getOneWeek())
				.setParameter("type", DVDItem.MEDIATYPE_DVD).setMaxResults(10)
				.getResultList();

		Collections.sort(dvds, new Comparator<Item>() {
			public int compare(Item o1, Item o2) {

				if (o1.getReleaseDate().equals(o2.getReleaseDate()))
					return 0;
				if (o1.getReleaseDate().before(o2.getReleaseDate()))
					return -1;
				else
					return 1;
			};
		});

		if(!send) return;
		
		Newsletter newsletter = new Newsletter();
		newsletter.setBatchSize(1);
		newsletter.setFromEmail("newsletter@dvdb.de");
		newsletter.setSubject("Top Blu-ray und DVD-Veroeffentlichungen in "
				+ "KW " + getCalendarWeek());

		List<User> users = dvdb.createQuery(
				"from User u where u.newsletter = true").getResultList();

		log.info("Sending newsletter to " + users.size() + " users");

		for (User user : users) {
			if (applicationSettings.isProduction())
				newsletter.setToEmail(user.getEmail());
			else
				newsletter.setToEmail("chris@dvdb.de");

			log.info("Sending newsletter to " + newsletter.getToEmail()
					+ " - production mode " + user.getEmail());

			scheduleSend(newsletter);
		}
	}

	// @Asynchronous
	// public void scheduleSend(@Duration long delay, Newsletter newsletter) {
	// @Asynchronous
	public void scheduleSend(Newsletter newsletter) {
		try {
			Contexts.getEventContext().set("newsletter", newsletter);
			Contexts.getEventContext().set("dvds", dvds);
			Contexts.getEventContext().set("bluRays", bluRays);
			Renderer.instance().render(
					"/WEB-INF/mail/newsletter/releases_de.xhtml");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
