package com.ecosio;

import com.ecosio.dto.Link;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts hyperlinks from raw HTML content using regular expressions.
 * Filters links based on domain and subdomain inclusion rules.
 */
public class LinkExtractor {

    private static final Logger logger = Logger.getLogger(LinkExtractor.class.getName());

    // Simplified regex to extract anchor href and text
    private static final Pattern LINK_PATTERN = Pattern.compile(
            "<a\\b[^>]*href\\s*=\\s*\"([^\"]*)\"[^>]*>(.*?)</a>", Pattern.CASE_INSENSITIVE);

    private static final List<String> EXCLUDED_SCHEMES = List.of("mailto:", "javascript:", "tel:");

    public List<Link> extractLinks(String html, String baseUrl, String domain) throws MalformedURLException {
        List<Link> links = new ArrayList<>();
        Matcher matcher = LINK_PATTERN.matcher(html);

        while (matcher.find()) {
            String href = matcher.group(1).trim();
            String label = matcher.group(2).replaceAll("\\s+", " ").trim();

            if (shouldExclude(href)) continue;

            URL absoluteUrl = new URL(new URL(baseUrl), href);
            if (absoluteUrl.getHost().contains(domain)) {
                logger.fine("Added link from domain: " + absoluteUrl);
                links.add(new Link(
                        label.isEmpty() ? absoluteUrl.toString() : label,
                        absoluteUrl.toString()
                ));
            } else {
                logger.fine("Excluded link from different domain: " + absoluteUrl);
            }
        }

        return links;
    }

    private boolean shouldExclude(String href) {
        return EXCLUDED_SCHEMES.stream().anyMatch(href::startsWith);
    }
}
