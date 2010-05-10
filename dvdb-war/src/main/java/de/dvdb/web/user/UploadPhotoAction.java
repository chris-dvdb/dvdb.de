package de.dvdb.web.user;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.ui.graphicImage.Image;

import de.dvdb.domain.model.user.User;
import de.dvdb.web.Actor;

@Name("uploadPhotoAction")
@Scope(ScopeType.EVENT)
public class UploadPhotoAction implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	private final static int maxWidth = 64;
	private final static int maxHeight = 64;

	@Logger
	Log log;

	@In
	Actor actor;

	@In
	FacesMessages facesMessages;

	@In
	EntityManager dvdb;

	byte[] imageData;

	String contentType;

	public void uploadPhoto() {
		if (imageData == null || imageData.length == 0)
			return;

		if (contentType.equalsIgnoreCase("image/gif")
				|| contentType.equalsIgnoreCase("image/bmp")) {
			facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_WARN,
					"account.uploadPhoto.action.upload.error.gif");
			return;
		}

		if (imageData.length > 40000) {
			facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_WARN,
					"account.uploadPhoto.action.upload.error.size");
		}

		try {
			Image img = new Image();
			img.setInput(imageData);
			int width = img.getWidth();
			int height = img.getHeight();

			if (height > maxHeight || width > maxWidth) {
				facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_WARN,
						"account.uploadPhoto.action.upload.error.dimensions");
				return;
			}

		} catch (IOException e) {
			log.error(e);
		}

		User u = dvdb.find(User.class, actor.getUser().getId());
		u.setImageData(imageData);
		u.setMimeType(contentType);
		dvdb.merge(u);
		actor.setUser(u);
		facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
				"account.uploadPhoto.action.upload.success");
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void removePhoto() {
		User u = dvdb.find(User.class, actor.getUser().getId());
		u.setImageData(null);
		u.setMimeType(null);
		actor.setUser(u);

		facesMessages.addFromResourceBundle(FacesMessage.SEVERITY_INFO,
				"account.uploadPhoto.action.remove.success");
	}

}
