package de.dvdb.domain.model.pricing.robot;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.pricing.Price;
import de.dvdb.domain.model.pricing.Shop;

public interface PriceRobot {

	public abstract Price fetchRawPrice(Item item, Shop shop)
			throws RobotException;

}
