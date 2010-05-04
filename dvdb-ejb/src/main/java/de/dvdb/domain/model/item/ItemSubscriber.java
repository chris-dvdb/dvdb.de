package de.dvdb.domain.model.item;

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

import de.dvdb.domain.model.user.User;

@Entity
@Table(name = "dvdb2_item_subscriber")
public class ItemSubscriber implements Serializable {
	private static final long serialVersionUID = 3833189124977801528L;

	private Long id;

	// expiration nach 2 wochen per default
	private Date dateOfExpiration = new Date(System.currentTimeMillis() + 1000
			* 60 * 60 * 24 * 14);

	Item item;

	User user;

	Double lastCommunicatedPrice;

	Boolean communicateStorePriceUpdates;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false, updatable = false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false, updatable = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Column(name = "dateofexpiration")
	public Date getDateOfExpiration() {
		return dateOfExpiration;
	}

	public void setDateOfExpiration(Date dateOfExpiration) {
		this.dateOfExpiration = dateOfExpiration;
	}

	@Column(name = "lastcommunicatedprice")
	public Double getLastCommunicatedPrice() {
		return lastCommunicatedPrice;
	}

	public void setLastCommunicatedPrice(Double lastCommunicatedPrice) {
		this.lastCommunicatedPrice = lastCommunicatedPrice;
	}

	@Column(name = "communicatestorepriceupdates")
	public Boolean getCommunicateStorePriceUpdates() {
		return communicateStorePriceUpdates;
	}

	public void setCommunicateStorePriceUpdates(
			Boolean communicateStorePriceUpdates) {
		this.communicateStorePriceUpdates = communicateStorePriceUpdates;
	}

}
