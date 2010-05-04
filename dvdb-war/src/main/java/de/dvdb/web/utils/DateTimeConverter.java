package de.dvdb.web.utils;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("dateTimeConverter")
@Converter
@BypassInterceptors
public class DateTimeConverter implements javax.faces.convert.Converter {

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) throws ConverterException {

		if (value == null || value.trim().length() == 0)
			return null;

		return new Date(Long.parseLong(value));

	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) throws ConverterException {

		return "" + ((Date) value).getTime();
	}
}