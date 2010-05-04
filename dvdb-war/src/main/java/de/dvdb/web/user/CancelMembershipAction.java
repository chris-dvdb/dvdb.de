package de.dvdb.web.user;

import java.io.Serializable;

import javax.faces.application.FacesMessage;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.user.UserRepository;
import de.dvdb.web.Actor;

@Name("cancelMembershipAction")
public class CancelMembershipAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	Actor actor;

	@In
	FacesMessages facesMessages;

	@In
	UserRepository userRepository;

	boolean step1Confirmed = false;

	boolean step2Confirmed = false;

	@Restrict(value = "#{identity.loggedIn}")
	public void confirm() {

		if (!step1Confirmed)
			return;

	}

	@Restrict(value = "#{identity.loggedIn}")
	public String cancelMembership() {

		userRepository.deleteUser(actor.getUser());

		facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
				"account.cancelMembership.action.cancelMembership.success");

		return "/logout.xhtml";
	}

	public boolean isStep1Confirmed() {
		return step1Confirmed;
	}

	public void setStep1Confirmed(boolean step1Confirmed) {
		this.step1Confirmed = step1Confirmed;
	}

	public boolean isStep2Confirmed() {
		return step2Confirmed;
	}

	public void setStep2Confirmed(boolean step2Confirmed) {
		this.step2Confirmed = step2Confirmed;
	}

	@Destroy
	public void destroy() {
	}

}
