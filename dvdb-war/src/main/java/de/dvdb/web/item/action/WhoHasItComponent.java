package de.dvdb.web.item.action;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.Item;

@Name("whoHasItComponent")
@Scope(ScopeType.PAGE)
public class WhoHasItComponent {

	@In
	Item item;

	@In
	protected EntityManager dvdb;

	@Logger
	protected Log log;

	@In(required = false)
	public void getOwners() {
		log.info("Getting owners for " + item.getTitle());
	}

}
