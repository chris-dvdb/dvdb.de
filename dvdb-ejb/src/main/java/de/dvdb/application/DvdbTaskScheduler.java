package de.dvdb.application;

import java.io.Serializable;
import java.util.Calendar;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

import de.dvdb.application.tasks.ForumMaintenanceActions;
import de.dvdb.application.tasks.TaskMonitor;
import de.dvdb.domain.model.item.palace.PalaceMaintenanceAction;
import de.dvdb.domain.shared.DateTimeQueryHelper;
import de.dvdb.domain.shared.DvdbGlobals;

@Startup
@Scope(ScopeType.APPLICATION)
@AutoCreate
@Name("dvdbTaskScheduler")
public class DvdbTaskScheduler implements Serializable {

	private static final long serialVersionUID = 3628265648532776840L;

	@In
	PalaceMaintenanceAction palaceMaintenanceAction;

	@In
	ForumMaintenanceActions forumMaintenanceActions;

	@In
	TaskMonitor taskMonitor;

	@In
	ApplicationSettings applicationSettings;

	@In
	VisitorTrackerCleanupTask visitorTrackerCleanupTask;

	@In
	DvdbGlobals dvdbGlobals;

	@In
	DateTimeQueryHelper dateTimeQueryHelper;

	@Create
	public void initScheduler() {

		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, 1);

		// Import neuer DVDs aus der Palace DB (dvdbase)
		palaceMaintenanceAction.importPalaceData(c.getTime(),
				Frequency.EVERY_MINUTE.getInterval() * 30);

		// Loeschen alter Items, wenn DVD in der Palace DB geloescht
		palaceMaintenanceAction.cleanUpDeletedItems(c.getTime(),
				Frequency.DAILY.getInterval() + Frequency.HOURLY.getInterval()
						* 3);

		// einfuegen forum threads
		forumMaintenanceActions.createDVDForumThreads(c.getTime(),
				Frequency.EVERY_MINUTE.getInterval() * 1 / 3);

		// einfuegen forum threads
		dvdbGlobals.populate(c.getTime(),
				Frequency.EVERY_MINUTE.getInterval() * 10);

		visitorTrackerCleanupTask.cleanup(c.getTime(), Frequency.DAILY
				.getInterval());

		// fuelle asin groups mit den letzten 30 amazon tags aus dem forum
		// shoppingMaintenanceActions.updateAsinGroupsWithAmazonTags(new Date(
		// System.currentTimeMillis() + Frequency.HOURLY.getInterval()),
		// Frequency.EVERY_MINUTE.getInterval() * 15);

		// einfuegen relevanter prices bei twitter
		// twitterPostingManager.checkForRelevantPrices(new Date(),
		// Frequency.EVERY_MINUTE.getInterval() * 15);

		// Watcher fuer permanente loop tasks
		taskMonitor.monitor(c.getTime(), Frequency.EVERY_MINUTE.getInterval());

	}

	public enum Frequency {
		ONCE(null), EVERY_SECOND(1000l), EVERY_MINUTE(60 * 1000l), HOURLY(
				60 * 60 * 1000l), DAILY(24 * 60 * 60 * 1000l), WEEKLY(7 * 24
				* 60 * 60 * 1000l);
		Long interval;

		Frequency(Long interval) {
			this.interval = interval;
		}

		public Long getInterval() {
			return interval;
		}
	}

}
