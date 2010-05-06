package de.dvdb.infrastructure;

import de.dvdb.infrastructure.http.HttpService;
import junit.framework.TestCase;

public class HttpServiceTest extends TestCase {

	public void _testRetrieveHttpDocument() {
		String result = HttpService
				.retrieveHttpDocument("http://www.google.de");
		assertTrue(result.indexOf("<html>") > 0);
	}
	
	public void testA() {}

}
