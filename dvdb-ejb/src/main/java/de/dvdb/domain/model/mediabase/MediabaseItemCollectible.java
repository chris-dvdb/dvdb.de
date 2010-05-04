package de.dvdb.domain.model.mediabase;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.jboss.seam.annotations.Name;

@Entity
@DiscriminatorValue("1")
@Name("mediabaseItemCollectible")
public class MediabaseItemCollectible extends MediabaseItem implements
		Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	public static Integer PURCHASETYPE_NONE = 1;

	public static Integer PURCHASETYPE_LOCALSHOP = 2;

	public static Integer PURCHASETYPE_ONLINESHOP = 3;

	public static Integer PURCHASETYPE_AUCTION = 4;

	public static Integer PURCHASETYPE_GIFT = 5;

	public static Integer PURCHASETYPE_GRATIS = 6;

	public static Integer PURCHASETYPE_CLUB = 7;

	public static Integer PURCHASETYPE_SWAPPED = 8;

	public static Integer PURCHASETYPE_BOERSE = 9;

	public static Integer PURCHASETYPE_VIDEOTHEK = 10;

	public static Integer PURCHASETYPE_2NDHANDSHOP = 11;

	private String position;

	private Integer topNPosition;

	private String comment;

	private Date lastConsumeDate;

	private Integer consumeCounter;

	private Integer purchaseType;

	private Double purchasePrice;

	private String purchaseShop;

	private Date dateOfPurchase = new Date();

	private Boolean favorite;

	private de.dvdb.domain.model.social.Buddy borrowedToBuddy;

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Column(name = "topnposition")
	public Integer getTopNPosition() {
		return topNPosition;
	}

	public void setTopNPosition(Integer topNPosition) {
		this.topNPosition = topNPosition;
	}

	@Column(name = "comment", length = 5000)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Column(name = "lastconsumedate")
	public Date getLastConsumeDate() {
		return lastConsumeDate;
	}

	public void setLastConsumeDate(Date lastConsumeDate) {
		this.lastConsumeDate = lastConsumeDate;
	}

	@Column(name = "consumecounter")
	public Integer getConsumeCounter() {
		return consumeCounter;
	}

	public void setConsumeCounter(Integer consumeCounter) {
		this.consumeCounter = consumeCounter;
	}

	@Column(name = "dateofpurchase")
	public Date getDateOfPurchase() {
		return dateOfPurchase;
	}

	public void setDateOfPurchase(Date dateOfPurchase) {
		this.dateOfPurchase = dateOfPurchase;
	}

	@Column(name = "purchaseprice")
	public Double getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	@Column(name = "purchaseshop", length = 255)
	public String getPurchaseShop() {
		return purchaseShop;
	}

	public void setPurchaseShop(String purchaseShop) {
		this.purchaseShop = purchaseShop;
	}

	@Column(name = "purchasetype")
	public Integer getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(Integer purchaseType) {
		this.purchaseType = purchaseType;
	}

	public Boolean getFavorite() {
		return favorite;
	}

	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
	}

	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "borrowed_buddy_id")
	public de.dvdb.domain.model.social.Buddy getBorrowedToBuddy() {
		return borrowedToBuddy;
	}

	public void setBorrowedToBuddy(de.dvdb.domain.model.social.Buddy borrowedToBuddy) {
		this.borrowedToBuddy = borrowedToBuddy;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof MediabaseItemCollectible))
			return false;
		MediabaseItemCollectible u = (MediabaseItemCollectible) arg0;
		if (getId() == null || u.getId() == null)
			return false;
		return getId().equals(u.getId());
	}
}
