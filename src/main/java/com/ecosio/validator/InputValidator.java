package com.ecosio.validator;

import java.net.MalformedURLException;
import java.net.URL;

public class InputValidator {

    /**
     * Validates the given URL string.
     *
     * @param urlString the URL to validate
     * @throws IllegalArgumentException if the URL is malformed or missing protocol/host
     */
    public static void validateUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            if (url.getProtocol() == null || url.getHost() == null || url.getHost().isEmpty()) {
                throw new MalformedURLException("Missing protocol or host.");
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL: " + urlString, e);
        }
    }
}
