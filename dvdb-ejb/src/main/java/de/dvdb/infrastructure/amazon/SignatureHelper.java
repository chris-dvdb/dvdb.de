package de.dvdb.infrastructure.amazon;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureHelper {
	private static final String UTF8_CHARSET = "UTF-8";
	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

	private String awsSecretKey = "90Ku8CdV+WUnfbzvLmn23yxtKWP9kq0vR7V73v/t";

	private SecretKeySpec secretKeySpec = null;
	private Mac mac = null;

	public SignatureHelper() throws Exception {
		byte[] secretyKeyBytes = awsSecretKey.getBytes(UTF8_CHARSET);
		secretKeySpec = new SecretKeySpec(secretyKeyBytes,
				HMAC_SHA256_ALGORITHM);
		mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
		mac.init(secretKeySpec);
	}

	public String sign(String stringToSign) {
		String signature = null;
		byte[] data;
		byte[] rawHmac;
		try {
			data = stringToSign.getBytes(UTF8_CHARSET);
			rawHmac = mac.doFinal(data);
			signature = new String(Base64.encodeBytes(rawHmac));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
		}
		return signature;
	}

	public String getTimestamp() {
		String timestamp = null;
		Calendar cal = Calendar.getInstance();
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
		timestamp = dfm.format(cal.getTime());
		return timestamp;
	}

}
