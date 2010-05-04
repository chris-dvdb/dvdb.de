package de.dvdb.domain.model.tag;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Index;

import de.dvdb.domain.model.item.Item;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "relationship", discriminatorType = DiscriminatorType.STRING)
@javax.persistence.Table(name = "dvdb2_tag")
@org.hibernate.annotations.Table(appliesTo = "dvdb2_tag", indexes = { @Index(name = "idx_value", columnNames = { "value" }) })
public class Tag implements Serializable {

	private static final long serialVersionUID = 253815690158331276L;

	private Long id;

	private Item item;

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

	@ManyToOne(targetEntity = Item.class)
	@JoinColumn(name = "item_id", nullable = false)
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	public boolean equals(Object arg0) {
		if (super.equals(arg0))
			return true;

		if (!(arg0 instanceof Tag))
			return false;
		Tag t = (Tag) arg0;
		return getId().equals(t.getId());
	}

}
