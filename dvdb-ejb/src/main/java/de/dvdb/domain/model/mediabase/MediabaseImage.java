package de.dvdb.domain.model.mediabase;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;

import org.hibernate.validator.Length;
import org.jboss.seam.annotations.Name;

@javax.persistence.Entity
@javax.persistence.Table(name = "dvdb2_mediabaseimage")
@Name("mediabaseImage")
public class MediabaseImage implements Serializable {

	private static final long serialVersionUID = 253815690158331276L;

	private Long id;

	private String title;

	private String description;

	private String filename;

	private String imageContentType;

	private Mediabase mediabase;

	private Date imageDate;

	private int imageOrder;

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "imagedate")
	public Date getImageDate() {
		return imageDate;
	}

	public void setImageDate(Date imageDate) {
		this.imageDate = imageDate;
	}

	@Column(name = "imageorder")
	public int getImageOrder() {
		return imageOrder;
	}

	public void setImageOrder(int imageOrder) {
		this.imageOrder = imageOrder;
	}

	@Length(max = 10000)
	@Column(length = 10000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Length(max = 255)
	@Column(length = 255)
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Length(max = 10000)
	@Column(length = 10000)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "content_type")
	public String getImageContentType() {
		return imageContentType;
	}

	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
	}

	@javax.persistence.ManyToOne(optional = false, fetch = FetchType.LAZY)
	@javax.persistence.JoinColumn(name = "mediabase_id")
	public Mediabase getMediabase() {
		return mediabase;
	}

	public void setMediabase(Mediabase mediabase) {
		this.mediabase = mediabase;
	}

	@Override
	public boolean equals(Object arg0) {
		if (super.equals(arg0))
			return true;
		if (!(arg0 instanceof MediabaseImage))
			return false;
		MediabaseImage s = (MediabaseImage) arg0;
		return getId().equals(s.getId());
	}

}
