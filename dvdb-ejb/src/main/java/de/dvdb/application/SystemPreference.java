package de.dvdb.application;

import java.io.Serializable;

@javax.persistence.Entity
@javax.persistence.Table(name = "dvdb2_systempreference")
public class SystemPreference implements Serializable {

	private static final long serialVersionUID = 253815690158331276L;

	private Long id;

	private String feature;

	private String value;

	public SystemPreference() {
	}

	public SystemPreference(String feature, String value) {
		this();
		this.feature = feature;
		this.value = value;
	}

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
