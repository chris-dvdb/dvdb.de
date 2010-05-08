package de.dvdb.application;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.shared.DateTimeQueryHelper;

@Name("visitorTrackerCleanupTask")
@AutoCreate
public class VisitorTrackerCleanupTask implements Serializable {

	private static final long serialVersionUID = 7639296623706763277L;

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

			dvdb.createQuery(
					"delete from MediabaseVisit mv where mv.mediabase = :mediabase and mv.dateOfVisit < :yesterday")
					.setParameter("mediabase", mediabase).setParameter(
							"yesterday", dateTimeQueryHelper.getYesterday())
					.executeUpdate();
		}
	}

}
