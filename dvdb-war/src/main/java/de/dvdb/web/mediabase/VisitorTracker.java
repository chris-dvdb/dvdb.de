package de.dvdb.web.mediabase;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.mediabase.MediabaseVisit;
import de.dvdb.domain.shared.DateTimeQueryHelper;
import de.dvdb.web.Actor;

@Name("visitorTracker")
public class VisitorTracker implements Serializable {

	private static final long serialVersionUID = 7639296623706763277L;

	@In(required = false)
	Mediabase mediabase;

	@In(required = false)
	Actor actor;

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	@In
	DateTimeQueryHelper dateTimeQueryHelper;

	@SuppressWarnings("unchecked")
	@Asynchronous
	@Transactional
	public void cleanup(@Expiration Date date, @IntervalDuration Long interval) {

		log.info("Cleaning up visitor tracking data");

		List<Mediabase> visitedMediabases = (List<Mediabase>) dvdb.createQuery(
				"select distinct(mv.mediabase) from MediabaseVisit mv")
				.getResultList();

		for (Mediabase mediabase : visitedMediabases) {
			Long newRecords = (Long) dvdb
					.createQuery(
							"select count(mv) from MediabaseVisit mv where mv.mediabase = :mediabase and mv.dateOfVisit < :yesterday")
					.setParameter("mediabase", mediabase).setParameter(
							"yesterday", dateTimeQueryHelper.getYesterday())
					.getSingleResult();
			Integer visits = mediabase.getTotalVisits();

			if (visits == null)
				visits = 0;
			mediabase.setTotalVisits(visits + newRecords.intValue());

			dvdb
					.createQuery(
							"delete from MediabaseVisit mv where mv.mediabase = :mediabase and mv.dateOfVisit < :yesterday")
					.setParameter("mediabase", mediabase).setParameter(
							"yesterday", dateTimeQueryHelper.getYesterday())
					.executeUpdate();
		}
	}

	public void trackVisitor() {
		log.info("Tracking visit of mediabase of user "
				+ mediabase.getUser().getUsername());

		String sessionId = "";
		Object req = FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();

		if (req instanceof HttpServletRequest) {
			sessionId = ((HttpServletRequest) req).getRequestedSessionId();
		}

		if (sessionId == null || sessionId.equals(""))
			return;

		Long visitCount = 1l;
		if (actor.isAnonymous()) {
			visitCount = (Long) dvdb
					.createQuery(
							"select count(mv) from MediabaseVisit mv where mv.mediabase = :mediabase and mv.visitor is null and mv.sessionId = :sessionid and mv.dateOfVisit >= :yesterday")
					.setParameter("mediabase", mediabase).setParameter(
							"sessionid", sessionId).setParameter("yesterday",
							dateTimeQueryHelper.getYesterday())
					.getSingleResult();
		} else if (!actor.getUser().equals(mediabase.getUser())) {
			visitCount = (Long) dvdb
					.createQuery(
							"select count(mv) from MediabaseVisit mv where mv.mediabase = :mediabase and mv.visitor = :visitor and mv.dateOfVisit >= :yesterday")
					.setParameter("visitor", actor.getUser()).setParameter(
							"mediabase", mediabase).setParameter("yesterday",
							dateTimeQueryHelper.getYesterday())
					.getSingleResult();
		}

		if (visitCount == 0l) {
			MediabaseVisit mv = new MediabaseVisit();
			mv.setDateOfVisit(new Date());
			mv.setVisitor(actor.getUser());
			mv.setMediabase(mediabase);
			mv.setSessionId(sessionId);
			dvdb.persist(mv);
		}
	}
}
