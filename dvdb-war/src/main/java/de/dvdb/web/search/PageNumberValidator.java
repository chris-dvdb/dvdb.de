package de.dvdb.web.search;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Validator;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;

@Name("pageNumberValidator")
@BypassInterceptors
@Validator
public class PageNumberValidator implements javax.faces.validator.Validator {

  @Logger
  private Log log;
  public void validate(FacesContext context, UIComponent component,
          Object value) throws ValidatorException {
      log.info("PageNumberValidator.validate called");
      try {
          Integer pageNumber = Integer.parseInt(value.toString());
          if (pageNumber < 1) {
              FacesMessage message = new FacesMessage();
              message.setDetail("Invalid page number [less than 1]");
              message.setSummary("The page number cannot be "+pageNumber);
              message.setSeverity(FacesMessage.SEVERITY_ERROR);
              throw new ValidatorException(message);
          }
      } catch (NumberFormatException e) {
          e.printStackTrace();
          FacesMessage message = new FacesMessage();
          message.setDetail("Invalid or missing page number");
          message.setSummary("The page number has to be a valid number");
          message.setSeverity(FacesMessage.SEVERITY_ERROR);
          throw new ConverterException(message);
      }
  }
}