package de.dvdb.domain.model.item;

//@HttpError(errorCode = HttpServletResponse.SC_NOT_FOUND)
public class ItemNotFoundException extends Exception {

	private static final long serialVersionUID = -67227062863248090L;

	public ItemNotFoundException(String message) {
		super(message);
	}
}
