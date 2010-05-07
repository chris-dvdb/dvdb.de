package de.dvdb.domain.model.pricing.robot.alphamusic;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;

import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.pricing.Price;
import de.dvdb.domain.model.pricing.Shop;
import de.dvdb.domain.model.pricing.robot.AbstractPriceRobot;
import de.dvdb.domain.model.pricing.robot.RobotException;

@Stateless
@Name("alphaPriceFetcher")
@Local(AlphaPriceFetcher.class)
public class AlphaPriceFetcherImpl extends AbstractPriceRobot implements
		AlphaPriceFetcher, Serializable {

	private static final long serialVersionUID = -2222102720904149239L;

	public Price fetchRawPrice(Item item, Shop shop) throws RobotException {

		if (item.getEan() == null || item.getEan().length() < 7)
			return null;

		String documentURL = String
				.format(
						"http://www.alphamusic.de/partner?command=item&pid=affili&pid_ref=187287&ean=%s",
						item.getEan());
		String xmlDocument = retrieveHttpDocument(documentURL);

		if (xmlDocument == null)
			return null;

		Double price = parsePrice(xmlDocument);
		String url = getTagText(xmlDocument, "url");
		String lieferstatus = getTagText(xmlDocument, "lieferstatus");

		if (price == null || url == null)
			return null;

		Price alphaPrice = new Price();
		alphaPrice.setUrl(url);
		alphaPrice.setAvailability(lieferstatus);
		alphaPrice.setPrice(price);
		alphaPrice.setValidForTPG(true);
		alphaPrice.setAvailableOnline(true);
		alphaPrice.setDateOfPriceValidation(new Date());
		return alphaPrice;
	}

	private Double parsePrice(String xmlDocument) {
		String priceText = getTagText(xmlDocument, "preis");
		if (priceText == null)
			return null;
		Double price = new Double(priceText);
		return price;
	}
}
