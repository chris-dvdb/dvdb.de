package de.dvdb.domain.model.item.palace;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;


@Stateless
@Local(PalaceMaintenanceActions.class)
@Name("palaceMaintenanceActions")
public class PalaceMaintenanceActionsImpl implements PalaceMaintenanceActions,
		Serializable {

	private static final long serialVersionUID = 2397643542368662677L;

	private static final int DEFAULT_BATCHSIZE = 500;

	@Logger
	Log log;

	@RequestParameter
	Integer batchsize;

	@RequestParameter
	Long dvdId;

	@EJB
	PalaceConverter palaceConverter;

	public String importPalaceData() {
		if (batchsize == null) {
			batchsize = DEFAULT_BATCHSIZE;
		}
		palaceConverter.importUpdatedDVDs(batchsize);
		return "ok";
	}

	public String cleanUpDeletedItems() {
		palaceConverter.cleanUpDeletedItems();
		return "ok";
	}

	public String importDVD() {
		palaceConverter.importSingleDVD(dvdId);
		return "ok";
	}

	public void importPalaceData(@Expiration
	Date date, @IntervalDuration
	Long interval) {
		palaceConverter.importUpdatedDVDs(DEFAULT_BATCHSIZE);
	}

	public void cleanUpDeletedItems(@Expiration
	Date date, @IntervalDuration
	Long interval) {
		palaceConverter.cleanUpDeletedItems();
	}

}
