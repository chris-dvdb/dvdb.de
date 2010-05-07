package de.dvdb.domain.model.item.type;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import de.dvdb.domain.model.user.User;

@Entity
@DiscriminatorValue("3")
public class UserDVDItem extends DVDItem implements Serializable {

	private static final long serialVersionUID = -4077732947803570902L;

	private User user;

	private Long dvdId;

	private String url;

	@javax.persistence.ManyToOne(optional = true)
	@javax.persistence.JoinColumn(name = "user_id")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(nullable = true, unique = true, name = "source_dvdid")
	public Long getDvdId() {
		return dvdId;
	}

	public void setDvdId(Long dvdId) {
		this.dvdId = dvdId;
	}

	@Override
	@Transient
	public String getUrlImageLarge() {
		return null;
	}

	@Override
	@Transient
	public String getUrlImageMedium() {
		return null;
	}

	@Override
	@Transient
	public String getUrlImageSmall() {
		return null;
	}

	@Override
	@Transient
	public void setUrlImageLarge(String url) {
	}

	@Override
	@Transient
	public void setUrlImageMedium(String url) {
	}

	@Override
	@Transient
	public void setUrlImageSmall(String url) {
	}

	@Override
	@Transient
	public String getUrlImage40() {
		return null;
	}
	
	@Override
	@Transient
	public void setUrlImage40(String url) {
	}
}
