package de.dvdb.domain.model.message;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("2")
public class GuestbookEntry extends TextMessage {

	private static final long serialVersionUID = -5040609100935760841L;

}
