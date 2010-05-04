package de.dvdb.web.item.action;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import de.dvdb.domain.model.tag.GenreTag;

@Name("dvdGenreTagConverter")
@Converter
@BypassInterceptors
public class GenreTagConverter implements javax.faces.convert.Converter {

	@In(create = true, value = "dvdb")
	protected EntityManager em;

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) throws ConverterException {

		if (value == null || value.trim().length() == 0)
			return null;

		return (GenreTag) em.find(GenreTag.class, new Long(value));

	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) throws ConverterException {

		return ((GenreTag) value).getId().toString();
	}
}