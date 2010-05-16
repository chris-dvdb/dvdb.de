package de.dvdb.web.mediabase;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import de.dvdb.domain.model.mediabase.MediabaseItemCollectible;
import de.dvdb.domain.model.mediabase.MediabaseItemWish;

@Name("mediabaseItemSearchForm")
@Scope(ScopeType.CONVERSATION)
public class MediabaseItemSearchForm implements Serializable {

	private static final long serialVersionUID = 8907559561751325862L;

	@Out(required = false)
	private String order;

	String mediabaseItemClassName = MediabaseItemCollectible.class.getName();

	Boolean preOrdered;

	Boolean borrowed;

	private Boolean topN;

	public MediabaseItemSearchForm() {
		reset();
	}

	public String getMediabaseItemClassName() {
		return mediabaseItemClassName;
	}

	public void setMediabaseItemClassName(String mediabaseItemClassName) {
		if (mediabaseItemClassName.equals(MediabaseItemCollectible.class
				.getName())
				|| mediabaseItemClassName.equals(MediabaseItemWish.class
						.getName())
				|| mediabaseItemClassName.equals(MediabaseItemWish.class
						.getName())) {
			this.mediabaseItemClassName = mediabaseItemClassName;
		} else if (mediabaseItemClassName.equals("mic") || mediabaseItemClassName.equals("de.dvdb.mediabase.domain.MediabaseItemCollectible")) {
			this.mediabaseItemClassName = MediabaseItemCollectible.class
					.getName();
		} else if (mediabaseItemClassName.equals("miw") || mediabaseItemClassName.equals("de.dvdb.mediabase.domain.MediabaseItemWish")) {
			this.mediabaseItemClassName = MediabaseItemWish.class.getName();
		} else

			throw new RuntimeException("Cannot set mediabaseitemclassname to "
					+ mediabaseItemClassName);

	}

	public Boolean getPreOrdered() {
		return preOrdered;
	}

	public void setPreOrdered(Boolean preOrdered) {
		this.preOrdered = preOrdered;
	}

	public Boolean getBorrowed() {
		return borrowed;
	}

	public void setBorrowed(Boolean borrowed) {
		this.borrowed = borrowed;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Boolean getTopN() {
		return topN;
	}

	public void setTopN(Boolean topN) {
		this.topN = topN;
	}

	public void reset() {
		this.borrowed = false;
		this.preOrdered = false;
		this.topN = false;
		this
				.setMediabaseItemClassName(MediabaseItemCollectible.class
						.getName());
		this.order = null;
	}
}
