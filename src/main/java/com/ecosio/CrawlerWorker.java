package com.ecosio;

import com.ecosio.dto.Link;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A worker that processes URLs from the shared queue. For each URL, it:
 * <ul>
 *     <li>Fetches the HTML content via {@link WebsiteFetcher}</li>
 *     <li>Extracts links using {@link LinkExtractor}</li>
 *     <li>Adds the extracted links to the shared collection of all links</li>
 * </ul>
 * This worker may dynamically submit new worker instances if there are pending URLs,
 * as determined by the {@link CrawlManager}.
 */
public class CrawlerWorker {

    private static final Logger logger = Logger.getLogger(CrawlerWorker.class.getName());

    private final CrawlManager crawlManager;

    public CrawlerWorker(CrawlManager crawlManager) {
        this.crawlManager = crawlManager;
    }

    public void run(String urlToProcess) {
        if (urlToProcess != null) {
            processUrl(urlToProcess);
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
                crawlManager.submitNewWorker(link);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to process URL: " + url, e);
        }
    }
}
