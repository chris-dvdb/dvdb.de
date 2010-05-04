package de.dvdb.web.mediabase.action;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.mediabase.MediabaseItem;
import de.dvdb.domain.model.mediabase.MediabaseService;
import de.dvdb.web.Actor;
import de.dvdb.web.mediabase.MediabaseFactory;
import de.dvdb.web.search.Page;

@Name("mbMediabaseSearchAction")
public class MbMediabaseSearchAction extends MediabaseItemSearchAction {

	@Out(value = "mbPage", required = false)
	protected Page page;

	@In(required = false, create = true)
	MediabaseFactory mediabaseFactory;

	@In(create = true)
	MediabaseService mediabaseService;

	@In(create = true)
	Actor actor;

	@Logger
	Log log;

	List<MediabaseItem> matches = new ArrayList<MediabaseItem>();

	@Override
	protected Mediabase getMediabase() {
		return mediabaseFactory.getMediabase();
	}

	@Override
	protected void setPage(Page page) {
		this.page = page;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void search() {
		super.search();

		// add actors matches
		if (!actor.isAnonymous() && page.getResults() != null) {
			List items = new ArrayList();

			for (Object object : page.getResults()) {
				MediabaseItem mi = (MediabaseItem) object;
				items.add(mi.getItem().getId());
			}

			matches = em
					.createQuery(
							"from MediabaseItem mi where mi.mediabase = :mediabase and mi.item.id in (:items)")
					.setParameter("items", items).setParameter("mediabase",
							actor.getUser().getMediabase()).getResultList();
			log.info("Found matches " + matches.size());
		}
	}

	public MediabaseItem getMediabaseItemForActor(Item item) {
		for (MediabaseItem mi : matches) {
			if (mi.getItem().equals(item))
				return mi;

		}
		return null;
	}
}
