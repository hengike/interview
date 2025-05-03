package com.ecosio;

import com.ecosio.dto.Link;
import com.ecosio.validator.InputValidator;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class Main {

    public static final Duration TIMEOUT = Duration.ofSeconds(5);
    public static final int THREADS = 10;
    public static final boolean SUB_DOMAIN_CHECK = true;

    public static void main(String[] args) throws IOException {
        String baseUrl = "https://orf.at/";  // Replace with actual URL
        if (args.length != 0) {
            baseUrl = args[0];
        }

        validate(baseUrl);
        List<Link> allLinks = crawl(baseUrl);
        writeOutput(allLinks);
    }

    private static void writeOutput(List<Link> allLinks) {
        JsonFileWriter.writeLinksToJsonFile(allLinks, "links");
    }

    private static List<Link> crawl(String baseUrl) {
        WebCrawler crawler = CrawlerFactory.create(baseUrl, TIMEOUT, THREADS, SUB_DOMAIN_CHECK);
        crawler.startCrawling(baseUrl);
        crawler.waitForItToFinish();
        return crawler.getAllLinksSorted();
    }

    private static void validate(String baseUrl) {
        InputValidator.validateUrl(baseUrl);
    }
}