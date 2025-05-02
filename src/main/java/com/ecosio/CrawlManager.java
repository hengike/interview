package com.ecosio;

import com.ecosio.dto.Link;

import java.util.List;
import java.util.concurrent.*;

/**
 * Central coordinator of the crawling process.
 * Initializes the crawling queue, manages the thread pool executor,
 * and submits {@link CrawlerWorker} tasks to process URLs.
 */
public class CrawlManager {
    private final WebsiteFetcher fetcher;
    private final LinkExtractor extractor;
    private final String domain;
    private final ExecutorService executor;
    private final int maxThreads;
    private final ConcurrentLinkedQueue<String> urlQueue;
    private final CopyOnWriteArrayList<Link> allLinks;

    public CrawlManager(WebsiteFetcher fetcher, LinkExtractor extractor, String domain, int maxThreads) {
        this.fetcher = fetcher;
        this.extractor = extractor;
        this.domain = domain;
        this.maxThreads = maxThreads;
        this.urlQueue = new ConcurrentLinkedQueue<>();
        this.allLinks = new CopyOnWriteArrayList<>();
        this.executor = new ThreadPoolExecutor(maxThreads, maxThreads, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }

    public void startCrawling(String startUrl) {
        urlQueue.offer(startUrl);
        executor.submit(new CrawlerWorker(fetcher, extractor, urlQueue, allLinks, domain, executor, maxThreads));
    }

    public List<Link> getAllLinks() {
        return allLinks;
    }

    public boolean isFinished() {
        return urlQueue.isEmpty() && ((ThreadPoolExecutor) executor).getActiveCount() == 0;
    }

    public void shutdownExecutor() {
        executor.shutdown();
    }

    public void shutdownNow() {
        executor.shutdownNow();
    }
}
