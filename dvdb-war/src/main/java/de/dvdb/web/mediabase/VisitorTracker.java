package de.dvdb.web.mediabase;

import java.io.Serializable;
import java.util.Date;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
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
