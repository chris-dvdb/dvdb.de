package de.dvdb.web.user;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaResponse;

import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.JpaIdentityStore;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.forum.ForumService;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.mediabase.Skin;
import de.dvdb.domain.model.user.User;
import de.dvdb.domain.model.user.UserRepository;
import de.dvdb.domain.shared.PWDGenerator;
import de.dvdb.infrastructure.persistence.EntityMetadata;
import de.dvdb.web.Actor;

@Name("signUpAction")
@Scope(ScopeType.CONVERSATION)
public class SignUpAction implements Serializable {

	private static final String letDig = "[a-zA-Z0-9]";

	public static final Pattern VALID_PATTERN = Pattern.compile("^" + letDig
			+ "*$");

	private static final long serialVersionUID = -2046086599526263064L;

	String username;
	String email;
	String emailConfirmation;
	String password;
	String passwordConfirmation;

	@Logger
	Log log;

	@In
	ForumService forumService;

	@In
	FacesMessages facesMessages;

	@In
	IdentityManager identityManager;

	@In
	EntityManager dvdb;

	@In
	Actor actor;

	@In
	UserRepository userRepository;

	@In
	SignUpAction signUpAction;

	@In
	Renderer renderer;

	@In
	ApplicationSettings applicationSettings;

	public String signUp() {

		boolean error = false;

		// get captcha field
		if (!actor.getHuman()) {
			HttpServletRequest req = (HttpServletRequest) (FacesContext
					.getCurrentInstance().getExternalContext().getRequest());
			String remoteip = req.getRemoteAddr();

			String challenge = (String) FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap().get(
							"recaptcha_challenge_field");

			String response = (String) FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap().get(
							"recaptcha_response_field");

			if (applicationSettings.isProduction()) {
				ReCaptcha re = ReCaptchaFactory.newReCaptcha(
						applicationSettings.getRecaptchaPublicKey(),
						applicationSettings.getRecaptchaPrivateKey(), true);
				ReCaptchaResponse reRe = re.checkAnswer(remoteip, challenge,
						response);

				if (!reRe.isValid()) {
					error = true;
					facesMessages.addFromResourceBundle(reRe.getErrorMessage());
				} else {
					actor.setCaptchaRecognized(true);
				}
			}
		}

		if (userRepository.getUser(username) != null) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"signUpAction.error.usernameTaken");
			setUsername("");
			error = true;
		}

		if (!email.trim().equalsIgnoreCase(emailConfirmation.trim())) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"signUpAction.error.emailMismatch");
			emailConfirmation = null;
			error = true;
		}

		if (!password.equals(passwordConfirmation)) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"signUpAction.error.passwordMismatch");
			passwordConfirmation = null;
			password = null;
			error = true;
		}

		if (!VALID_PATTERN.matcher(getUsername()).matches()) {
			facesMessages.addFromResourceBundle(Severity.ERROR,
					"signUpAction.error.usernameInvalid");
			setUsername("");
			error = true;
		}

		if (error) {
			return "invalid";
		}

		new RunAsOperation() {
			public void execute() {
				identityManager.createUser(getUsername(), getPassword());
			}
		}.addRole("admin").run();

		facesMessages.addFromResourceBundle(Severity.INFO,
				"signUpAction.success");

		Conversation.instance().end();
		return "success";

	}

	@Observer(JpaIdentityStore.EVENT_PRE_PERSIST_USER)
	public void prePersist(User user) {
		Mediabase mb = new Mediabase();
		mb.setUser(user);

		mb.setSkin(dvdb.find(Skin.class, 1l));
		user.setDateOfLastSignIn(new Date());
		user.setNumberCollectibles(0);
		user.setMediabase(mb);
		user.setAdult(false);
		user.setEmail(getEmail());
		user.setPassword(password);
		user.setConfirmationCode(new PWDGenerator().generate());
		user.setAccountEnabled(false);
		EntityMetadata em = new EntityMetadata();
		em.setDateOfCreation(new Date());
		em.setDateOfLastUpdate(new Date());
		user.setEntityMetadata(em);
	}

	@Observer(JpaIdentityStore.EVENT_USER_CREATED)
	public void userCreated(User user) {
		signUpAction.createForumAccount(user,
				((HttpServletRequest) FacesContext.getCurrentInstance()
						.getExternalContext().getRequest()).getRemoteAddr());
		signUpAction.sendSignUpMail(user);

	}

	@Email
	@NotEmpty
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Email
	@NotEmpty
	public String getEmailConfirmation() {
		return emailConfirmation;
	}

	public void setEmailConfirmation(String emailConfirmation) {
		this.emailConfirmation = emailConfirmation;
	}

	@Length(min = 3, max = 100)
	@NotEmpty
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@NotEmpty
	public String getPasswordConfirmation() {
		return passwordConfirmation;
	}

	public void setPasswordConfirmation(String passwordConfirmation) {
		this.passwordConfirmation = passwordConfirmation;
	}

	@Length(min = 3, max = 100, message = "#{messages['signUp.error.usernameLength']}")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void createForumAccount(User newUser, String ip) {
		forumService.registerUser(newUser, ip);
	}

	@RequestParameter
	String c;

	@RequestParameter
	String u;

	public String unlock() {

		User user = userRepository.getUser(u);

		if (user == null || !user.getConfirmationCode().equalsIgnoreCase(c)) {
			facesMessages
					.addFromResourceBundle("signUpAction.error.invalidToken");
			return "invalid";
		}

		if (user.getAccountEnabled()) {
			facesMessages
					.addFromResourceBundle("signUpAction.error.alreadyUnlocked");
			return "invalid";
		}

		user.setAccountEnabled(true);
		facesMessages.addFromResourceBundle("signUpAction.success.unlocked");
		return "success";
	}

	@Asynchronous
	public void sendSignUpMail(User newUser) {
		log.info("Sending welcome mail to " + newUser.getUsername());

		try {
			Contexts.getConversationContext().set("newUser", newUser);
			renderer.render("/WEB-INF/mail/signup/signup_de.xhtml");
		} catch (Exception e) {
			facesMessages.add("Email sending failed:" + e.getMessage());
			e.printStackTrace();
		}
	}
}
