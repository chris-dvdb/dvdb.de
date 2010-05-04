package de.dvdb.infrastructure.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpService {

	private static Log log = LogFactory.getLog(HttpService.class);

	public static String retrieveHttpDocument(String url) {
		log.info("Accessing " + url);

		HttpClient client = new HttpClient();
		// establish a connection within 5 seconds
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);

		String responseBody = null;
		HttpMethod method = null;

		try {
			method = new GetMethod(url);
			client.executeMethod(method);
			responseBody = method.getResponseBodyAsString();
		} catch (Exception e) {
			responseBody = null;
		} finally {
			if (method != null)
				method.releaseConnection();
		}
		return responseBody;

	}

	private HttpService() {
	}
}
