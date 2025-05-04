package com.ecosio;

import com.ecosio.dto.Link;
import com.ecosio.validator.InputValidator;

import java.time.Duration;
import java.util.List;

public class Main {

    public static final Duration TIMEOUT = Duration.ofSeconds(5);
    public static final int THREADS = 10;
    public static final boolean SUB_DOMAIN_CHECK = false;

    public static void main(String[] args) {
        String baseUrl = "https://ecosio.com/";  // Replace with actual URL
        if (args.length != 0) {
            baseUrl = args[0];
        }

        validate(baseUrl);
        CrawlerFactory.create(baseUrl, TIMEOUT, THREADS, SUB_DOMAIN_CHECK)
                .startCrawling(baseUrl)
                .thenAccept(Main::writeOutput)
                .join();

    }

    private static void writeOutput(List<Link> allLinks) {
        JsonFileWriter.writeLinksToJsonFile(allLinks, "links");
    }

    private static void validate(String baseUrl) {
        InputValidator.validateUrl(baseUrl);
    }
}