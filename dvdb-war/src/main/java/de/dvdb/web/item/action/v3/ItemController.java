package de.dvdb.web.item.action.v3;

import java.util.Date;

import javax.faces.event.ActionEvent;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.mediabase.MediabaseItemCollectible;

@Name("itemController")
@Scope(ScopeType.PAGE)
public class ItemController {

	@Logger
	Log log;

	@Out(required = false)
	Item selectedItem;
	
	@Out(scope = ScopeType.PAGE)
	MediabaseItemCollectible mic;
	
	@Create
	public void init() {
		mic = (MediabaseItemCollectible)Component.getInstance(MediabaseItemCollectible.class, true);
	}

	public void test(Object o) {

		selectedItem = (Item) o;
		log.info("work on " + o + " " + new Date());
		
	}
}
