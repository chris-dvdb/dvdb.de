package de.dvdb.domain.model.item;

import java.util.Date;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.IntervalDuration;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.user.User;

public interface ItemRepository {

	public static final String EVENT_ITEMREFRESHREQUIRED = "itemRefreshRequired";

	@Asynchronous
	@Transactional
	public void maintainItem(@Expiration Date date,
			@IntervalDuration Long interval);

	@Transactional
	public void maintainItemSync(Item i);

	@Asynchronous
	@Transactional
	public void maintainItemData(Item i);

	public void removeItem(Item item);

	public Item getItem(Long id, User user) throws ItemNotFoundException;

	public Item getItemByDvdId(Long dvdid, User user)
			throws ItemNotFoundException;

}
