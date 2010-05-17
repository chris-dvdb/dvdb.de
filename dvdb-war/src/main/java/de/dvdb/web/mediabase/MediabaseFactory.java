package de.dvdb.web.mediabase;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.mediabase.AccessLevelEnum;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.mediabase.MediabaseService;
import de.dvdb.web.Actor;

@Name("mediabaseFactory")
@Scope(ScopeType.PAGE)
public class MediabaseFactory implements Serializable {

	private static final long serialVersionUID = 7639296623706763277L;

	String username;

	@Out(required = false)
	Mediabase mediabase;

	@In(create = true)
	MediabaseService mediabaseService;

	@In
	Actor actor;

	@In
	FacesContext facesContext;

	@In(required = false)
	FacesMessages facesMessages;

	@Logger
	Log log;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Mediabase getMediabase() throws MediabaseNotFoundException {
		if (username == null && !actor.isAnonymous())
			username = actor.getUser().getUsername();

		if (mediabase == null && username != null) {
			mediabase = mediabaseService.getMediabase(username);
		}

		if (mediabase == null) {
			throw new MediabaseNotFoundException(username);
		}

		return mediabase;
	}

	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String sendPassword() {

		if (getMediabase().getMediabasePassword() != null
				&& getMediabase().getMediabasePassword().equals(password)
				&& (getMediabase().getMediabasePassword18() == null || !getMediabase()
						.getMediabasePassword18().equals(password))) {
			actor.addMediabaseToAccessList(getMediabase().getUser()
					.getUsername(), false);
		} else if (getMediabase().getMediabasePassword18() != null
				&& getMediabase().getMediabasePassword18().equals(password)) {
			actor.addMediabaseToAccessList(getMediabase().getUser()
					.getUsername(), true);
		} else {
			facesMessages.addFromResourceBundle(Severity.INFO,
					"mb.action.result.wrongPassword");
			return null;

		}

		return "/mb/index.xhtml";
	}

	private Boolean onPWPage;

	private String onlyUsersCheck() {
		if (getMediabase().getAccessLevel().equals(AccessLevelEnum.USER)) {
			if (actor.isAnonymous())
				return "/login.xhtml";
		}
		return null;
	}

	private String onlyOwnerCheck() {

		if (getMediabase().getAccessLevel().equals(AccessLevelEnum.MYSELF)) {
			if (actor.isAnonymous())
				return "/login.xhtml";

			if (!actor.isAnonymous()
					&& !actor.getUser().getUsername().equalsIgnoreCase(
							getMediabase().getUser().getUsername())) {
				return "/private.xhtml";
			}
		}

		return null;
	}

	public String passwordCheck() {

		String ownerCheckResult = onlyOwnerCheck();
		if (ownerCheckResult != null)
			return ownerCheckResult;

		String userCheckResult = onlyUsersCheck();
		if (userCheckResult != null)
			return userCheckResult;

		// is password based access level active
		if (!getMediabase().getAccessLevel().equals(AccessLevelEnum.PASSWORD))
			return null;

		// is visitor mediabase owner
		if (actor.getUser() != null
				&& actor.getUser().getUsername().equalsIgnoreCase(
						username.trim())) {
			return null;
		}

		if ((getMediabase().getMediabasePassword() != null && !getMediabase()
				.getMediabasePassword().equals(""))
				|| (getMediabase().getMediabasePassword18() != null && !getMediabase()
						.getMediabasePassword18().equals(""))) {
			if (!actor.allowedToSeeMediabase(getMediabase().getUser()
					.getUsername())) {
				HttpServletRequest request = (HttpServletRequest) facesContext
						.getExternalContext().getRequest();
				String servletPath = request.getServletPath();
				onPWPage = (servletPath.indexOf("/mb/password") >= 0);
				if (!onPWPage)
					return "/mb/password.xhtml";
			}
		}

		return null;
	}

	public void setMediabase(Mediabase mediabase) {
		this.mediabase = mediabase;
	}

	private Boolean inMoviebase;

	public boolean getVisitingMoviebase() {
		if (inMoviebase == null) {
			HttpServletRequest request = (HttpServletRequest) facesContext
					.getExternalContext().getRequest();
			String servletPath = request.getServletPath();
			inMoviebase = (servletPath.indexOf("/mb/") >= 0);
		}

		return inMoviebase;
	}
}
