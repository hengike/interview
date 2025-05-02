package com.ecosio;

import com.ecosio.dto.Link;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Main {

    public static final Duration TIMEOUT = Duration.ofSeconds(5);
    public static final int THREADS = 10;
    public static final boolean SUB_DOMAIN_CHECK = true;

    public static void main(String[] args) throws IOException, InterruptedException {
        String baseUrl = "https://orf.at/";  // Replace with actual URL
        if (args.length != 0) {
            baseUrl = args[0];
        }
        WebCrawler crawler = CrawlerFactory.create(baseUrl, TIMEOUT, THREADS, SUB_DOMAIN_CHECK);

        crawler.startCrawling(baseUrl);
        crawler.waitForItToFinish();

        List<Link> allLinks = crawler.getAllLinksSorted();
        JsonFileWriter.writeLinksToJsonFile(allLinks, "links");
    }
}