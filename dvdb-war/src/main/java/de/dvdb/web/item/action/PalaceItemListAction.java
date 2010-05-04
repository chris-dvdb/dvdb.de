package de.dvdb.web.item.action;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.palace.PalaceDVDItem;

@Name("palaceItemListAction")
public class PalaceItemListAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	Long firstId;
	Long lastId;

	protected List<PalaceDVDItem> items;

	public List<PalaceDVDItem> getItems() {
		return items;
	}

	public void setItems(List<PalaceDVDItem> items) {
		this.items = items;
	}

	public void list() {

		log.info("Retrieving items from " + firstId + " to " + lastId);

		items = dvdb
				.createQuery(
						"from PalaceDVDItem item where item.dvdId >= :f and item.dvdId <= :l")
				.setParameter("f", firstId).setParameter("l", lastId)
				.getResultList();
	}

	public Long getFirstId() {
		return firstId;
	}

	public void setFirstId(Long firstId) {
		this.firstId = firstId;
	}

	public Long getLastId() {
		return lastId;
	}

	public void setLastId(Long lastId) {
		this.lastId = lastId;
	}

}
