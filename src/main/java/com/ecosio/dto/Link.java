package com.ecosio.dto;

public class Link {
    private final String label;
    private final String url;
    private final String normalizedUrl;

    public Link(String label, String url, String normalizedUrl) {
        this.label = label;
        this.url = url;
        this.normalizedUrl = normalizedUrl;
    }

    @Override
    public String toString() {
        return "Label: " + getShortLabel() + " | URL: " + getUrl() + " | Normalized URL: " + getNormalizedUrl();
    }

    public String getShortLabel() {
        return label.length() > 50 ? label.substring(0, 47) + "..." : label;
    }

    public String getUrl() {
        return url;
    }

    public String getLabel() {
        return label;
    }

    public String getNormalizedUrl() {
        return normalizedUrl;
    }
}
