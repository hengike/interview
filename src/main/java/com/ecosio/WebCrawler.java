package com.ecosio;

import com.ecosio.dto.Link;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

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

    public void startCrawling(String startUrl) {
        crawlManager.startCrawling(startUrl);
    }

    public List<Link> getAllLinksSorted() {
        return crawlManager.getAllLinks()
                .stream()
                .sorted(Comparator.comparing(Link::getUrl))
                .toList();
    }

    public void waitForItToFinish() {
        Instant start = Instant.now();
        while (true) {
            if (crawlManager.isFinished()) {
                crawlManager.shutdownExecutor();
                break;
            }

            Duration elapsed = Duration.between(start, Instant.now());
            if (elapsed.compareTo(timeout) > 0) {
                System.err.println("Crawler timeout reached. Forcing shutdown.");
                crawlManager.shutdownNow();
                break;
            } else {
                Duration remaining = timeout.minus(elapsed);
                System.out.println("Crawler still running. Remaining time: " + remaining.toSeconds() + " seconds");
            }

            try {
                Thread.sleep(100); // TODO busy-waiting, we should move to Future implementation?
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
