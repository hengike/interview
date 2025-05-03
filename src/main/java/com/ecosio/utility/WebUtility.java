package com.ecosio.utility;

import java.net.URI;

public class WebUtility {

    public static String getDomain(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (host == null) {
                throw new IllegalArgumentException("URL does not have a valid host: " + url);
            }
            return host;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid URL: " + url, e);
        }
    }

    public static String normalizeUrl(String url) {
        try {
            URI uri = URI.create(url);
            StringBuilder normalized = new StringBuilder();

            normalized.append(uri.getScheme()).append("://").append(uri.getHost());

            int port = uri.getPort();
            if (port != -1 && port != defaultPort(uri.getScheme())) {
                normalized.append(":").append(port);
            }

            String path = uri.getPath();
            if (path != null && !path.isEmpty()) {
                normalized.append(path.replaceAll("/+$", ""));
            }

            return normalized.toString();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid URL for normalization: " + url, e);
        }
    }

    private static int defaultPort(String scheme) {
        return switch (scheme.toLowerCase()) {
            case "http" -> 80;
            case "https" -> 443;
            default -> -1;
        };
    }
}
