package de.dvdb.web.user;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.PasswordHash;

import de.dvdb.domain.model.forum.ForumService;
import de.dvdb.domain.model.user.User;
import de.dvdb.domain.model.user.UserRepository;
import de.dvdb.domain.shared.PWDGenerator;
import de.dvdb.web.Actor;

@Name("changePasswordAction")
@Scope(ScopeType.PAGE)
public class ChangePasswordAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	Actor actor;

	@In
	Identity identity;

	@In
	FacesMessages facesMessages;

	@In
	EntityManager dvdb;

	@In
	ForumService forumService;

	@In
	PasswordHash passwordHash;

	@In
	UserRepository userRepository;

	@In
	IdentityManager identityManager;

	@In
	Credentials credentials;

	@In
	Renderer renderer;

	// current password for logged in password change request
	String currentPassword;

	// token as URL parameter for forgot password
	@RequestParameter
	String c;

	// username as URL parameter for forgot password
	@RequestParameter
	String u;

	String email;

	String username;

	@Out
	boolean disableButton = false;

	String newPassword;

	String passwordConfirmation;

	User passwordChangeUser;

	/**
	 * Change password for logged in user.
	 */
	@Restrict(value = "#{identity.loggedIn}")
	public void changePassword() {

		passwordChangeUser = userRepository.getUser(identity.getPrincipal()
				.getName());

		// check current password entered
		boolean success = identityManager.authenticate(passwordChangeUser
				.getUsername(), currentPassword);

		if (!success) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"changePasswordAction.error.wrongCurrentPassword");
			return;
		}

		if (!newPassword.equalsIgnoreCase(passwordConfirmation)) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"changePasswordAction.error.passwordConfirmationWrong");
			return;
		}

		// change password
		userRepository.changePassword(passwordChangeUser, newPassword);
		facesMessages.addFromResourceBundle(Severity.INFO,
				"changePasswordAction.success.passwordChanged");
	}

	/**
	 * Validate token for forgot password action
	 * 
	 * @return
	 */
	public String validateToken() {

		passwordChangeUser = userRepository.getUser(u);

		if (passwordChangeUser == null
				|| passwordChangeUser.getConfirmationCode() == null
				|| c == null
				|| !passwordChangeUser.getConfirmationCode()
						.equalsIgnoreCase(c)) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"changePasswordAction.error.tokenInvalid");
			passwordChangeUser = null;
			return "/index.xhtml";
		}

		facesMessages.addFromResourceBundle(Severity.INFO,
				"changePasswordAction.success.tokenValid");

		passwordChangeUser.setAccountEnabled(true);
		passwordChangeUser.setConfirmationCode(null);

		return null;

	}

	/**
	 * Set new password
	 * 
	 * @return
	 */
	@End
	public String setPassword() {

		if (passwordChangeUser == null) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"changePasswordAction.error.userNotIdentified");
			return "invalid";
		}

		if (!newPassword.equalsIgnoreCase(passwordConfirmation)) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"changePasswordAction.error.passwordConfirmationWrong");
			return "invalid";
		}

		User u = userRepository.getUser(passwordChangeUser.getUsername());
		userRepository.changePassword(u, newPassword);

		facesMessages.addFromResourceBundle(Severity.INFO,
				"changePasswordAction.success.passwordChanged");
		return "success";

	}

	/**
	 * Handle reset password request.
	 * 
	 * @return
	 */
	public String resetPasswordRequest() {

		passwordChangeUser = userRepository.getUser(getUsername());

		if (passwordChangeUser == null
				|| !passwordChangeUser.getEmail().equalsIgnoreCase(getEmail())) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"changePasswordAction.error.userNotIdentified");
			return "invalid";
		}

		passwordChangeUser.setConfirmationCode(new PWDGenerator().generate());

		try {
			renderer.render("/WEB-INF/mail/resetpassword/plain_de.xhtml");
			facesMessages.addFromResourceBundle(Severity.INFO,
					"changePasswordAction.success.emailSent");
			disableButton = true;
		} catch (Exception e) {
			facesMessages.add("Email sending failed:" + e.getMessage());
			e.printStackTrace();
		}
		return "success";
	}

	// --- getters and setters ---------------------

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public User getPasswordChangeUser() {
		return passwordChangeUser;
	}

	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
