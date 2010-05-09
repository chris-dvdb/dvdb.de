package de.dvdb.application.tasks;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.log.Log;

import de.dvdb.application.ApplicationSettings;

@Name("taskMonitor")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class TaskMonitor implements Serializable {

	private static final long serialVersionUID = 2397643542368662677L;

	@Logger
	Log log;

	@In
	ApplicationSettings applicationSettings;

	@In(create = true)
	ItemMaintenanceTasks itemMaintenanceTasks;

	@In
	PricingMaintenanceTasks pricingMaintenanceTasks;

	@Asynchronous
	public void monitor(@Expiration Date date, @IntervalDuration Long interval) {
		log.info("Running task monitor...");

		if (applicationSettings.getMaintainItemsActive()
				&& !isRunning(ApplicationSettings.TASK_MAINTAINITEMS)) {
			log.warn(ApplicationSettings.TASK_MAINTAINITEMS
					+ " is not running. Starting.");
			itemMaintenanceTasks.maintainItems();
		}

		if (applicationSettings.getRefreshPricesActive()
				&& !isRunning(ApplicationSettings.TASK_REFRESHPRICES)) {
			log.warn(ApplicationSettings.TASK_REFRESHPRICES
					+ " is not running. Starting.");

			pricingMaintenanceTasks.maintainPrices();
		}
	}

	private boolean isRunning(String task) {
		if (!taskStatus.containsKey(task))
			return false;

		// last run more than 5 minutes ago?
		if ((System.currentTimeMillis() - taskStatus.get(task).getTime()) > 1000 * 60 * 10)
			return false;

		return true;
	}

	private Map<String, Date> taskStatus = new HashMap<String, Date>();

	public void reportRunning(String task) {
		taskStatus.put(task, new Date());
	}
}
