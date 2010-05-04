package de.dvdb.domain.model.mediabase;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.social.Address;
import de.dvdb.domain.model.user.User;

@Entity
@Table(name = "dvdb2_mediabase")
public class Mediabase implements Serializable {

	private static final long serialVersionUID = 4473853441516634290L;

	private Long id;

	private User user;

	private String mediabasePassword;

	private String mediabasePassword18;

	private Integer pageHits = 0;

	private Date pageHitResetDate = new Date();

	private String theme = "default";

	private Integer itemDisplayMode;

	private Integer itemsPerPage = 50;

	private Boolean aboutMeActivated = true;

	private Boolean guestBookActivated = true;

	private Boolean showroomActivated = true;

	private Boolean feedbackFormActivated = true;

	private Boolean blogActivated = true;

	private Boolean wishlistActivated = true;

	private Boolean topNActivated = true;

	private Boolean indiziertActivated = true;

	private Boolean ordersActivated = true;

	private Boolean privacyActivated = false;

	private Boolean geoActivated = true;

	private Boolean galleryActivated = true;

	private AccessLevelEnum accessLevel = AccessLevelEnum.EVERYBODY;

	private Skin skin;

	private String customCss;

	private Boolean displayPersonalDetails = true;

	private Integer numberCollectibles = 0;

	private Integer numberWishes = 0;

	private Integer numberOrdered = 0;

	private Integer numberBorrowed = 0;

	private String home;

	private Address address = new Address();

	private String aboutMe;

	private String defaultMediabaseSortOrder;

	private Integer totalVisits;
	
	private Item lastItem;

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "aboutmeactivated")
	public Boolean getAboutMeActivated() {
		return aboutMeActivated;
	}

	public void setAboutMeActivated(Boolean aboutMeActivated) {
		this.aboutMeActivated = aboutMeActivated;
	}

	@Column(name = "blogactivated")
	public Boolean getBlogActivated() {
		return blogActivated;
	}

	public void setBlogActivated(Boolean blogActivated) {
		this.blogActivated = blogActivated;
	}

	@Column(name = "feedbackformactivated")
	public Boolean getFeedbackFormActivated() {
		return feedbackFormActivated;
	}

	public void setFeedbackFormActivated(Boolean feedbackFormActivated) {
		this.feedbackFormActivated = feedbackFormActivated;
	}

	@Column(name = "guestbookactivated")
	public Boolean getGuestBookActivated() {
		return guestBookActivated;
	}

	public void setGuestBookActivated(Boolean guestBookActivated) {
		this.guestBookActivated = guestBookActivated;
	}

	@Column(name = "itemdisplaymode")
	public Integer getItemDisplayMode() {
		return itemDisplayMode;
	}

	public void setItemDisplayMode(Integer itemDisplayMode) {
		this.itemDisplayMode = itemDisplayMode;
	}

	@Column(name = "itemsperpage")
	public Integer getItemsPerPage() {
		return itemsPerPage;
	}

	public void setItemsPerPage(Integer itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	@Column(name = "mediabasepassword")
	public String getMediabasePassword() {
		return mediabasePassword;
	}

	public void setMediabasePassword(String mediabasePassword) {
		this.mediabasePassword = mediabasePassword;
	}

	@Column(name = "mediabasepassword18")
	public String getMediabasePassword18() {
		return mediabasePassword18;
	}

	public void setMediabasePassword18(String mediabasePassword18) {
		this.mediabasePassword18 = mediabasePassword18;
	}

	@Column(name = "showroomactivated")
	public Boolean getShowroomActivated() {
		return showroomActivated;
	}

	public void setShowroomActivated(Boolean showroomActivated) {
		this.showroomActivated = showroomActivated;
	}

	@Column(name = "topnactivated")
	public Boolean getTopNActivated() {
		return topNActivated;
	}

	public void setTopNActivated(Boolean topNActivated) {
		this.topNActivated = topNActivated;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	@Column(name = "wishlistactivated")
	public Boolean getWishlistActivated() {
		return wishlistActivated;
	}

	public void setWishlistActivated(Boolean wishlistActivated) {
		this.wishlistActivated = wishlistActivated;
	}

	@Column(name = "indiziertactivated")
	public Boolean getIndiziertActivated() {
		return indiziertActivated;
	}

	public void setIndiziertActivated(Boolean indiziertActivated) {
		this.indiziertActivated = indiziertActivated;
	}

	@Column(name = "privacyactivated")
	public Boolean getPrivacyActivated() {
		return privacyActivated;
	}

	public void setPrivacyActivated(Boolean privacyActivated) {
		this.privacyActivated = privacyActivated;
	}

	@Column(name = "ordersactivated")
	public Boolean getOrdersActivated() {
		return ordersActivated;
	}

	public void setOrdersActivated(Boolean ordersActivated) {
		this.ordersActivated = ordersActivated;
	}

	@Column(name = "pagehitresetdate")
	public Date getPageHitResetDate() {
		return pageHitResetDate;
	}

	public void setPageHitResetDate(Date pageHitResetDate) {
		this.pageHitResetDate = pageHitResetDate;
	}

	@Column(name = "pagehits")
	public Integer getPageHits() {
		return pageHits;
	}

	public void setPageHits(Integer pageHits) {
		this.pageHits = pageHits;
	}

	@Column(name = "customcss", length = 40000)
	public String getCustomCss() {
		return customCss;
	}

	public void setCustomCss(String css) {
		this.customCss = css;
	}

	@Column(name = "displaypersonaldetails")
	public Boolean getDisplayPersonalDetails() {
		return displayPersonalDetails;
	}

	public void setDisplayPersonalDetails(Boolean displayPersonalDetails) {
		this.displayPersonalDetails = displayPersonalDetails;
	}

	@javax.persistence.ManyToOne(fetch = FetchType.EAGER)
	@javax.persistence.JoinColumn(name = "skin_id", nullable = false)
	public Skin getSkin() {
		return skin;
	}

	public void setSkin(Skin skin) {
		this.skin = skin;
	}

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "accesslevel")
	public AccessLevelEnum getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(AccessLevelEnum accessLevel) {
		this.accessLevel = accessLevel;
	}

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	@Column(name = "numberborrowed")
	public Integer getNumberBorrowed() {
		return numberBorrowed;
	}

	public void setNumberBorrowed(Integer numberBorrowed) {
		this.numberBorrowed = numberBorrowed;
	}

	@Column(name = "numbercollectibles")
	public Integer getNumberCollectibles() {
		return numberCollectibles;
	}

	public void setNumberCollectibles(Integer numberCollectibles) {
		this.numberCollectibles = numberCollectibles;
	}

	@Column(name = "numberordered")
	public Integer getNumberOrdered() {
		return numberOrdered;
	}

	public void setNumberOrdered(Integer numberOrdered) {
		this.numberOrdered = numberOrdered;
	}

	@Column(name = "numberwishes")
	public Integer getNumberWishes() {
		return numberWishes;
	}

	public void setNumberWishes(Integer numberWishes) {
		this.numberWishes = numberWishes;
	}

	@Column(name = "aboutme", length = 10000)
	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	@Embedded
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Column(name = "totalvisits")
	public Integer getTotalVisits() {
		return totalVisits;
	}

	public void setTotalVisits(Integer totalVisits) {
		this.totalVisits = totalVisits;
	}

	@Column(name = "geoactivated")
	public Boolean getGeoActivated() {
		return geoActivated;
	}

	public void setGeoActivated(Boolean geoActivated) {
		this.geoActivated = geoActivated;
	}

	@Column(name = "galleryactivated")
	public Boolean getGalleryActivated() {
		return galleryActivated;
	}

	public void setGalleryActivated(Boolean galleryActivated) {
		this.galleryActivated = galleryActivated;
	}
	
	@javax.persistence.ManyToOne
	@javax.persistence.JoinColumn(name = "lastitem_id", nullable = true)
	public Item getLastItem() {
		return lastItem;
	}
	
	public void setLastItem(Item lastItem) {
		this.lastItem = lastItem;
	}

	@Transient
	public URL getMediabaseURL() {
		try {
			return new URL("http://moviebase.dvdb.de/" + user.getUsername());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Column(name = "defaultmediabasesortorder", length = 50)
	public String getDefaultMediabaseSortOrder() {
		return defaultMediabaseSortOrder;
	}

	public void setDefaultMediabaseSortOrder(String defaultMediabaseSortOrder) {
		this.defaultMediabaseSortOrder = defaultMediabaseSortOrder;
	}
}
