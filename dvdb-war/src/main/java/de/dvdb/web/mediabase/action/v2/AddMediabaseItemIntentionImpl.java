package de.dvdb.web.mediabase.action.v2;

import java.io.Serializable;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;

import de.dvdb.domain.model.mediabase.MediabaseItemCollectible;
import de.dvdb.domain.model.mediabase.MediabaseItemWish;

@Name("addMediabaseItemIntention")
public class AddMediabaseItemIntentionImpl implements Serializable {

	@Out
	Class<?> mediabaseItemClass;

	private static final long serialVersionUID = -2046086599526263064L;

	public void addMediabaseItemCollectible() {
		mediabaseItemClass = MediabaseItemCollectible.class;
	}

	public void addMediabaseItemWish() {
		mediabaseItemClass = MediabaseItemWish.class;
	}

}
