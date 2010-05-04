package de.dvdb.domain.model.mediabase;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.jboss.seam.annotations.Name;

import de.dvdb.domain.model.user.User;

@Entity
@DiscriminatorValue("2")
@Name("mediabaseItemWish")
public class MediabaseItemWish extends MediabaseItem implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	private Double lastNotificationPrice;

	private Double maximumPurchasePrice;

	private Integer intensity;

	private Boolean notify = true;

	public Boolean getNotify() {
		return notify;
	}

	public void setNotify(Boolean notify) {
		this.notify = notify;
	}

	@Column(name = "lastnotificationprice")
	public Double getLastNotificationPrice() {
		return lastNotificationPrice;
	}

	public void setLastNotificationPrice(Double lastNotificationPrice) {
		this.lastNotificationPrice = lastNotificationPrice;
	}

	@Override
	@Column(insertable = false, updatable = false)
	public Integer getStatus() {
		return STATUS_WISHLIST;
	}

	@Column(name = "maximumpurchaseprice")
	public Double getMaximumPurchasePrice() {
		return maximumPurchasePrice;
	}

	public void setMaximumPurchasePrice(Double maximumPurchasePrice) {
		this.maximumPurchasePrice = maximumPurchasePrice;
	}

	public Integer getIntensity() {
		return intensity;
	}

	public void setIntensity(Integer intensity) {
		this.intensity = intensity;
	}

	@Transient
	public User getBorrowedToBuddy() {
		return null;
	}
}
