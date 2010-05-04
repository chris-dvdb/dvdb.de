package de.dvdb.domain.model.pricing.robot.jpc;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Name;

import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.pricing.Price;
import de.dvdb.domain.model.pricing.Shop;
import de.dvdb.domain.model.pricing.robot.AbstractPriceRobot;
import de.dvdb.domain.model.pricing.robot.RobotException;

@Stateless
@Name("jpcPriceFetcher")
@Local(JPCPriceFetcher.class)
public class JPCPriceFetcherImpl extends AbstractPriceRobot implements
		JPCPriceFetcher, Serializable {

	private static final long serialVersionUID = -2222102720904149239L;

	@PersistenceContext(unitName = "dvdb")
	EntityManager dvdb;

	public Price fetchRawPrice(Item item, Shop shop) throws RobotException {

		if (item.getEan() == null || item.getEan().length() < 7)
			return null;

		String documentURL = String.format(
				"http://www.jpc.de/jpcng/home/xml/-/task/detail/hnum/%s", item
						.getEan());
		String xmlDocument = retrieveHttpDocument(documentURL);

		if (xmlDocument == null)
			return null;

		Double price = parsePrice(xmlDocument);
		String lagerbestand = getTagText(xmlDocument, "lagerbestand");

		if (price == null)
			return null;

		Price jpcPrice = new Price();
		jpcPrice.setUrl(String.format(
				"http://www.jpc.de/jpcng/home/detail/-/hnum/%s/iampartner/k92",
				item.getEan()));
		jpcPrice.setPrice(price);
		jpcPrice.setAvailability(lagerbestand);
		jpcPrice.setValidForTPG(true);
		jpcPrice.setDateOfPriceValidation(new Date());
		jpcPrice.setAvailableOnline(true);
		return jpcPrice;
	}

	private Double parsePrice(String xmlDocument) {
		String priceText = getTagText(xmlDocument, "preis");
		if (priceText == null)
			return null;
		Double price = new Double(priceText);
		if (price.doubleValue() == 0)
			return null;
		return price;
	}
}
