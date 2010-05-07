package de.dvdb.domain.model.pricing.robot;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.pricing.Price;
import de.dvdb.domain.model.pricing.Shop;
import de.dvdb.infrastructure.http.HttpService;

public abstract class AbstractPriceRobot implements PriceRobot {

	public abstract Price fetchRawPrice(Item item, Shop shop)
			throws RobotException;

	protected String getTagText(String xmlDocument, String tag) {
		int po = xmlDocument.indexOf("<" + tag + ">");
		if (po == -1)
			return null;
		int pc = xmlDocument.indexOf("</" + tag + ">", po);
		if (pc == -1)
			return null;
		return xmlDocument.substring(po + 2 + tag.length(), pc);
	}

	protected String retrieveHttpDocument(String url) {
		return HttpService.retrieveHttpDocument(url);
	}

}
