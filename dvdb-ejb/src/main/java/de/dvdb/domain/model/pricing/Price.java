package de.dvdb.domain.model.pricing;

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
import javax.persistence.Transient;

import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.user.User;

@Entity
@Table(name = "dvdb2_price")
public class Price implements Serializable, Comparable<Price> {
	private static final long serialVersionUID = 3833189124977801528L;

	private Long id;

	private String availability;

	private User user;

	private Date validFromDate; // o

	private Date validToDate = new Date(System.currentTimeMillis() + 1000 * 60
			* 60 * 24 * 14);

	private Double price;

	private Double previousPrice;

	private Date changeDate;

	private Double porto = 0d;

	private Date dateOfPrice;

	private String comment;

	private Boolean availableOnline;

	private Boolean validForTPG;

	private String url;

	private Shop shop;

	private String shopName;

	private Item item;

	private Boolean priceValidated;

	private Date dateOfPriceValidation;

	private User priceValidatedByUser;

	private Boolean priceDeal;

	private Boolean tpgDeal;

	private Date dealDiscoveryDate;

	private Boolean discountAccepted;

	private Date discountFeedbackDate;

	private User discountFeedbackByUser;

	private Double lastPricePostedToTwitter;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int compareTo(Price o) {
		return getPrice().compareTo(o.getPrice());
	}

	@ManyToOne
	@JoinColumn(name = "item_id")
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Column(nullable = false)
	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
		// updatePriceChange();
	}

	public String getAvailability() {
		return availability;
	}

	public void setAvailability(String availability) {
		this.availability = availability;
	}

	@Column(name = "dateofprice", nullable = false)
	public Date getDateOfPrice() {
		return dateOfPrice;
	}

	public void setDateOfPrice(Date dateOfPrice) {
		this.dateOfPrice = dateOfPrice;
	}

	@Column(length = 255)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@ManyToOne
	@JoinColumn(name = "shop_id", nullable = true)
	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shopEntity) {
		this.shop = shopEntity;
	}

	@Transient
	public Double getCalculatedPorto() {
		if (shop != null) {
			if (shop.getPortoFree() != null
					&& shop.getPortoFree() <= getPrice())
				return 0d;
			if (shop.getPortoFree() != null && shop.getPortoFree() > getPrice())
				return shop.getPp();
			if (shop.getPp() != null)
				return shop.getPp();
		}

		return porto;
	}

	public Double getPorto() {
		return porto;
	}

	public void setPorto(Double porto) {
		this.porto = porto;
	}

	@Column(name = "dateofpricechange")
	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	@Column(name = "previousprice")
	public Double getPreviousPrice() {
		return previousPrice;
	}

	public void setPreviousPrice(Double previousPrice) {
		this.previousPrice = previousPrice;
		// updatePriceChange();
	}

	@Column(name = "validfortpg")
	public Boolean getValidForTPG() {
		return validForTPG;
	}

	public void setValidForTPG(Boolean validForTPG) {
		this.validForTPG = validForTPG;
	}

	@Column(name = "availableonline")
	public Boolean getAvailableOnline() {
		return availableOnline;
	}

	@Transient
	public Boolean getAvailableOnlineOrOnlineShop() {
		if (getAvailableOnline() != null)
			return getAvailableOnline();
		return getShop().getOnlineShop();
	}

	public void setAvailableOnline(Boolean availableOnline) {
		this.availableOnline = availableOnline;
	}

	@Column(name = "shopname", length = 255)
	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	@Column(name = "validfromdate")
	public Date getValidFromDate() {
		return validFromDate;
	}

	public void setValidFromDate(Date validFromDate) {
		this.validFromDate = validFromDate;
	}

	@Column(name = "validtodate")
	public Date getValidToDate() {
		return validToDate;
	}

	public void setValidToDate(Date validToDate) {
		this.validToDate = validToDate;
	}

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = true, updatable = true)
	public User getUser() {
		return user;
	}

	public void setUser(User reporter) {
		this.user = reporter;
	}

	@ManyToOne
	@JoinColumn(name = "pricevalidatedbyuser_id", nullable = true, updatable = true)
	public User getPriceValidatedByUser() {
		return priceValidatedByUser;
	}

	public void setPriceValidatedByUser(User validatedByUser) {
		this.priceValidatedByUser = validatedByUser;
	}

	@Column(name = "pricevalidated")
	public Boolean getPriceValidated() {
		return priceValidated;
	}

	public void setPriceValidated(Boolean validated) {
		this.priceValidated = validated;
	}

	@Column(name = "dateofpricevalidation")
	public Date getDateOfPriceValidation() {
		return dateOfPriceValidation;
	}

	public void setDateOfPriceValidation(Date dateLastValidated) {
		this.dateOfPriceValidation = dateLastValidated;
	}

	@Column(name = "dealdiscoverydate")
	public Date getDealDiscoveryDate() {
		return dealDiscoveryDate;
	}

	public void setDealDiscoveryDate(Date dealDiscoveryDate) {
		this.dealDiscoveryDate = dealDiscoveryDate;
	}

	@Column(name = "pricedeal")
	public Boolean getPriceDeal() {
		return priceDeal;
	}

	public void setPriceDeal(Boolean priceDeal) {
		this.priceDeal = priceDeal;
	}

	@Column(name = "tpgdeal")
	public Boolean getTpgDeal() {
		return tpgDeal;
	}

	public void setTpgDeal(Boolean tpgDeal) {
		this.tpgDeal = tpgDeal;
	}

	@Column(name = "discountaccepted")
	public Boolean getDiscountAccepted() {
		return discountAccepted;
	}

	public void setDiscountAccepted(Boolean discountAccepted) {
		this.discountAccepted = discountAccepted;
	}

	@ManyToOne
	@JoinColumn(name = "discountfeedbackbyuser_id", nullable = true, updatable = true)
	public User getDiscountFeedbackByUser() {
		return discountFeedbackByUser;
	}

	public void setDiscountFeedbackByUser(User discountAcceptedByUser) {
		this.discountFeedbackByUser = discountAcceptedByUser;
	}

	@Column(name = "discountfeedbackdate")
	public Date getDiscountFeedbackDate() {
		return discountFeedbackDate;
	}

	public void setDiscountFeedbackDate(Date discountFeedbackDate) {
		this.discountFeedbackDate = discountFeedbackDate;
	}

	@Transient
	public Double getPriceWithPorto() {
		if (getCalculatedPorto() == null)
			return null;
		return price + getCalculatedPorto();
	}

	@Transient
	public Double getAbsolutePriceChange() {
		if (price == null || previousPrice == null)
			return null;
		return price - previousPrice;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Price))
			return false;
		Price off = (Price) arg0;
		return getId().equals(off.getId());
	}

	@Transient
	public String getQualifiedShopName() {
		if (getShop() == null)
			return getShopName();
		else
			return getShop().getName();
	}

	@Override
	public String toString() {
		return item.getTitle() + ", Shop " + getQualifiedShopName()
				+ ", Price " + price + ", " + availability;
	}

	@Column(name = "price_posted_to_twitter")
	public Double getPricePostedToTwitter() {
		return lastPricePostedToTwitter;
	}

	public void setPricePostedToTwitter(Double lastPricePostedToTwitter) {
		this.lastPricePostedToTwitter = lastPricePostedToTwitter;
	}
}
