package de.dvdb.domain.service;

import javax.ejb.Local;

import de.dvdb.domain.model.item.type.AmazonDVDItem;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.domain.model.pricing.Price;

/**
 * Ueber die AmazonBridge laeuft die Kommunikation mit Amazon. Sie abstrahiert
 * von dem zugrundeliegenden Amazon-Webservice und dessen POJOs.
 */
@Local
public interface AmazonService {

	public AmazonDVDItem getAmazonDVDItemLight(String asin);

	public AmazonDVDItem getAmazonDVDItemFull(String asin);

	public static final String SEARCHINDEX_VIDEOGAMES = "VideoGames";
	public static final String SEARCHINDEX_DVD = "DVD";

	// public String lookupAsinForEan(String ean, String searchIndex);

	/**
	 * Retrieves current price, availability, and maybe producturl from
	 * underlying data source
	 * 
	 * @param amazonItem
	 * @return
	 */
	public Price getCurrentPriceForItem(Item item);

}
