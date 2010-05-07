package de.dvdb.application.tasks;

import java.io.Serializable;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.item.ItemRepository;

/**
 * TOOD: get rid of this after launch - items don't need maintainence.
 * 
 * @author Chris
 * 
 */
@Name("itemMaintenanceTasks")
public class ItemMaintenanceTasksImpl implements Serializable {

	private static final long serialVersionUID = -2222102720904149239L;

	@In
	ItemRepository itemRepository;

	@In
	ApplicationSettings applicationSettings;

	@In
	TaskMonitor taskMonitor;

	public void maintainItems() {
		while (applicationSettings.getMaintainItemsActive()) {
			itemRepository.maintainItemSync(null);
			taskMonitor.reportRunning(ApplicationSettings.TASK_MAINTAINITEMS);
			// try {
			// Thread.sleep(2000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
		}
	}
}
