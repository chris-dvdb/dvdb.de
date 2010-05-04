package de.dvdb.domain.model.mediabase;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.dvdb.domain.model.item.Item;
import de.dvdb.infrastructure.persistence.EntityMetadata;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "itemtype", discriminatorType = DiscriminatorType.INTEGER)
@Table(name = "dvdb2_mediabase_item")
public abstract class MediabaseItem implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	private Long id;

	private Mediabase mediabase;

	private Item item;

	public static int STATUS_NORMAL = 0;

	public static int STATUS_WISHLIST = 1;

	public static int STATUS_ORDERED = 2;

	public static int STATUS_BORROWED = 3;

	private Integer status = 0;

	private Integer ratingContent;

	private Integer ratingMastering;

	private EntityMetadata entityMetadata = new EntityMetadata();

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@javax.persistence.ManyToOne(optional = false)
	@javax.persistence.JoinColumn(name = "item_id")
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@javax.persistence.ManyToOne(optional = false)
	@javax.persistence.JoinColumn(name = "mediabase_id")
	public Mediabase getMediabase() {
		return mediabase;
	}

	public void setMediabase(Mediabase mediabase) {
		this.mediabase = mediabase;
	}

	@Column(name = "ratingcontent")
	public Integer getRatingContent() {
		return ratingContent;
	}

	public void setRatingContent(Integer ratingContent) {
		this.ratingContent = ratingContent;
	}

	@Column(name = "ratingmastering")
	public Integer getRatingMastering() {
		return ratingMastering;
	}

	public void setRatingMastering(Integer ratingMastering) {
		this.ratingMastering = ratingMastering;
	}

	@Embedded
	public EntityMetadata getEntityMetadata() {
		return entityMetadata;
	}

	public void setEntityMetadata(EntityMetadata entityMetadata) {
		this.entityMetadata = entityMetadata;
	}

	@Transient
	public Integer getRatingContentAsInteger() {
		if (getRatingContent() == null)
			return 0;
		return getRatingContent();
	}

	@Transient
	public Integer getRatingMasteringAsInteger() {
		if (getRatingMastering() == null)
			return 0;
		return getRatingMastering();
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null)
			return false;
		if (!(arg0 instanceof MediabaseItem))
			return false;
		MediabaseItem u = (MediabaseItem) arg0;
		if (u.getId() == null)
			return false;
		if (this.getId() == null)
			return false;
		return getId().equals(u.getId());
	}
}
