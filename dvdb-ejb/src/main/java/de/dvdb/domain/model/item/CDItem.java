package de.dvdb.domain.model.item;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
@DiscriminatorValue("7")
public abstract class CDItem extends Item implements Serializable {

	private static final long serialVersionUID = 9132383346084271157L;

	public static String TAGREL_CDITEM_GENRE = "cdgenre";

	private Integer length;

	@Column(nullable = true)
	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	@Override
	@Transient
	public String getSubTitle() {
		return null;
	}
}
