package de.dvdb.domain.model.pricing;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Shop represents an online shop. At this time it should not represent a local
 * store.
 * 
 * @author cpiets
 * 
 */
@Entity
@Table(name = "dvdb2_shop")
public class Shop implements Serializable {
	private static final long serialVersionUID = 3833189124977801521L;

	public static long AMAZONID = 1;

	private Long id;

	private String name;

	private String urlSite;

	private String urlLogo;

	private Double pp;

	private Boolean onlineShop;

	private Boolean store;

	private String street;

	private String city;

	private String plz;

	private Double portoFree;

	private String priceFetcherComponentName;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "url_logo")
	public String getUrlLogo() {
		return urlLogo;
	}

	public void setUrlLogo(String urlLogo) {
		this.urlLogo = urlLogo;
	}

	@Column(name = "url_site")
	public String getUrlSite() {
		return urlSite;
	}

	public void setUrlSite(String urlSite) {
		this.urlSite = urlSite;
	}

	public Double getPp() {
		return pp;
	}

	public void setPp(Double pp) {
		this.pp = pp;
	}

	@Column(name = "onlineshop")
	public Boolean getOnlineShop() {
		return onlineShop;
	}

	public void setOnlineShop(Boolean onlineShop) {
		this.onlineShop = onlineShop;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public Boolean getStore() {
		return store;
	}

	public void setStore(Boolean store) {
		this.store = store;
	}

	@Column(name = "portofree")
	public Double getPortoFree() {
		return portoFree;
	}

	public void setPortoFree(Double portoFree) {
		this.portoFree = portoFree;
	}

	@Column(name = "pricefetchercomponentname")
	public String getPriceFetcherComponentName() {
		return priceFetcherComponentName;
	}

	public void setPriceFetcherComponentName(String priceFetcherComponentName) {
		this.priceFetcherComponentName = priceFetcherComponentName;
	}

	@Override
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Shop))
			return false;
		Shop off = (Shop) arg0;
		return getId().equals(off.getId());
	}

	@Override
	public String toString() {
		return getId() + ": " + getName();
	}
}
