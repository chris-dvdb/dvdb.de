package de.dvdb.domain.model.pricing;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.application.tasks.TaskMonitor;
import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.item.ItemRepository;
import de.dvdb.domain.shared.DateTimeQueryHelper;

@Name("pricingMaintenanceTasks")
public class PricingMaintenanceTasksImpl implements Serializable {

	private static final long serialVersionUID = -2222102720904149239L;

	@Logger
	Log log;

	@In(create = true)
	PriceManager priceManager;

	@In(create = true)
	ItemRepository itemService;

	@In
	EntityManager entityManager;

	@In(create = true)
	DateTimeQueryHelper dateTimeQueryHelper;

	@In
	ApplicationSettings applicationSettings;

	@In
	TaskMonitor taskMonitor;

	@Asynchronous
	public void cleanUpExpiredPrices(@Expiration Date date,
			@IntervalDuration Long interval) {
		cleanUpExpiredPrices();
	}

	@Asynchronous
	public void maintainPrices() {
		while (applicationSettings.getRefreshPricesActive()) {
			refreshItemPrices(null);
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
		List<Item> oldestItem = entityManager.createQuery(
				"from PalaceDVDItem ai order by ai.dateOfLastUpdateCheck asc")
				.setMaxResults(1).getResultList();
		if (oldestItem.size() > 0) {
			oldi = oldestItem.get(0);
			oldi.setDateOfLastUpdateCheck(new Date());
			entityManager.merge(oldi);
			entityManager.flush();
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
