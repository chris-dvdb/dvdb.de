package de.dvdb.application.tasks;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;

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
import de.dvdb.domain.model.item.ItemRepository;
import de.dvdb.domain.model.pricing.PriceManager;
import de.dvdb.domain.shared.DateTimeQueryHelper;

@Name("pricingMaintenanceTasks")
@AutoCreate
public class PricingMaintenanceTasks implements Serializable {

	private static final long serialVersionUID = -2222102720904149239L;

	@Logger
	Log log;

	@In
	PriceManager priceManager;

	@In
	ItemRepository itemRepository;

	@In
	EntityManager dvdb;

	@In
	DateTimeQueryHelper dateTimeQueryHelper;

	@In
	ApplicationSettings applicationSettings;

	@In
	TaskMonitor taskMonitor;

	@Asynchronous
	public void cleanUpExpiredPrices(@Expiration Date date,
			@IntervalDuration Long interval) {
		priceManager.cleanUpExpiredPrices();
	}

	public void maintainPrices() {
//		while (applicationSettings.getRefreshPricesActive()) {
			priceManager.refreshItemPrices(null);
			taskMonitor.reportRunning(ApplicationSettings.TASK_REFRESHPRICES);
//		}
	}

}
