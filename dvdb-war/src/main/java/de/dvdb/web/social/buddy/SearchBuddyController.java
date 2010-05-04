package de.dvdb.web.social.buddy;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.social.Buddy;
import de.dvdb.domain.model.user.User;
import de.dvdb.web.Actor;

@Name("searchBuddyController")
@Scope(ScopeType.PAGE)
@AutoCreate
public class SearchBuddyController implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	EntityManager dvdb;

	String searchString;

	@In
	Actor actor;

	@SuppressWarnings("unchecked")
	public List<User> suggest(Object fragment) {

		return dvdb
				.createQuery(
						"from Buddy buddy where buddy.name like :fragment and buddy.owner = :actor order by buddy.name ")
				.setMaxResults(10).setParameter("fragment", fragment + "%")
				.setParameter("actor", actor.getUser()).getResultList();

	}

	public void validateExistingBuddy(FacesContext context,
			UIComponent component, Object value) {

		List<Buddy> buddies = dvdb
				.createQuery(
						"from Buddy buddy where buddy.owner = :owner and buddy.name = :name")
				.setParameter("name", (value.toString())).setParameter("owner",
						actor.getUser()).getResultList();
		if (buddies.size() != 1)
			throw new ValidatorException(new FacesMessage("Buddy "
					+ value.toString() + " does not exist."));
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
}
