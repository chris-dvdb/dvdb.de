package de.dvdb.domain.model.pricing.robot;

import java.io.Serializable;

public class RobotException extends Exception implements Serializable {

	public static String CAUSE_UNSPECIFIC = "exception.dvdb.unspecific";

	private static final long serialVersionUID = 50943361953811896L;

	public RobotException() {
	}

	public RobotException(Throwable throwable) {
		super(throwable);
	}

	public RobotException(String message) {
		super(message);
	}

	public RobotException(String message, Throwable throwable) {
		super(message, throwable);
	}

	// protected static Throwable findRootCause(Throwable th) {
	// if (th != null) {
	// // Lets reflectively get any JMX or EJB exception causes.
	// try {
	// Throwable targetException = null;
	// // java.lang.reflect.InvocationTargetException
	// // or javax.management.ReflectionException
	// String exceptionProperty = "targetException";
	// if (PropertyUtils.isReadable(th, exceptionProperty)) {
	// targetException = (Throwable) PropertyUtils.getProperty(th,
	// exceptionProperty);
	// } else {
	// exceptionProperty = "causedByException";
	// // javax.ejb.EJBException
	// if (PropertyUtils.isReadable(th, exceptionProperty)) {
	// targetException = (Throwable) PropertyUtils
	// .getProperty(th, exceptionProperty);
	// }
	// }
	// if (targetException != null) {
	// th = targetException;
	// }
	// } catch (Exception ex) {
	// // just print the exception and continue
	// ex.printStackTrace();
	// }
	//
	// if (th.getCause() != null) {
	// th = th.getCause();
	// th = findRootCause(th);
	// }
	// }
	// return th;
	// }
}
