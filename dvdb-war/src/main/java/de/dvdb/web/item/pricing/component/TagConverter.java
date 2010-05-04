package de.dvdb.web.item.pricing.component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

import de.dvdb.domain.model.tag.Tag;


@Name("tagConverter")
public class TagConverter implements Converter {

	@Transactional
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
		if (arg2 instanceof Tag) {
			Tag tag = (Tag) arg2;
			return tag.getId().toString();
		} else {
			return null;
		}
	}

	@Transactional
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {
		if (arg2 == null) {
			return null;
		}
		try {
			return ((EntityManager) Component.getInstance("dvdb")).find(
					Tag.class, Long.valueOf(arg2));
		} catch (NumberFormatException e) {
			throw new ConverterException("Cannot find selected tag", e);
		}
	}
}
