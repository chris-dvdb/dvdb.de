package de.dvdb.web.mediabase.admin.action;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.AccessLevelEnum;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.user.User;
import de.dvdb.web.Actor;
import de.dvdb.web.item.action.DVDSearchForm;

@Name("mediabaseAdminAction")
@Scope(ScopeType.CONVERSATION)
public class MediabaseAdminAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	@In
	Actor actor;

	@In(required = false)
	DVDSearchForm searchForm;

	@In
	FacesMessages facesMessages;

	public void find() {
		log.info("Searching for " + searchForm.getKeyword1());
	}

	public void updateMediabaseConfiguration() {

		Mediabase m = actor.getUser().getMediabase();
		if (m.getAccessLevel().equals(AccessLevelEnum.PASSWORD)
				&& (m.getMediabasePassword() == null || m
						.getMediabasePassword().equals(""))) {

			facesMessages.addFromResourceBundle(Severity.ERROR,
					"manage.configure.mediabasePassword.required",
					"mediabasePassword");
			actor.setUser(dvdb.find(User.class, actor.getUser().getId()));
			return;
		}

		dvdb.merge(m);

		facesMessages.addFromResourceBundle(Severity.INFO,
				"general.update.success");

	}

	public void updateMediabaseStats() {

		Mediabase m = actor.getUser().getMediabase();
		m.setNumberBorrowed(((Long) dvdb
				.createQuery(
						"select count(*) from MediabaseItemCollectible mi where mi.mediabase = :mediabase and mi.status = 3")
				.setParameter("mediabase", m).getSingleResult()).intValue());
		m.setNumberCollectibles(((Long) dvdb
				.createQuery(
						"select count(*) from MediabaseItemCollectible mi where mi.mediabase = :mediabase")
				.setParameter("mediabase", m).getSingleResult()).intValue());
		m.setNumberWishes(((Long) dvdb
				.createQuery(
						"select count(*) from MediabaseItemWish mi where mi.mediabase = :mediabase")
				.setParameter("mediabase", m).getSingleResult()).intValue());
		m.setNumberOrdered(((Long) dvdb
				.createQuery(
						"select count(*) from MediabaseItemCollectible mi where mi.mediabase = :mediabase and mi.status = 2")
				.setParameter("mediabase", m).getSingleResult()).intValue());

		log.debug("Updated mediabase stats for user " + actor.getUser());
		dvdb.merge(m);

	}

}
