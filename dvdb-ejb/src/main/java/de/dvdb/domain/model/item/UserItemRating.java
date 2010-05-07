package de.dvdb.domain.model.item;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.user.User;

@Entity
@Table(name = "dvdb2_user_item_rating")
public class UserItemRating implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	private Long id;

	private Item item;

	private User user;

	private Integer ratingContent;

	private Integer ratingMastering;

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

	@javax.persistence.ManyToOne(optional = false)
	@javax.persistence.JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

}
