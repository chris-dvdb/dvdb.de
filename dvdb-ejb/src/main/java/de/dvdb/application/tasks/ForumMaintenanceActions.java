package de.dvdb.application.tasks;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.palace.PalaceDVDItem;

@Name("forumMaintenanceActions")
@AutoCreate
public class ForumMaintenanceActions implements
		Serializable {

	private static final long serialVersionUID = 2397643542368662677L;

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	@In
	EntityManager forum;

	@In
	ForumTasks forumTasks;

	@SuppressWarnings("unchecked")
	public void createDVDForumThreads(@Expiration Date date,
			@IntervalDuration Long interval) {

		List<PalaceDVDItem> items = dvdb.createQuery(
				"from PalaceDVDItem item where forumThreadId is null")
				.setMaxResults(1).getResultList();

		if (items.size() == 0)
			return;

		PalaceDVDItem item = null;
		if (items.size() == 1)
			item = items.get(0);

		if (item == null)
			return;

		try {
			Integer i = forumTasks.createPalaceDVDPosting(item);
			if (i != null) {
				log.info("Created forum post with id " + i + " for item "
						+ item.getTitleWithMediatype() + ", ID " + item.getId());
				item.setForumThreadId(new Long(i));
				dvdb.merge(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}

		log.info("Creating dvd forum thread");
	}

}
