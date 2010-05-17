package de.dvdb.web.item.action.v3;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.type.Item;

@Name("itemController")
@Scope(ScopeType.PAGE)
public class ItemController {

	@Logger
	Log log;

	@Out(required = false)
	Item selectedItem;

	public void doIt() {
		log.info("Do it on {0}", selectedItem);
	}

	public void test(Object o) {

		selectedItem = (Item) o;
		log.info("work on " + selectedItem);

	}

}
