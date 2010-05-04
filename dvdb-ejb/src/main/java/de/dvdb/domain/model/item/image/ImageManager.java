package de.dvdb.domain.model.item.image;

import java.io.Serializable;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.item.palace.PalaceDVDItem;

@Name("imageManager")
public class ImageManager implements Serializable {

	private static final long serialVersionUID = -6046128633462602384L;

	@In
	ApplicationSettings applicationSettings;

	@In
	ImageManagerDB imageManagerDB;

	public boolean dvdImageExists(Item item) {
		if (!(item instanceof PalaceDVDItem))
			return true;

		if (!applicationSettings.getCheckImage())
			return true;

		PalaceDVDItem pi = (PalaceDVDItem) item;

		return imageManagerDB.dvdImageExists(pi);
	}

	public ImageSource getDBImage(String clazz, Long id) {
		return imageManagerDB.getDBImage(clazz, id);
	}

}
