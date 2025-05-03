package com.ecosio;

import com.ecosio.dto.Link;
import com.ecosio.utility.WebUtility;

import java.net.URI;
import java.net.URISyntaxException;
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
    //private static final List<String> EXCLUDED_CHAR = List.of("#", "?", "&", "="); // TODO should I keep this?

    public List<Link> extractLinks(String html, String baseUrl, String domain, Boolean subDomainCheck) {
        List<Link> links = new ArrayList<>();
        Matcher matcher = LINK_PATTERN.matcher(html);

        URI baseUri;
        try {
            baseUri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid base URL: " + baseUrl, e);
        }

        while (matcher.find()) {
            String href = matcher.group(1).trim();
            String label = matcher.group(2).replaceAll("\\s+", " ").trim();

            if (shouldExclude(href)) continue;

            try {
                URI resolved = baseUri.resolve(href);
                String host = resolved.getHost();

                if (isMalformed(host)) continue;

                boolean domainMatches = subDomainCheck
                        ? host.contains(domain)
                        : host.equalsIgnoreCase(domain);

                if (domainMatches) {
                    logger.fine("Added link from domain: " + resolved);
                    links.add(new Link(
                            label.isEmpty() ? resolved.toString() : label,
                            resolved.toString(),
                            WebUtility.normalizeUrl(resolved.toString())
                    ));
                } else {
                    logger.fine("Excluded link from different domain: " + resolved);
                }

            } catch (IllegalArgumentException e) {
                logger.fine("Invalid href: " + href + " – " + e.getMessage());
            }
        }

        return links;
    }

    private static boolean isMalformed(String host) {
        return host == null;
    }

    private boolean shouldExclude(String href) {
        return EXCLUDED_SCHEMES.stream().anyMatch(href::startsWith);/* ||
        EXCLUDED_CHAR.stream().anyMatch(href::contains);*/ // TODO Should I keep this?
    }
}
