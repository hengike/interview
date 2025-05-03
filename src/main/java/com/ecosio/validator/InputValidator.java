package com.ecosio.validator;

import java.net.URI;

public class InputValidator {

    /**
     * Validates the given URL string.
     *
     * @param urlString the URL to validate
     * @throws IllegalArgumentException if the URL is malformed or missing protocol/host
     */
    public static void validateUrl(String urlString) {
        try {
            URI uri = URI.create(urlString);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            if (scheme == null || host == null || host.isEmpty()) {
                throw new IllegalArgumentException("Missing scheme or host in URL: " + urlString);
            }

            // Optional: only allow HTTP/HTTPS
            if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                throw new IllegalArgumentException("Unsupported scheme: " + scheme);
            }

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid URL: " + urlString, e);
        }
    }
}
