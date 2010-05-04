package de.dvdb.domain.shared;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {

	public static String ArrayToCSV(String[] array) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < array.length; i++) {
			String string = array[i];
			if (i == 0)
				sb.append(string);
			else
				sb.append("," + string);
		}
		return sb.toString();
	}

	public static String getMD5(String plain) {

		byte[] defaultBytes = plain.getBytes();
		try {
			MessageDigest algorithm = MessageDigest.getInstance("MD5");
			algorithm.reset();
			algorithm.update(defaultBytes);
			byte messageDigest[] = algorithm.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String hex = Integer.toHexString(0xFF & messageDigest[i]);
				if (hex.length() == 1)
					hexString.append('0');

				hexString.append(hex);
			}
			return hexString + "";
		} catch (NoSuchAlgorithmException nsae) {

		}
		return null;
	}

}
