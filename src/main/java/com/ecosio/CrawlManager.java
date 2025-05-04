package com.ecosio;

import com.ecosio.dto.Link;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
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
    private static final Set<Link> visitedUrls = ConcurrentHashMap.newKeySet();

    private final List<Link> allLinks;
    private final CompletableFuture<?> future;
    private final AtomicLong counter = new AtomicLong();
    private final Timer timer;

    public CrawlManager(WebsiteFetcher fetcher, LinkExtractor extractor, String domain, Boolean subDomainCheck, int maxThreads) {
        this.fetcher = fetcher;
        this.extractor = extractor;
        this.domain = domain;
        this.subDomainCheck = subDomainCheck;

        this.allLinks = Collections.synchronizedList(new ArrayList<>());
        this.executor = new ThreadPoolExecutor(
                maxThreads, maxThreads,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()/*,
                Thread.ofVirtual().factory() // from java 24 -> lot of IO (HTTP call) and not much calculation, and no IO in synchronized block*/
        );
        this.future = new CompletableFuture<Void>();
        // Alternatively, we could use scheduler = Executors.newSingleThreadScheduledExecutor();
        timer = new Timer(true);
    }

    /**
     * Starts the crawling process with the given start URL.
     *
     * @param startUrl the initial URL to crawl
     */
    public CompletableFuture<?> startCrawling(String startUrl, Duration timeout) {
        setUpTimeoutTask(timeout);
        submitNewWorker(new Link(startUrl, startUrl)); // Submit the first worker
        logger.info("Started crawling with: " + startUrl);
        return future;
    }

    /**
     * Submits a new {@link CrawlerWorker} task to the executor and tracks the future.
     */
    public void submitNewWorker(Link url) {
        if (!visitedUrls.add(url)) {
            logger.warning("Loop detected, skip URL: " + url);
            return;
        }
        counter.incrementAndGet();
        CompletableFuture
                .runAsync(() -> new CrawlerWorker(this).run(url.getUrl()), executor)
                .thenAccept(_ -> {
                    if (counter.decrementAndGet() == 0) {
                        future.complete(null);
                        shutdownNow();
                    }
                });
    }

    private void setUpTimeoutTask(Duration timeout) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isFinished()) {
                    logger.warning("Timeout Called! Remained: " + counter.get());
                    executor.shutdownNow();
                    future.complete(null);
                }
            }
        }, timeout.toMillis());
    }

    /**
     * Returns whether the crawling process has completed:
     * - the URL queue is empty
     * - no active threads are working
     */
    public boolean isFinished() {
        return counter.get() == 0;
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

    public List<Link> getAllLinks() {
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
}
