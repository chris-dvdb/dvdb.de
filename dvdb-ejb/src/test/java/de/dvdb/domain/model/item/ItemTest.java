package de.dvdb.domain.model.item;

import junit.framework.TestCase;
import de.dvdb.domain.model.item.palace.PalaceDVDItem;
import de.dvdb.domain.model.item.type.Item;
import de.dvdb.infrastructure.amazon.AmazonServiceImpl;

public class ItemTest extends TestCase {

	public void testFormatNumber() {

		Item i = new PalaceDVDItem();
		assertEquals("abc", i.calcTitleLex("abc"));
		assertEquals("0000000 abc", i.calcTitleLex("0 abc"));
		assertEquals("0000000abc", i.calcTitleLex("0abc"));
		assertEquals("0000123 abc", i.calcTitleLex("123 abc"));
		assertEquals("0012345 abc", i.calcTitleLex("12345 abc"));
		assertEquals("0001234 abc", i.calcTitleLex("01234 abc"));
		assertEquals("0001234abc", i.calcTitleLex("01234/abc"));
		assertEquals("0123456789 abc", i.calcTitleLex("0123456789 abc"));
		assertEquals("012345678a9 abc", i.calcTitleLex("012345678a9 abc"));
		assertEquals("0000013abc123", i.calcTitleLex("013abc123"));
		assertEquals("0000024", i.calcTitleLex("24"));

	}
//
//	public void testGetAmazonItemLight() {
//		AmazonBridgeBean bb = new AmazonBridgeBean();
//		AmazonDVDItem dvd = bb.getAmazonDVDItemLight("B0026RHBSY");
//		assertNotNull(dvd);
//	}
//
//	public void testGetAmazonItemFull() {
//		AmazonBridgeBean bb = new AmazonBridgeBean();
//		AmazonDVDItem dvd = bb.getAmazonDVDItemFull("B0026RHBSY");
//		assertNotNull(dvd);
//	}
}
