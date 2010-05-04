package de.dvdb.domain.model.social;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.tag.Tagable;

@Entity
@Table(name = "dvdb2_blogentry")
@Name("blogEntry")
public class BlogEntry implements Serializable, Tagable {

	private static final long serialVersionUID = -8753714944734959457L;

	private Long id;

	private String title;

	private String excerpt;

	private String body;

	private Date date = new Date();

	private Mediabase mediabase;

	private Boolean active;

	private Integer countComments = 0;

	private Item item;

	private Boolean wysiwyg = true;

	public boolean isWysiwyg() {
		return wysiwyg;
	}

	public void setWysiwyg(boolean wysiwyg) {
		this.wysiwyg = wysiwyg;
	}

	@javax.persistence.ManyToOne(optional = true)
	@javax.persistence.JoinColumn(name = "item_id")
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Column(name = "countcomments")
	public Integer getCountComments() {
		return countComments;
	}

	public void setCountComments(Integer countComments) {
		this.countComments = countComments;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public BlogEntry(Mediabase mediabase) {
		this.mediabase = mediabase;
	}

	public BlogEntry() {
	}

	@ManyToOne
	@NotNull
	@JoinColumn(name = "mediabase_id")
	public Mediabase getMediabase() {
		return mediabase;
	}

	public void setMediabase(Mediabase mediabase) {
		this.mediabase = mediabase;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@NotNull
	@Length(max = 70)
	public String getTitle() {
		return title;
	}

	@Length(max = 2000)
	public String getExcerpt() {
		return excerpt;
	}

	@NotNull
	@Length(max = 50000)
	public String getBody() {
		return body;
	}

	@NotNull
	public Date getDate() {
		return date;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setExcerpt(String excerpt) {
		if ("".equals(excerpt))
			excerpt = null;
		this.excerpt = excerpt;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
