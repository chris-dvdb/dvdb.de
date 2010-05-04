package de.dvdb.web.item;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.palace.PalaceDVDItem;
import de.dvdb.domain.model.user.User;

@Name("itemSearchComponent")
@Scope(ScopeType.PAGE)
@AutoCreate
public class ItemSearchComponent implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	String searchString;

	@SuppressWarnings("unchecked")
	public List<User> suggest(Object fragment) {

		return dvdb
				.createQuery(
						"from PalaceDVDItem u where u.title like :fragment order by u.title")
				.setMaxResults(10).setParameter("fragment", fragment + "%")
				.getResultList();

	}

	public PalaceDVDItem getItem() {
		if(searchString==null || "".equals(searchString)) return null;

		return (PalaceDVDItem) dvdb.createQuery(
				"from PalaceDVDItem u where u.id = :id").setParameter("id",
				Long.valueOf(searchString)).getSingleResult();
	}

	@RequestParameter
	String itemId;

	@Create
	public void test() {
		if (itemId != null)
			setSearchString(itemId);
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
}
