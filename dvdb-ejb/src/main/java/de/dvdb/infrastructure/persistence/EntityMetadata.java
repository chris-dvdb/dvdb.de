package de.dvdb.infrastructure.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EntityMetadata implements Serializable {

	private static final long serialVersionUID = 3445482339759550942L;

	Date dateOfCreation;

	Date dateOfLastUpdate;

	@Column(name = "dateofcreation")
	public Date getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	@Column(name = "dateoflastupdate")
	public Date getDateOfLastUpdate() {
		return dateOfLastUpdate;
	}

	public void setDateOfLastUpdate(Date dateOfLastUpdate) {
		this.dateOfLastUpdate = dateOfLastUpdate;
	}
}
