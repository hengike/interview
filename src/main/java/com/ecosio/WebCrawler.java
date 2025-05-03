package com.ecosio;

import com.ecosio.dto.Link;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

        for (Future<?> future : crawlManager.getFutures()) {
            Duration elapsed = Duration.between(start, Instant.now());
            Duration remaining = timeout.minus(elapsed);

            if (remaining.isNegative() || remaining.isZero()) {
                System.err.println("Crawler timeout reached before all tasks completed. Forcing shutdown.");
                crawlManager.shutdownNow();
                return;
            }

            try {
                future.get(remaining.toMillis(), TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                System.err.println("A task timed out within the overall crawler timeout. Forcing shutdown.");
                crawlManager.shutdownNow();
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrupted while waiting for tasks. Forcing shutdown.");
                crawlManager.shutdownNow();
                return;
            } catch (ExecutionException e) {
                System.err.println("A task failed with an exception: " + e.getCause());
                crawlManager.shutdownNow();
                return;
            }
        }

        System.out.println("All crawler tasks completed successfully within the timeout.");
        crawlManager.shutdownNow();
    }
}
