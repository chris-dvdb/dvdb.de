package de.dvdb.web.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.theme.ThemeSelector;

import de.dvdb.domain.model.user.User;
import de.dvdb.infrastructure.http.HttpService;

@Name("urlController")
@AutoCreate
public class UrlController implements Serializable {

	private static final long serialVersionUID = -6227299519500898191L;

	@In("org.jboss.seam.theme.themeSelector")
	ThemeSelector themeSelector;

	static Map<String, String> map;

	public String getMoviebaseUrl(User user) {
		return "http://moviebase.dvdb.de/" + user.getUsername();
	}

	public static String getServername() {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		return request.getServerName();
	}

	public String getSN() {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		return request.getServerName();
	}

	public String get(String function) {
		return map.get(getServername() + "/" + function);
	}

	public String getUrlCoverBig() {
		return "http://www.dvdb.de/dvdpix/big/";
	}

	public String getUrlCoverSmall() {
		return "http://www.dvdb.de/dvdpix/small/";
	}

	@Create
	public void init() {
		map = new HashMap<String, String>();

		map.put("localhost/theme", "dvdb3");
		map.put("www.dvdb.de/theme", "dvdb3");
		map.put("dvdpalace.dvdb.de/theme", "palace");
		map.put("beta.dvdb.de/theme", "dvdb3");
	}

	public void checkDomainAndSetTheme() {
		String theme = map.get(getServername() + "/theme");
		if (theme != null) {
			themeSelector.setTheme(theme);
		}
	}

	public String getPalaceHeader() {
		return HttpService
				.retrieveHttpDocument("http://www.dvd-palace.de/skin/header/header.php?menucode=moviebase&closedbase=1&moviebase=1&closeheader=1");
	}

}
