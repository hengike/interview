package com.ecosia.integration;

import com.ecosio.CrawlerFactory;
import com.ecosio.JsonFileWriter;
import com.ecosio.WebCrawler;
import com.ecosio.dto.Link;
import fi.iki.elonen.NanoHTTPD;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class WebCrawlerIntegrationTest {

    private static TestWebServer server;
    private static final int PORT = 8000;
    private static final String OUTPUT_FILE = "test-links.json";

    @BeforeAll
    static void startServer() throws IOException {
        server = new TestWebServer(PORT);
        server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void testCrawlLocalSiteAndGenerateJson() throws MalformedURLException {
        String startUrl = "http://localhost:" + PORT + "/index.html";
        String domain = "http://localhost";

        WebCrawler crawler = CrawlerFactory.create(domain, Duration.ofSeconds(5), 2, true);
        crawler.startCrawling(startUrl);
        crawler.waitForItToFinish();
        List<Link> links = crawler.getAllLinksSorted();

        String outputWithTimestamp = JsonFileWriter.writeLinksToJsonFile(links, OUTPUT_FILE);

        assertTrue(new File(outputWithTimestamp).exists(), "JSON output file should be created");

        // Simple verification
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(outputWithTimestamp));
            String json = String.join("", lines);
            assertTrue(json.contains("page1.html"), "JSON should contain crawled link to page1.html");
        } catch (IOException e) {
            fail("Failed to read JSON output");
        }
    }
}

