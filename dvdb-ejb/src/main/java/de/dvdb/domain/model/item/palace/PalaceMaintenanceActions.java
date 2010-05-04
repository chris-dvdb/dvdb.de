package de.dvdb.domain.model.item.palace;

import java.util.Date;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;

public interface PalaceMaintenanceActions {

	public abstract String importPalaceData();

	public String cleanUpDeletedItems();

	public String importDVD();

	@Asynchronous
	@Transactional
	public void importPalaceData(@Expiration
	Date date, @IntervalDuration
	Long interval);

	@Asynchronous
	@Transactional
	public void cleanUpDeletedItems(@Expiration
	Date date, @IntervalDuration
	Long interval);
}
