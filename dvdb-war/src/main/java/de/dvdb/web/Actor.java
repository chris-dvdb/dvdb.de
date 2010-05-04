package de.dvdb.web;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import de.dvdb.domain.model.user.User;
import de.dvdb.domain.model.user.UserRepository;

@Name("actor")
@Scope(ScopeType.SESSION)
@AutoCreate
public class Actor implements Serializable {

	@Logger
	Log log;

	@In
	UserRepository userRepository;

	@In
	Credentials credentials;

	@In
	Identity identity;

	/**
	 * On login update our actor and set the user
	 */
	@Observer("org.jboss.seam.security.postAuthenticate")
	public void onLogin() {
		User u = userRepository.getUser(identity.getPrincipal().getName());
		setUser(u);
	}

	@Observer("org.jboss.seam.security.loggedOut")
	public void onLogout() {
		setUser(null);
	}

	private static final long serialVersionUID = -5773343435801198408L;

	private User user;

	private Map<String, Boolean> mediabasesAccessList = new HashMap<String, Boolean>();

	private boolean captchaRecognized = false;

	Integer tableLayout = 0;

	Integer itemsOnPage = 25;

	public Integer getItemsOnPage() {
		return itemsOnPage;
	}

	public void setItemsOnPage(Integer itemsOnPage) {
		this.itemsOnPage = itemsOnPage;
	}

	public Integer getTableLayout() {
		return tableLayout;
	}

	public void setTableLayout(Integer tableLayout) {
		this.tableLayout = tableLayout;
	}

	public void addMediabaseToAccessList(String usernameMediabaseOf,
			Boolean allowAdult) {
		this.mediabasesAccessList.put(usernameMediabaseOf, allowAdult);
	}

	public boolean allowedToSeeMediabase(String username) {
		return mediabasesAccessList.containsKey(username);
	}

	public boolean allowedToSee18InMediabase(String username) {
		if (mediabasesAccessList.containsKey(username))
			return mediabasesAccessList.get(username);
		return false;
	}

	public boolean getHuman() {
		if (user != null)
			return true;
		return isCaptchaRecognized();
	}

	public boolean isCaptchaRecognized() {
		return captchaRecognized;
	}

	public void setCaptchaRecognized(boolean captchaRecognized) {
		this.captchaRecognized = captchaRecognized;
	}

	public boolean isAnonymous() {
		return user == null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
