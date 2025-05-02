package com.ecosio;

import com.ecosio.dto.Link;

import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * A worker that processes a URL from the queue, fetches its HTML content,
 * extracts links using {@link LinkExtractor}, and adds new, unvisited URLs back to the queue.
 * This class dynamically submits new worker instances if thread capacity and work remain.
 */
public class CrawlerWorker implements Runnable {
    private final WebsiteFetcher fetcher;
    private final LinkExtractor extractor;
    private final ConcurrentLinkedQueue<String> urlQueue;
    private final CopyOnWriteArrayList<Link> allLinks;
    private final String domain;
    private final ExecutorService executor;
    private final int maxThreads;

    private static final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();

    public CrawlerWorker(WebsiteFetcher fetcher, LinkExtractor extractor,
                         ConcurrentLinkedQueue<String> urlQueue,
                         CopyOnWriteArrayList<Link> allLinks,
                         String domain,
                         ExecutorService executor,
                         int maxThreads) {
        this.fetcher = fetcher;
        this.extractor = extractor;
        this.urlQueue = urlQueue;
        this.allLinks = allLinks;
        this.domain = domain;
        this.executor = executor;
        this.maxThreads = maxThreads;
    }

    @Override
    public void run() {
        while (!executor.isShutdown() && !urlQueue.isEmpty()) {
            String urlToProcess = urlQueue.poll();
            if (urlToProcess != null) {
                processUrl(urlToProcess);
            }

            if (urlQueue.size() > 2 && executor instanceof ThreadPoolExecutor tpe
                    && tpe.getActiveCount() < maxThreads) {
                executor.submit(new CrawlerWorker(fetcher, extractor, urlQueue, allLinks, domain, executor, maxThreads));
            }
        }
    }

    private void processUrl(String url) {
        try {
            String htmlContent = fetcher.fetchContentNew(url);
            List<Link> links = extractor.extractLinks(htmlContent, url, domain);
            allLinks.addAll(links);
            for (Link link : links) {
                if (visitedUrls.add(link.getUrl())) {
                    urlQueue.offer(link.getUrl());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to process: " + url + " - " + e.getMessage());
        }
    }
}
