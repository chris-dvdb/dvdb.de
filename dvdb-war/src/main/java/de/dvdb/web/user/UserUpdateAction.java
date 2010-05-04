package de.dvdb.web.user;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.user.User;
import de.dvdb.domain.model.user.UserRepository;
import de.dvdb.web.Actor;

@Name("userUpdateAction")
@Scope(ScopeType.EVENT)
public class UserUpdateAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In(create = true)
	Actor actor;

	@Out(required = false)
	User user;

	@In
	FacesMessages facesMessages;

	@In
	EntityManager dvdb;

	String passwordConfirmation;

	@In
	UserRepository userRepository;

	@Restrict(value = "#{identity.loggedIn}")
	public String updateUser() {

		user = dvdb.merge(user);
		actor.setUser(dvdb.find(User.class, user.getId()));

		facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
				"account.account.action.change.success");

		return null;
	}

	@Factory("user")
	public void initUserCopy() {
		try {
			user = (User) BeanUtils.cloneBean(actor.getUser());
		} catch (Exception e) {
			log.error(e);
		}
	}

	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void unsubscribe() {
		log.info("Unsubscribing user with email " + email);
		dvdb
				.createQuery(
						"update User u set u.newsletter = false where u.email = :email")
				.setParameter("email", email.trim()).executeUpdate();
	}

	public void deleteUser(User u) {

		userRepository.deleteUser(u);

		facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
				"admin.user.action.delete.success");

	}
}
