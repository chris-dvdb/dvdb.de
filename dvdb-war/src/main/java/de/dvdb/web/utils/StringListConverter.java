package de.dvdb.web.utils;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("stringListConverter")
@Converter
@BypassInterceptors
public class StringListConverter implements javax.faces.convert.Converter {

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) throws ConverterException {

		if (value == null || value.trim().length() == 0)
			return null;

		List<String> sl = new ArrayList<String>();
		for (String token : value.split(":")) {
			sl.add(token);
		}

		return sl;

	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) throws ConverterException {

		StringBuilder sb = new StringBuilder();
		for (String token : (List<String>) value) {
			sb.append(token + ":");
		}

		String s = sb.toString();
		return s.substring(0, Math.max(s.length()-1, 0));
	}
}