package de.dvdb.web.search;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;

@Name("pageSizeConverter")
@BypassInterceptors
@Converter
public class PageSizeConverter implements javax.faces.convert.Converter {

	@Logger
	private Log log;

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) throws ConverterException {
		log.info("PageSizeConverter.getAsObject called");
		if (value == null || value.trim().length() == 0)
			return null;
		Integer i = null;
		try {
			i = new Integer(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			FacesMessage message = new FacesMessage();
			message.setDetail("Invalid page size [String specified]");
			message.setSummary("The page size has to be a valid number");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(message);
		}
		return i.intValue();
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) throws ConverterException {
		log.info("PageSizeConverter.getAsString called");
		return value + "";
	}
}