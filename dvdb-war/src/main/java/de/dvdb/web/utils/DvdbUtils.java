package de.dvdb.web.utils;

import java.io.Serializable;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.theme.ThemeSelector;

import de.dvdb.domain.shared.StringUtils;

@Name("dvdbUtils")
public class DvdbUtils implements Serializable {

	private static final long serialVersionUID = -2222102720904149239L;

	@In("org.jboss.seam.theme.themeSelector")
	ThemeSelector themeSelector;

	@Logger
	Log log;

	@In("org.jboss.seam.core.resourceBundle")
	ResourceBundle resourceBundle;

	public String messageForDynamicKey(String... key) {
		StringBuilder sb = new StringBuilder();
		for (String string : key) {
			sb.append(string);
		}
		String value = null;

		try {
			value = resourceBundle.getString(sb.toString());
		} catch (MissingResourceException e) {
			return "### " + sb.toString() + " ###";
		}
		return value;
	}

	public String[] splitCSV(String csv) {
		return csv.split(",");
	}

	@In
	FacesMessages facesMessages;

	public Boolean hasNonEmptyMessageSummaries() {
		List<FacesMessage> messages = facesMessages.getCurrentMessages();
		Boolean display = false;
		for (FacesMessage facesMessage : messages) {
			if (!facesMessage.getSummary().equals(""))
				display = true;
		}

		return display;
	}

	public Boolean[] getRatingAsArray(Integer ratingValue) {
		Boolean[] x = new Boolean[10];
		if (ratingValue == null)
			return x;
		for (int i = 0; i < 10; i++) {
			if (ratingValue > i)
				x[i] = true;
			else
				x[i] = false;
		}
		return x;
	}

	public String getMD5(String string) {
		return StringUtils.getMD5(string);
	}
}
