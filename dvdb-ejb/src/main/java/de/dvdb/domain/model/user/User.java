package de.dvdb.domain.model.user;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;
import org.hibernate.validator.Length;
import org.jboss.seam.annotations.security.management.UserEnabled;
import org.jboss.seam.annotations.security.management.UserFirstName;
import org.jboss.seam.annotations.security.management.UserLastName;
import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;

import de.dvdb.domain.model.item.image.ImageSource;
import de.dvdb.domain.model.mediabase.Mediabase;
import de.dvdb.domain.model.social.Newsletter;
import de.dvdb.infrastructure.persistence.EntityMetadata;

@Entity
@Table(name = "dvdb2_user")
@org.hibernate.annotations.Table(appliesTo = "dvdb2_user", indexes = { @Index(name = "idx_username", columnNames = { "username" }) })
public class User implements Serializable, Principal, ImageSource {
	private static final long serialVersionUID = 3833189124977801528L;

	public static int STATUS_NEW = 0;

	public static int STATUS_CONFIRMED = 1;

	Long id;

	String username;

	String firstname;

	String lastname;

	String email;

	Boolean adult = false;

	Boolean accountEnabled;

	String confirmationCode;

	String registrationSite;

	Boolean newsletter = true;

	Date dateOfBirth;

	Date dateOfLastSignIn;

	String signature = "";

	String password;
	
	byte[] imageData;

	byte[] imageDataSmall;

	String mimeType;

	Long oldUserId;

	Integer numberOfReportedPrices;

	Integer numberCollectibles;

	EntityMetadata entityMetadata = new EntityMetadata();

	Mediabase mediabase;

	Newsletter lastNewsletter;

	Integer type = 0;

	Date lastInboxVisit;

	Date dateOfLastMessage;

	Integer tableLayout = 0;

	Integer itemsOnPage = 25;

	Long facebookUserId;

	String passwordHash;

	Set<Role> roles = new HashSet<Role>();

	public static final Integer TYPE_ADMIN = 9;
	public static final Integer TYPE_SUPERUSER = 5;
	public static final Integer TYPE_USER = 1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(mappedBy = "user", optional = false, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	public Mediabase getMediabase() {
		return mediabase;
	}

	public void setMediabase(Mediabase mediabase) {
		this.mediabase = mediabase;
	}

	@UserPrincipal
	// @Pattern(regex = "[a-zA-Z]?[a-zA-Z0-9_\\-]+", message =
	// "#{messages['signUp.error.usernameChars']}")
	@Column(name = "username", unique = true, length = 80, nullable = false)
	public String getUsername() {
		return username;
	}

	public void setUsername(String nickname) {
		this.username = nickname;
	}

	@Transient
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getAdult() {
		return adult;
	}

	public void setAdult(Boolean adult) {
		this.adult = adult;
	}

	@Column(name = "confirmationcode")
	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Embedded
	public EntityMetadata getEntityMetadata() {
		return entityMetadata;
	}

	public void setEntityMetadata(EntityMetadata entityMetadata) {
		this.entityMetadata = entityMetadata;
	}

	@UserFirstName
	@Length(max = 255)
	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	@Length(max = 255)
	@UserLastName
	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Boolean getNewsletter() {
		return newsletter;
	}

	public void setNewsletter(Boolean newsletter) {
		this.newsletter = newsletter;
	}

	@Column(name = "registrationsite")
	public String getRegistrationSite() {
		return registrationSite;
	}

	public void setRegistrationSite(String registrationSite) {
		this.registrationSite = registrationSite;
	}

	@UserEnabled
	@Column(name = "account_enabled")
	public Boolean getAccountEnabled() {
		return accountEnabled;
	}

	public void setAccountEnabled(Boolean accountEnabled) {
		this.accountEnabled = accountEnabled;
	}

	@Lob
	@Column(name = "imagedata", columnDefinition = "MEDIUMBLOB")
	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] image) {
		this.imageData = image;
	}

	@Lob
	@Column(name = "imagedatasmall", columnDefinition = "MEDIUMBLOB")
	public byte[] getImageDataSmall() {
		return imageDataSmall;
	}

	public void setImageDataSmall(byte[] imageDataSmall) {
		this.imageDataSmall = imageDataSmall;
	}

	@Column(name = "mimetype")
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Column(name = "dateofbirth")
	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Column(name = "dateoflastsignin")
	public Date getDateOfLastSignIn() {
		return dateOfLastSignIn;
	}

	public void setDateOfLastSignIn(Date dateOfLastSignIn) {
		this.dateOfLastSignIn = dateOfLastSignIn;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Column(name = "olduser_id")
	public Long getOldUserId() {
		return oldUserId;
	}

	public void setOldUserId(Long oldUserId) {
		this.oldUserId = oldUserId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "numbercollectibles")
	public Integer getNumberCollectibles() {
		return numberCollectibles;
	}

	public void setNumberCollectibles(Integer numberCollectibles) {
		this.numberCollectibles = numberCollectibles;
	}

	@Transient
	public String getName() {
		return getUsername();
	}

	@javax.persistence.ManyToOne(optional = true, fetch = FetchType.LAZY)
	@javax.persistence.JoinColumn(name = "lastnewsletter_id")
	public Newsletter getLastNewsletter() {
		return lastNewsletter;
	}

	public void setLastNewsletter(Newsletter lastNewsletter) {
		this.lastNewsletter = lastNewsletter;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof User))
			return false;
		User u = (User) arg0;
		return getUsername().equals(u.getUsername());
	}

	@Override
	public String toString() {
		return "User: " + getUsername();
	}

	@Column(name = "dateoflastmessage")
	public Date getDateOfLastMessage() {
		return dateOfLastMessage;
	}

	public void setDateOfLastMessage(Date dateOfLastMessage) {
		this.dateOfLastMessage = dateOfLastMessage;
	}

	@Column(name = "lastinboxvisit")
	public Date getLastInboxVisit() {
		return lastInboxVisit;
	}

	public void setLastInboxVisit(Date lastInboxVisit) {
		this.lastInboxVisit = lastInboxVisit;
	}

	public Integer getItemsOnPage() {
		return itemsOnPage;
	}

	@Column(name = "itemsonpage")
	public void setItemsOnPage(Integer itemsOnPage) {
		this.itemsOnPage = itemsOnPage;
	}

	public Integer getTableLayout() {
		return tableLayout;
	}

	@Column(name = "tablelayout")
	public void setTableLayout(Integer tableLayout) {
		this.tableLayout = tableLayout;
	}

	@Column(name = "facebook_userid")
	public Long getFacebookUserId() {
		return facebookUserId;
	}

	public void setFacebookUserId(Long facebookUserId) {
		this.facebookUserId = facebookUserId;
	}

	@Length(max = 255)
	@Column(name = "password_hash")
	@UserPassword(hash = "md5")
	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	@UserRoles
	@ManyToMany(targetEntity = Role.class)
	@JoinTable(name = "dvdb2_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}
