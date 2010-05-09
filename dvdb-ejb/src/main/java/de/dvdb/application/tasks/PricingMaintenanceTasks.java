package de.dvdb.application.tasks;

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
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.item.ItemRepository;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.pricing.PriceManager;
import de.dvdb.domain.shared.DateTimeQueryHelper;

@Name("pricingMaintenanceTasks")
@AutoCreate
@Transactional
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
	PricingMaintenanceTasks pricingMaintenanceTasks;

	@In
	TaskMonitor taskMonitor;

	@Asynchronous
	public void cleanUpExpiredPrices(@Expiration Date date,
			@IntervalDuration Long interval) {
		cleanUpExpiredPrices();
	}

	public void maintainPrices() {
		while (applicationSettings.getRefreshPricesActive()) {
			pricingMaintenanceTasks.refreshItemPrices(null);
			taskMonitor.reportRunning(ApplicationSettings.TASK_REFRESHPRICES);
			try {
				Thread.sleep(10000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void refreshItemPrices(Item item) {
		if (item == null) {
			item = getItemForPriceUpdate();
			priceManager.updatePrices(item, true);
		}
	}

	@SuppressWarnings("unchecked")
	private Item getItemForPriceUpdate() {
		Item oldi = null;
		List<Item> oldestItem = dvdb.createQuery(
				"from PalaceDVDItem ai order by ai.dateOfLastUpdateCheck asc")
				.setMaxResults(1).getResultList();
		if (oldestItem.size() > 0) {
			oldi = oldestItem.get(0);
			oldi.setDateOfLastUpdateCheck(new Date());
			dvdb.merge(oldi);
			dvdb.flush();
		}
		return oldi;
	}

	@RequestParameter
	String priceDataUrl;

	@RequestParameter
	Boolean dropOldData;

	@RequestParameter
	String ean;

	@RequestParameter
	String searchIndex;

	public void cleanUpExpiredPrices() {
		priceManager.cleanUpExpiredPrices();
	}

}
