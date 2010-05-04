package de.dvdb.web.mediabase;

import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.exception.HttpError;

@HttpError(errorCode = HttpServletResponse.SC_NOT_FOUND)
public class MediabaseNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -67227062863248090L;

	public MediabaseNotFoundException(String id) {
		super("No mediabase for user " + id);
	}
}
