package com.ecosio;

import com.ecosio.dto.Link;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * High-level web crawler that launches and monitors the crawl lifecycle.
 * Delegates crawling to the {@link CrawlManager} and waits until the process completes or times out.
 */
public class WebCrawler {
    private final CrawlManager crawlManager;
    private final Duration timeout;

    public WebCrawler(CrawlManager crawlManager, Duration timeout) {
        this.crawlManager = crawlManager;
        this.timeout = timeout;
    }

    public CompletableFuture<List<Link>> startCrawling(String startUrl) {
        return crawlManager.startCrawling(startUrl)
                        .thenApply(_ -> getAllLinksSorted());
    }

    public List<Link> getAllLinksSorted() {
        return crawlManager.getAllLinks()
                .stream()
                .sorted(Comparator.comparing(Link::getUrl))
                .toList();
    }

}
