package de.dvdb.application;

import java.io.Serializable;
import java.util.Date;

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
import de.dvdb.domain.shared.DvdbGlobals;

@Startup
@Scope(ScopeType.APPLICATION)
@AutoCreate
@Name("dvdbTaskScheduler")
public class DvdbTaskScheduler implements Serializable {

	private static final long serialVersionUID = 3628265648532776840L;

	@In
	PalaceMaintenanceAction palaceMaintenanceAction;

	// @In(create = true)
	// AmazonMaintenanceActions amazonMaintenanceActions;

	@In(create = true)
	ForumMaintenanceActions forumMaintenanceActions;

	@In(create = true)
	TaskMonitor taskMonitor;

	@In
	ApplicationSettings applicationSettings;

	// @In(create = true)
	// VisitorTracker visitorTracker;

	@In
	DvdbGlobals dvdbGlobals;

	@Create
	public void initScheduler() {

		// Import neuer DVDs aus der Palace DB (dvdbase)
		palaceMaintenanceAction.importPalaceData(new Date(),
				Frequency.EVERY_MINUTE.getInterval() * 30);

		// Loeschen alter Items, wenn DVD in der Palace DB geloescht
		palaceMaintenanceAction.cleanUpDeletedItems(new Date(), Frequency.DAILY
				.getInterval()
				+ Frequency.HOURLY.getInterval() * 3);

		// einfuegen forum threads
		forumMaintenanceActions.createDVDForumThreads(new Date(),
				Frequency.EVERY_MINUTE.getInterval() * 1 / 3);

		// einfuegen forum threads
		dvdbGlobals.populate(new Date(),
				Frequency.EVERY_MINUTE.getInterval() * 10);

		// TODO: uncomment
		// visitorTracker.cleanup(new Date(), Frequency.DAILY.getInterval());

		// fuelle asin groups mit den letzten 30 amazon tags aus dem forum
		// shoppingMaintenanceActions.updateAsinGroupsWithAmazonTags(new Date(
		// System.currentTimeMillis() + Frequency.HOURLY.getInterval()),
		// Frequency.EVERY_MINUTE.getInterval() * 15);

		// einfuegen relevanter prices bei twitter
		// twitterPostingManager.checkForRelevantPrices(new Date(),
		// Frequency.EVERY_MINUTE.getInterval() * 15);

		// Read Amazon Top RSS Feeds and import the asins

		// if (applicationSettings.getImportAmazonTopFeeds()) {
		// amazonMaintenanceActions.importAmazonTopFeeds(new Date(),
		// Frequency.DAILY.getInterval());
		// }

		// Watcher fuer permanente loop tasks
		taskMonitor.monitor(new Date(), Frequency.EVERY_MINUTE.getInterval());

		// Loeschen abgelaufener Preismeldungen
		// shoppingMaintenanceTasks.cleanUpExpiredPrices(new Date(),
		// Frequency.DAILY.getInterval() + Frequency.HOURLY.getInterval()
		// * 2);

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
