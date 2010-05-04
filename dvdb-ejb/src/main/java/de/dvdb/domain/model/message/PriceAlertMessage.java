package de.dvdb.domain.model.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import de.dvdb.domain.model.pricing.Price;

@Entity
@DiscriminatorValue("4")
public class PriceAlertMessage extends TextMessage {

	private static final long serialVersionUID = -5040609100935760841L;

	private Price price;

	@javax.persistence.ManyToOne(optional = false)
	@javax.persistence.JoinColumn(name = "price_id")
	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}
}
