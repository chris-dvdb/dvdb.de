package de.dvdb.domain.model.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import de.dvdb.domain.model.social.BlogEntry;


@Entity
@DiscriminatorValue("3")
public class BlogEntryComment extends TextMessage {

	private static final long serialVersionUID = -5040609100935760841L;

	private BlogEntry blogEntry;

	@javax.persistence.ManyToOne(optional = false)
	@javax.persistence.JoinColumn(name = "blogentry_id")
	public BlogEntry getBlogEntry() {
		return blogEntry;
	}

	public void setBlogEntry(BlogEntry blogEntry) {
		this.blogEntry = blogEntry;
	}
}
