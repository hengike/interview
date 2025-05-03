package com.ecosio;

import com.ecosio.dto.Link;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A worker that processes URLs from the shared queue. For each URL, it:
 * <ul>
 *     <li>Fetches the HTML content via {@link WebsiteFetcher}</li>
 *     <li>Extracts links using {@link LinkExtractor}</li>
 *     <li>Adds the extracted links to the shared collection of all links</li>
 *     <li>Enqueues unvisited URLs back to the queue</li>
 * </ul>
 * This worker may dynamically submit new worker instances if there are pending URLs
 * and thread capacity is available, as determined by the {@link CrawlManager}.
 */
public class CrawlerWorker implements Runnable {

    private static final Logger logger = Logger.getLogger(CrawlerWorker.class.getName());
    private static final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();

    private final CrawlManager crawlManager;

    public CrawlerWorker(CrawlManager crawlManager) {
        this.crawlManager = crawlManager;
    }

    @Override
    public void run() {
        while (!crawlManager.isFinished() && !crawlManager.isShutDown()) {
            String urlToProcess = crawlManager.getUrlQueue().poll();
            if (urlToProcess != null) {
                processUrl(urlToProcess);
            }

            if (crawlManager.shouldSpawnNew()) {
                crawlManager.submitNewWorker();
            }
        }
    }

    private void processUrl(String url) {
        try {
            String htmlContent = crawlManager.getFetcher().fetchContent(url);
            List<Link> links = crawlManager.getExtractor().extractLinks(
                    htmlContent, url, crawlManager.getDomain(), crawlManager.getSubDomainCheck()
            );
            crawlManager.getAllLinks().addAll(links);
            for (Link link : links) {
                if (visitedUrls.add(link.getUrl())) {
                    crawlManager.getUrlQueue().offer(link.getUrl());
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to process URL: " + url, e);
        }
    }
}
