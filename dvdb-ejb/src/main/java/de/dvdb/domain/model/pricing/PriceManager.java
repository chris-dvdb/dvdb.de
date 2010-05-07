package de.dvdb.domain.model.pricing;

import de.dvdb.domain.model.item.type.Item;

public interface PriceManager {

	public static final String USERNAME_REPORTER = "Luise";

	public static final String EVENT_PRICECHANGED = "pricechanged";

	/**
	 * Updates item prices with latest price from shop.
	 * 
	 * @param item
	 *            AmazonItem to update. If null -> update oldest item
	 */
	// @Asynchronous
	public Item updatePrices(Item item, boolean forceUpdate);

	public Item updatePricesSync(Item item, boolean forceUpdate);

	public void removePrice(Item item, Price price, boolean decreaseReported);

	public void addPrice(Item item, Price price, boolean increaseReported);

	/**
	 * Removes expired prices from database for all amazonItems.
	 * 
	 * @param item
	 */
	public void cleanUpExpiredPrices();

	public void invertFlagPriceDeal(Price price);

}
