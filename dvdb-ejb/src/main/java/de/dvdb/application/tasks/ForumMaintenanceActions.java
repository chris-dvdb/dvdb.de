package de.dvdb.application.tasks;

import java.util.Date;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;

public interface ForumMaintenanceActions {

	@Asynchronous
	@Transactional
	public void createDVDForumThreads(@Expiration Date date,
			@IntervalDuration Long interval);
	
}
