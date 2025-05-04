package com.ecosio.dto;

import java.util.Objects;

import static com.ecosio.utility.WebUtility.normalizeUrl;

public class Link {
    private final String label;
    private final String url;
    private final String normalizedUrl;

    public Link(String label, String url) {
        this.label = label;
        this.url = url;
        this.normalizedUrl = getNormalizedUr(url);
    }

    private String getNormalizedUr(final String url) {
        try {
            return normalizeUrl(url);
        } catch (IllegalArgumentException e) {
            return url;
        }
    }

    @Override
    public String toString() {
        return "Label: " + getShortLabel() + " | URL: " + getUrl() + " | Normalized URL: " + getNormalizedUrl();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Link link = (Link) o;
        return Objects.equals(getNormalizedUrl(), link.getNormalizedUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getNormalizedUrl());
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
