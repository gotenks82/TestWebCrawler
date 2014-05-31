package org.WebCrawler.Utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {
	public static URL verifyUrl(String url) throws MalformedURLException {
		// Only allow HTTP URLs.
		if (!url.toLowerCase().startsWith("http://"))
			return null;
		// Verify format of URL.
		URL verifiedUrl = null;
		try {
			verifiedUrl = new URL(url);
		} catch (Exception e) {
			return null;
		}
		return verifiedUrl;
	}

	public static String removeWww(String url) {
		int index = url.indexOf("://www.");
		if (index != -1) {
			return url.substring(0, index + 3) +
					url.substring(index + 7);
		}
		return (url);
	}
}
