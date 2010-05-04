package de.dvdb.domain.model.tag;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.annotations.Index;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "objecttype", discriminatorType = DiscriminatorType.INTEGER)
@javax.persistence.Table(name = "dvdb2_tag2")
@org.hibernate.annotations.Table(appliesTo = "dvdb2_tag2", indexes = { @Index(name = "idx_value", columnNames = { "value" }) })
public abstract class Tag2 implements Serializable {

	private static final long serialVersionUID = 253815690158331276L;

	private Long id;

	private String value;

	private Boolean isAlias;

	@javax.persistence.Id
	@javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(nullable = false, length = 255)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Column(nullable = false)
	public Boolean isAlias() {
		return isAlias;
	}

	public void setAlias(Boolean isAlias) {
		this.isAlias = isAlias;
	}

	public boolean equals(Object arg0) {
		if (super.equals(arg0))
			return true;

		if (!(arg0 instanceof Tag2))
			return false;
		Tag2 t = (Tag2) arg0;
		if(getId()==null||t.getId()==null)
			return t.getValue().equals(getValue());
		
		return getId().equals(t.getId());
	}

}
