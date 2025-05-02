package com.ecosia;

import com.ecosio.CrawlManager;
import com.ecosio.LinkExtractor;
import com.ecosio.WebsiteFetcher;
import com.ecosio.dto.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CrawlManagerTest {

    private CrawlManager manager;

    @BeforeEach
    public void setUp() {
        manager = new CrawlManager(
                new WebsiteFetcher(), new LinkExtractor(), "example.com", 2);
    }

    @Test
    public void testAddLinks() {
        manager.startCrawling("https://example.com");
        manager.shutdownNow(); // skip real execution
        List<Link> links = manager.getAllLinks();
        assertNotNull(links);
    }
}
