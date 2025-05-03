package com.ecosio;

import com.ecosio.dto.Link;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Central coordinator of the crawling process.
 * <p>
 * This class:
 * <ul>
 *     <li>Initializes and manages the shared queue of URLs</li>
 *     <li>Submits {@link CrawlerWorker} tasks to an {@link ExecutorService}</li>
 *     <li>Keeps track of futures and controls the crawling lifecycle</li>
 * </ul>
 */
public class CrawlManager {

    private static final Logger logger = Logger.getLogger(CrawlManager.class.getName());

    private final WebsiteFetcher fetcher;
    private final LinkExtractor extractor;
    private final String domain;
    private final Boolean subDomainCheck;
    private final ExecutorService executor;
    private final int maxThreads;

    private final ConcurrentLinkedQueue<String> urlQueue;
    private final CopyOnWriteArrayList<Link> allLinks;
    private final List<Future<?>> futures;

    public CrawlManager(WebsiteFetcher fetcher, LinkExtractor extractor, String domain, Boolean subDomainCheck, int maxThreads) {
        this.fetcher = fetcher;
        this.extractor = extractor;
        this.domain = domain;
        this.subDomainCheck = subDomainCheck;
        this.maxThreads = maxThreads;
        this.urlQueue = new ConcurrentLinkedQueue<>();
        this.allLinks = new CopyOnWriteArrayList<>();
        this.executor = new ThreadPoolExecutor(
                maxThreads, maxThreads,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
        this.futures = new ArrayList<>();
    }

    /**
     * Starts the crawling process with the given start URL.
     *
     * @param startUrl the initial URL to crawl
     */
    public void startCrawling(String startUrl) {
        urlQueue.offer(startUrl);
        submitNewWorker(); // Submit the first worker
        logger.info("Started crawling with: " + startUrl);
    }

    /**
     * Submits a new {@link CrawlerWorker} task to the executor and tracks the future.
     */
    public void submitNewWorker() {
        Future<?> future = executor.submit(new CrawlerWorker(this));
        futures.add(future);
    }

    /**
     * Returns whether the crawling process has completed:
     * - the URL queue is empty
     * - no active threads are working
     */
    public boolean isFinished() {
        return urlQueue.isEmpty()
                && executor instanceof ThreadPoolExecutor tpe
                && tpe.getActiveCount() == 0;
    }

    /**
     * Indicates whether the executor has been shut down.
     *
     */
    public boolean isShutDown(){
        return executor.isShutdown();
    }

    /**
     * Forces immediate shutdown of the executor and running tasks.
     */
    public void shutdownNow() {
        logger.warning("Forced shutdown initiated.");
        executor.shutdownNow();
    }

    /**
     * Indicates whether a new worker should be spawned.
     * A new worker is allowed if:
     * - there are multiple URLs in the queue
     * - the thread pool is not fully utilized
     */
    public boolean shouldSpawnNew() {
        return urlQueue.size() > 2
                && executor instanceof ThreadPoolExecutor tpe
                && tpe.getActiveCount() < maxThreads;
    }

    // --- Getters ---

    public ConcurrentLinkedQueue<String> getUrlQueue() {
        return urlQueue;
    }

    public CopyOnWriteArrayList<Link> getAllLinks() {
        return allLinks;
    }

    public WebsiteFetcher getFetcher() {
        return fetcher;
    }

    public LinkExtractor getExtractor() {
        return extractor;
    }

    public String getDomain() {
        return domain;
    }

    public Boolean getSubDomainCheck() {
        return subDomainCheck;
    }

    public List<Future<?>> getFutures(){
        return this.futures;
    }
}
