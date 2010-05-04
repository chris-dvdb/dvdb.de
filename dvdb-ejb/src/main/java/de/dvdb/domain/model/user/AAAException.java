package de.dvdb.domain.model.user;

import java.io.Serializable;

import javax.ejb.ApplicationException;

import de.dvdb.application.DvdbException;

@ApplicationException(rollback = true)
public class AAAException extends DvdbException implements Serializable {

	private static final long serialVersionUID = 50943361953811896L;

	public static String CAUSE_SESSION_EXPIRED = "exception.aaa.sessionExpired";

	public static String CAUSE_NAME_EXISTS = "exception.aaa.nameExists";

	public static String CAUSE_UNABLE_TO_IDENTIFY = "exception.aaa.unableToIdentify";

	public static String CAUSE_UNSPECIFIC = "exception.aaa.unspecific";

	public static String CAUSE_INVALIDCREDENTILS = "exception.aaa.wrongCredentials";

	public AAAException() {
	}

	public AAAException(Throwable throwable) {
		super(throwable);
	}

	public AAAException(String message) {
		super(message);
	}

	public AAAException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
