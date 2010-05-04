package de.dvdb.web.user;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.user.User;

@Name("userSearchComponent")
@Scope(ScopeType.PAGE)
@AutoCreate
public class UserSearchComponent implements Serializable {

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
						"from User u where u.username like :fragment or u.email = :fragment2 order by u.username")
				.setMaxResults(10).setParameter("fragment", fragment + "%")
				.setParameter("fragment2", fragment).getResultList();

	}

	public User getUser() {
		return (User) dvdb.createQuery(
				"from User u where u.username = :username").setParameter(
				"username", searchString).getSingleResult();
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
}
