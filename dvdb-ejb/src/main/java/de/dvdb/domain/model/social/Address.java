package de.dvdb.domain.model.social;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class Address implements Serializable {

	private static final long serialVersionUID = 1967219030492306714L;
	String street;
	String country;
	String plz;
	String city;
	Double breite;
	Double laenge;

	public Double getLaenge() {
		return laenge;
	}

	public void setLaenge(Double laenge) {
		this.laenge = laenge;
	}

	public Double getBreite() {
		return breite;
	}

	public void setBreite(Double breite) {
		this.breite = breite;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

}
