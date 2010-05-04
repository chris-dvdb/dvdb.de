package de.dvdb.domain.model.pricing.robot.amazon;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;

import de.dvdb.domain.model.item.Item;
import de.dvdb.domain.model.pricing.Price;
import de.dvdb.domain.model.pricing.Shop;
import de.dvdb.domain.model.pricing.robot.AbstractPriceRobot;
import de.dvdb.domain.model.pricing.robot.RobotException;
import de.dvdb.domain.service.AmazonService;

@Stateless
@Name("amazonPriceFetcher")
@Local(AmazonPriceFetcher.class)
public class AmazonPriceFetcherImpl extends AbstractPriceRobot implements
		AmazonPriceFetcher, Serializable {

	private static final long serialVersionUID = -2222102720904149239L;

	@EJB
	AmazonService amazonBridge;

	public Price fetchRawPrice(Item item, Shop shop) throws RobotException {

		if (item.getAsin() == null || item.getAsin().equals(""))
			return null;

		Price p = amazonBridge.getCurrentPriceForItem(item);
		if (p == null)
			throw new RobotException();

		p.setValidForTPG(false);
		p.setDateOfPriceValidation(new Date());
		p.setAvailableOnline(true);
		return p;
	}
}
