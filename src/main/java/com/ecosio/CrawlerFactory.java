package com.ecosio;

import com.ecosio.utility.WebUtility;

import java.net.MalformedURLException;
import java.time.Duration;

public class CrawlerFactory {
    public static WebCrawler create(String baseUrl, Duration timeout, int maxThreads, Boolean subDomainCheck) throws MalformedURLException {
        String domain = WebUtility.getDomain(baseUrl);
        WebsiteFetcher fetcher = new WebsiteFetcher();
        LinkExtractor extractor = new LinkExtractor();
        CrawlManager manager = new CrawlManager(fetcher, extractor, domain, subDomainCheck, maxThreads);
        return new WebCrawler(manager, timeout);
    }
}
