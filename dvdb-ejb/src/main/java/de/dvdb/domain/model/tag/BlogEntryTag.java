package de.dvdb.domain.model.tag;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.dvdb.domain.model.social.BlogEntry;

@Entity
@DiscriminatorValue("2")
public class BlogEntryTag extends Tag2 implements Serializable {

	private static final long serialVersionUID = 6900370443991366913L;
	private BlogEntry blogEntry;

	@ManyToOne(targetEntity = BlogEntry.class)
	@JoinColumn(name = "blogentry_id")
	public BlogEntry getBlogEntry() {
		return blogEntry;
	}

	public void setBlogEntry(BlogEntry blogEntry) {
		this.blogEntry = blogEntry;
	}

}
