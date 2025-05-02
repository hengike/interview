package com.ecosio.utility;

import java.net.MalformedURLException;
import java.net.URL;

public class WebUtility {
    public static String getDomain(String url) throws MalformedURLException {
        URL u = new URL(url);
        return u.getHost();
    }
}
