package com.ecosio.utility;

import java.net.MalformedURLException;
import java.net.URL;

public class WebUtility {
    public static String getDomain(String url) throws MalformedURLException {
        URL u = new URL(url);
        return u.getHost();
    }

    public static String normalizeUrl(URL url) {
        String normalized = url.getProtocol() + "://" + url.getHost();

        if (url.getPort() != -1 && url.getPort() != url.getDefaultPort()) {
            normalized += ":" + url.getPort();
        }

        normalized += url.getPath().replaceAll("/+$", ""); // remove trailing slash(es)

        return normalized;
    }
}
