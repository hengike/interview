package com.ecosio.dto;

public class Link {
    String label;
    String url;

    public Link(String label, String url) {
        this.label = label;
        this.url = url;
    }

    @Override
    public String toString() {
        return "Label: " + getLabel() + " | URL: " + getUrl();
    }

    public String getUrl() {
        return url;
    }

    public String getLabel() {
        return label;
    }

    public String getShortLabel() {
        return label.length() > 50 ? label.substring(0, 47) + "..." : label;
    }

}
