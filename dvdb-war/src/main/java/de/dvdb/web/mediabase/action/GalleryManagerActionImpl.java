package de.dvdb.web.mediabase.action;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.swing.ImageIcon;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.mediabase.MediabaseImage;
import de.dvdb.web.Actor;

@Name("galleryManagerAction")
public class GalleryManagerActionImpl implements Serializable {

	private static final long serialVersionUID = -2046086599526263064L;

	@Logger
	Log log;

	@In
	FacesContext facesContext;

	@In(required = false)
	Actor actor;

	@In
	ApplicationSettings applicationSettings;

	String imageContentType;

	byte[] imageData;

	@In
	EntityManager dvdb;

	@In(required = false)
	Mediabase mediabase;

	@In(required = false)
	MediabaseImage mediabaseImage;

	private Boolean editMode;

	public String getImageContentType() {
		return imageContentType;
	}

	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public Boolean getEditMode() {
		if (editMode == null && !actor.isAnonymous()
				&& actor.getUser().equals(mediabase.getUser()))
			editMode = true;
		else if (editMode == null
				&& (actor.isAnonymous() || !actor.getUser().equals(
						mediabase.getUser())))
			editMode = false;
		return editMode;
	}

	public void setEditMode(Boolean editMode) {
		this.editMode = editMode;
	}

	@Restrict("#{actor.user eq mediabase.user}")
	public void uploadNew() {

		if (imageData != null && imageData.length > 0)
			replaceImage();

		mediabaseImage.setMediabase(mediabase);
		mediabaseImage
				.setImageOrder(((Long) dvdb
						.createQuery(
								"select count(mi) from MediabaseImage mi where mi.mediabase = :mediabase")
						.setParameter("mediabase", mediabase).getSingleResult())
						.intValue());
		dvdb.persist(mediabaseImage);
		Contexts.getConversationContext().remove("mediabaseImage");
		Contexts.getEventContext().remove("mediabaseImage");
		Contexts.getPageContext().remove("mediabaseImage");
		imageData = null;
		imageContentType = null;
		mediabaseImage = null;
		loadImages();
	}

	public void moveUp(MediabaseImage image) {
		int currentPos = image.getImageOrder();
		if (currentPos == 0)
			return;
		image.setImageOrder(image.getImageOrder() - 1);
		MediabaseImage nextImage = images.get(currentPos - 1);
		nextImage.setImageOrder(currentPos);
		dvdb.merge(image);
		dvdb.merge(nextImage);
		loadImages();
	}

	public void moveDown(MediabaseImage image) {
		int currentPos = image.getImageOrder();
		if (currentPos == images.size() - 1)
			return;
		image.setImageOrder(image.getImageOrder() + 1);
		MediabaseImage nextImage = images.get(currentPos + 1);
		nextImage.setImageOrder(currentPos);
		dvdb.merge(image);
		dvdb.merge(nextImage);
		loadImages();
	}

	@Restrict("#{actor.user eq mediabase.user}")
	public void removeImage(MediabaseImage image) {
		dvdb.remove(image);
		images.remove(image);
		new File(buildFullFilename(image.getFilename())).delete();

		int o = 0;
		for (MediabaseImage mi : images) {
			mi.setImageOrder(o);
			o++;
			dvdb.merge(mi);
		}
		loadImages();
	}

	@Restrict("#{s:hasRole('superuser')}")
	public void adminRemoveImage(MediabaseImage image) {
		dvdb.remove(image);
		new File(buildFullFilename(image.getFilename())).delete();

		mediabase = image.getMediabase();
		loadImages();

		int o = 0;
		for (MediabaseImage mi : images) {
			mi.setImageOrder(o);
			o++;
			dvdb.merge(mi);
		}
	}

	@Restrict("#{actor.user eq mediabase.user}")
	public void mergeImage(MediabaseImage image) {
		dvdb.merge(image);
	}

	private void replaceImage() {

		// remove old image
		if (mediabaseImage.getFilename() != null) {
			new File(mediabaseImage.getFilename()).delete();
		}

		// generate new UUID
		String imageFileName = UUID.randomUUID().toString();

		// persist
		File f = new File(buildFullFilename(imageFileName));
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			fos.write(imageData);
			log.info("Created #0", f.getAbsoluteFile());
			mediabaseImage.setFilename(imageFileName);
			mediabaseImage.setImageContentType(getImageContentType());
			mediabaseImage.setImageDate(new Date());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public byte[] getImageData(MediabaseImage image, int width) {
		File f = new File(buildFullFilename(image.getFilename()));
		byte[] fileBArray = new byte[(int) f.length()];

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(f);
			fis.read(fileBArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return resizeImage(fileBArray, image.getImageContentType(), width);
	}

	@SuppressWarnings("unchecked")
	@Factory("images")
	public void loadImages() {
		images = dvdb
				.createQuery(
						"from MediabaseImage mi inner join fetch mi.mediabase where mi.mediabase = :mediabase order by mi.imageOrder")
				.setParameter("mediabase", mediabase).getResultList();
	}

	@DataModel
	List<MediabaseImage> allMediabaseImages;

	@SuppressWarnings("unchecked")
	@Factory("allMediabaseImages")
	public void allMediabaseImages() {
		allMediabaseImages = dvdb
				.createQuery(
						"from MediabaseImage mi inner join fetch mi.mediabase mb inner join fetch mb.user order by mi.imageDate desc")
				.getResultList();
	}

	@DataModel
	List<MediabaseImage> images;

	public static byte[] resizeImage(byte[] imageData, String contentType,
			int width) {
		ImageIcon icon = new ImageIcon(imageData);

		if (icon.getIconWidth() <= width)
			return imageData;

		double ratio = (double) width / icon.getIconWidth();
		int resizedHeight = (int) (icon.getIconHeight() * ratio);

		int imageType = "image/png".equals(contentType) ? BufferedImage.TYPE_INT_ARGB
				: BufferedImage.TYPE_INT_RGB;
		BufferedImage bImg = new BufferedImage(width, resizedHeight, imageType);
		Graphics2D g2d = bImg.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.drawImage(icon.getImage(), 0, 0, width, resizedHeight, null);
		g2d.dispose();

		String formatName = "";
		if ("image/png".equals(contentType))
			formatName = "png";
		else if ("image/jpeg".equals(contentType)
				|| "image/pjpg".equals(contentType)
				|| "image/pjpeg".equals(contentType)
				|| "image/jpg".equals(contentType))
			formatName = "jpeg";

		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try {
			ImageIO.write(bImg, formatName, baos);
			return baos.toByteArray();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private String buildFullFilename(String filename) {
		return applicationSettings.getMoviebaseImageDir() + filename;
	}

}
