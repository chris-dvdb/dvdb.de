package de.dvdb.domain.model.item.type;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;
import org.hibernate.validator.NotNull;

import de.dvdb.domain.model.mediabase.MediabaseItem;
import de.dvdb.domain.model.pricing.Price;
import de.dvdb.domain.model.tag.Tag;

@Entity
@Table(name = "dvdb2_item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.INTEGER)
@org.hibernate.annotations.Table(appliesTo = "dvdb2_item", indexes = {
		@Index(name = "idx_eancode", columnNames = { "eancode" }),
		@Index(name = "idx_asin", columnNames = { "asin" }) })
public abstract class Item implements Serializable {
	private static final long serialVersionUID = 5206898213669275502L;

	private Long id;

	private String title;

	private String titleLex;

	private String eanCode;

	private Date releaseDate;

	private Boolean indiziert;

	private String originalTitle;

	private String additionalInfo;

	private MediabaseItem mediabaseItem;

	private String rating;

	private Long numberOfOwners;

	private Long numberOfWishes;

	private Long numberOfReviews;

	private Double userRatingContent;

	private Long numberOfUserRatingContent;

	private Double userRatingMastering;

	private Long numberOfUserRatingMastering;

	private Date dateOfLastMaintenance;

	private Date dateOfLastAmazonItemLookup;

	private Long forumThreadId;

	private Long forumPriceThreadId;

	private Date itemDateOfImport;

	private Date itemDateOfUpdate;

	private Date itemDateOfData;

	private Price amazonPrice;

	private Price bestOnlinePrice;

	private Date dateOfLastPriceUpdate;

	private Date dateOfLastUpdateCheck;

	private String asin;

	private Integer itemType;

	private List<Price> prices = new ArrayList<Price>();

	private List<Price> top3prices = new ArrayList<Price>();

	private Integer salesRank;

	List<Tag> tags = new ArrayList<Tag>();

	private String tagString;

	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	@OneToMany(mappedBy = "item", fetch = FetchType.LAZY, targetEntity = Tag.class)
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	@Column(name = "tagstring")
	public String getTagString() {
		return tagString;
	}

	public void setTagString(String tagString) {
		this.tagString = tagString;
	}


	@Transient
	public String getTitleWithMediatype() {
		return getTitle();
	}

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "item_type", insertable = false, updatable = false)
	public Integer getItemType() {
		return itemType;
	}

	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}

	@Column(name = "salesrank")
	public Integer getSalesRank() {
		return salesRank;
	}

	public void setSalesRank(Integer salesRank) {
		this.salesRank = salesRank;
	}

	@NotNull
	@Column(nullable = false, length = 255)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if (title != null && title.length() > 255)
			title = title.substring(255);
		this.title = title;
	}

	public String getTitleLex() {
		return titleLex;
	}

	@Transient
	public String getURLTitle() {
		return title.trim().replaceAll("Š", "ae").replaceAll("š", "oe")
				.replaceAll("Ÿ", "ue").replaceAll("§", "ss").replaceAll("€",
						"Ae").replaceAll("…", "Oe").replaceAll("†", "Ue")
				.replaceAll("\u00e4", "ae").replaceAll("\u00f6", "oe")
				.replaceAll("\u00fc", "ue").replaceAll("\u00df", "ss")
				.replaceAll("\u00c4", "Ae").replaceAll("\u00d6", "Oe")
				.replaceAll("\u00dc", "Ue")
				.replaceAll("[^a-zA-Z0-9\\-\\ ]", "").replaceAll(" ", "-")
				.replaceAll("--", "-").replaceAll("--", "-").trim();
	}

	public String calcTitleLex(String title) {

		titleLex = title.trim().replaceFirst(
				"(^The )|(^[Dd]er )|(^[Dd]ie )|(^[Dd]as )", "");
		titleLex = titleLex.replaceAll("Š", "ae");
		titleLex = titleLex.replaceAll("š", "oe");
		titleLex = titleLex.replaceAll("Ÿ", "ue");
		titleLex = titleLex.replaceAll("§", "ss");
		titleLex = titleLex.replaceAll("€", "Ae");
		titleLex = titleLex.replaceAll("…", "Oe");
		titleLex = titleLex.replaceAll("†", "Ue").replaceAll("\u00e4", "ae")
				.replaceAll("\u00f6", "oe").replaceAll("\u00fc", "ue")
				.replaceAll("\u00df", "ss").replaceAll("\u00c4", "Ae")
				.replaceAll("\u00d6", "Oe").replaceAll("\u00dc", "Ue")
				.replaceAll("[^a-zA-Z0-9\\-\\ ]", "").replaceAll("  ", " ")
				.replaceAll("--", "-").replaceAll("--", "-").replaceAll("  ",
						" ").trim();

		int i;
		for (i = 0; i < 7; i++) {
			if (titleLex.length() <= i)
				break;
			char c = titleLex.charAt(i);
			if (Character.isDigit(c))
				continue;
			else
				break;
		}

		if (i > 0) {
			String number = titleLex.substring(0, i);
			String rest = titleLex.substring(i, titleLex.length());
			DecimalFormat dc = new DecimalFormat("0000000");
			return dc.format(new Integer(number)) + rest;
		}

		return titleLex;

	}

	@Transient
	public String getAbstract() {
		return getTitleLex();
	}

	public void setTitleLex(String titleLex) {
		if (titleLex != null && titleLex.length() > 255)
			titleLex = titleLex.substring(255);

		this.titleLex = titleLex;
	}

	@Column(nullable = true, length = 100)
	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		if (rating != null && rating.length() > 100)
			rating = rating.substring(100);
		this.rating = rating;
	}

	@Column(name = "eancode", nullable = true, length = 16)
	public String getEan() {
		return eanCode;
	}

	public void setEan(String eanCode) {
		if (eanCode != null && eanCode.length() > 16)
			eanCode = eanCode.substring(16);
		this.eanCode = eanCode;
	}

	@Column(name = "releasedate")
	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Column(name = "indiziert")
	public Boolean getIndiziert() {
		return indiziert;
	}

	public void setIndiziert(Boolean indiziert) {
		this.indiziert = indiziert;
	}

	@Column(name = "originaltitle", nullable = true, length = 255)
	public String getOriginalTitle() {
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		if (originalTitle != null && originalTitle.length() > 255)
			originalTitle = originalTitle.substring(255);
		this.originalTitle = originalTitle;
	}

	@Column(name = "additionalinfo", nullable = true, length = 4000)
	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		if (additionalInfo != null && additionalInfo.length() > 4000)
			additionalInfo = additionalInfo.substring(4000);
		this.additionalInfo = additionalInfo;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Item))
			return false;
		Item u = (Item) arg0;
		return getId().equals(u.getId());
	}

	@Column(name = "dateoflastmaintenance")
	public Date getDateOfLastMaintenance() {
		return dateOfLastMaintenance;
	}

	public void setDateOfLastMaintenance(Date dateOfLastMaintenance) {
		this.dateOfLastMaintenance = dateOfLastMaintenance;
	}

	@Column(name = "dateoflastamazonitemlookup")
	public Date getDateOfLastAmazonItemLookup() {
		return dateOfLastAmazonItemLookup;
	}

	public void setDateOfLastAmazonItemLookup(Date dateOfLastAmazonItemLookup) {
		this.dateOfLastAmazonItemLookup = dateOfLastAmazonItemLookup;
	}

	@Column(name = "numberofwishes")
	public Long getNumberOfWishes() {
		return numberOfWishes;
	}

	public void setNumberOfWishes(Long numberOfWishes) {
		this.numberOfWishes = numberOfWishes;
	}

	@Column(name = "numberofowners")
	public Long getNumberOfOwners() {
		return numberOfOwners;
	}

	public void setNumberOfOwners(Long numberOfOwners) {
		this.numberOfOwners = numberOfOwners;
	}

	@Column(name = "numberofreviews")
	public Long getNumberOfReviews() {
		return numberOfReviews;
	}

	public void setNumberOfReviews(Long numberOfReviews) {
		this.numberOfReviews = numberOfReviews;
	}

	@Column(name = "forum_thread_id")
	public Long getForumThreadId() {
		return forumThreadId;
	}

	public void setForumThreadId(Long forumThreadId) {
		this.forumThreadId = forumThreadId;
	}

	@Column(length = 15)
	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	@Column(name = "userratingcontent")
	public Double getUserRatingContent() {
		return userRatingContent;
	}

	public void setUserRatingContent(Double userRatingContent) {
		this.userRatingContent = userRatingContent;
	}

	@Column(name = "userratingmastering")
	public Double getUserRatingMastering() {
		return userRatingMastering;
	}

	public void setUserRatingMastering(Double userRatingMedium) {
		this.userRatingMastering = userRatingMedium;
	}

	@Column(name = "numberofuserratingcontent")
	public Long getNumberOfUserRatingContent() {
		return numberOfUserRatingContent;
	}

	public void setNumberOfUserRatingContent(Long numberOfUserRatingContent) {
		this.numberOfUserRatingContent = numberOfUserRatingContent;
	}

	@Column(name = "numberofuserratingmastering")
	public Long getNumberOfUserRatingMastering() {
		return numberOfUserRatingMastering;
	}

	public void setNumberOfUserRatingMastering(Long numberOfUserRatingMedium) {
		this.numberOfUserRatingMastering = numberOfUserRatingMedium;
	}

	@Transient
	public String getSubTitle() {
		return "";
	}

	@Transient
	public MediabaseItem getMediabaseItem() {
		return mediabaseItem;
	}

	public void setMediabaseItem(MediabaseItem mediabaseItem) {
		this.mediabaseItem = mediabaseItem;
	}

	@Column(name = "forumprice_thread_id")
	public Long getForumPriceThreadId() {
		return forumPriceThreadId;
	}

	public void setForumPriceThreadId(Long forumPriceThreadId) {
		this.forumPriceThreadId = forumPriceThreadId;
	}

	@Column(name = "itemdateofimport", nullable = true)
	public Date getItemDateOfImport() {
		return itemDateOfImport;
	}

	public void setItemDateOfImport(Date itemDateOfImport) {
		this.itemDateOfImport = itemDateOfImport;
	}

	@Column(name = "itemdateofupdate", nullable = true)
	public Date getItemDateOfUpdate() {
		return itemDateOfUpdate;
	}

	public void setItemDateOfUpdate(Date itemDateOfUpdate) {
		this.itemDateOfUpdate = itemDateOfUpdate;
	}

	@Column(name = "itemdateofdata", nullable = true)
	public Date getItemDateOfData() {
		return itemDateOfData;
	}

	public void setItemDateOfData(Date itemDateOfData) {
		this.itemDateOfData = itemDateOfData;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "amazon_price_id", nullable = true)
	public Price getAmazonPrice() {
		return amazonPrice;
	}

	public void setAmazonPrice(Price amazonPrice) {
		this.amazonPrice = amazonPrice;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "bestonlineprice_id", nullable = true)
	public Price getBestOnlinePrice() {
		return bestOnlinePrice;
	}

	public void setBestOnlinePrice(Price bestOnlinePrice) {
		this.bestOnlinePrice = bestOnlinePrice;
	}

	@Column(name = "dateoflastpriceupdate")
	public Date getDateOfLastPriceUpdate() {
		return dateOfLastPriceUpdate;
	}

	public void setDateOfLastPriceUpdate(Date dateOfLastPriceUpdate) {
		this.dateOfLastPriceUpdate = dateOfLastPriceUpdate;
	}

	@Column(name = "dateoflastupdatecheck")
	public Date getDateOfLastUpdateCheck() {
		return dateOfLastUpdateCheck;
	}

	public void setDateOfLastUpdateCheck(Date dateOfLastUpdateCheck) {
		this.dateOfLastUpdateCheck = dateOfLastUpdateCheck;
	}

	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	@OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
	@OrderBy("price, dateOfPrice DESC")
	public List<Price> getPrices() {
		return prices;
	}

	public void setPrices(List<Price> prices) {
		this.prices = prices;
	}

	@Transient
	public List<Price> getTop3Prices() {
		return top3prices;
	}

	public void setTop3Prices(List<Price> prices) {
		this.top3prices = prices;
	}

	public abstract String getUrlImageSmall();

	public abstract String getUrlImageMedium();

	public abstract String getUrlImageLarge();

	public abstract String getUrlImage40();

	public abstract void setUrlImageSmall(String url);

	public abstract void setUrlImageMedium(String url);

	public abstract void setUrlImageLarge(String url);

	public abstract void setUrlImage40(String url);

	@Transient
	public int getMinutesSinceLastPriceUpdate() {
		if (dateOfLastPriceUpdate == null)
			return 999999;
		long now = System.currentTimeMillis();
		long deltaInMinutes = (now - dateOfLastPriceUpdate.getTime()) / 1000 / 60;
		return (int) deltaInMinutes;

	}

	@Transient
	public boolean priceUpdateOk() {
		if (getDateOfLastPriceUpdate() == null)
			return true;

		if (salesRank == null)
			return true;

		int deltaInMinutes = getMinutesSinceLastPriceUpdate();

		// wir machen definitiv kein update, wenn das letzte update innerhalb
		// der letzten 3h

		if (deltaInMinutes < 180)
			return false;

		if (salesRank < 20000) {
			// update, wenn letztes update aelter als 8 stunden
			if (deltaInMinutes > 60 * 8)
				return true;
		} else {
			// update, wenn letztes update aelter als 40 stunden
			if (deltaInMinutes > 40 * 60)
				return true;
		}

		return false;
	}

	@Transient
	public Boolean getRequires18() {
		if (getIndiziert() == null)
			return false;

		if (getIndiziert())
			return true;

		if (getRating() == null)
			return false;

		if (getRating().equalsIgnoreCase("Keine Jugendfreigabe")
				|| getRating().equalsIgnoreCase("JK/SPIO")
				|| getRating().equalsIgnoreCase("Ungeprueft"))
			return true;

		return false;
	}

	@Transient
	public Integer getRatingContentAsInteger() {
		if (getUserRatingContent() == null)
			return 0;
		return (int) Math.round(getUserRatingContent());
	}

	@Transient
	public Integer getRatingMasteringAsInteger() {
		if (getUserRatingMastering() == null)
			return 0;
		return (int) Math.round(getUserRatingMastering());
	}

	@Transient
	public String getWikiPageURL() {
		return null;
	}

}
