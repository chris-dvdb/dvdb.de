package de.dvdb.domain.model.item.image;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import de.dvdb.application.ApplicationSettings;
import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.item.palace.PalaceDVDItem;

@Name("imageManagerDB")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class ImageManagerDB implements Serializable {

	private static final long serialVersionUID = -6046128633462602384L;

	private Map<Long, Boolean> imageAvailableMap = new HashMap<Long, Boolean>();

	@In(create = true)
	EntityManager dvdb;

	@In
	ApplicationSettings applicationSettings;

	public ImageSource getDBImage(String clazz, Long id) {
		Object o = null;
		try {
			o = dvdb.find(Class.forName(clazz), id);
			if (o instanceof ImageSource)
				return (ImageSource) o;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean dvdImageExists(Item item) {
		PalaceDVDItem pi = (PalaceDVDItem) item;

		Boolean exists = imageAvailableMap.get(pi.getDvdId());

		if (exists == null) {
			String filename = applicationSettings.getImageRoot() + "/big/"
					+ pi.getDvdId() + ".jpg";

			exists = new File(filename).exists();
			imageAvailableMap.put(pi.getDvdId(), exists);
		}
		return exists;
	}
}
