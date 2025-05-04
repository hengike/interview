package com.ecosia;

import com.ecosio.CrawlManager;
import com.ecosio.LinkExtractor;
import com.ecosio.WebsiteFetcher;
import com.ecosio.dto.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CrawlManagerTest {

    private WebsiteFetcher fetcher;
    private LinkExtractor extractor;
    private CrawlManager manager;

    @BeforeEach
    public void setUp() {
        // GIVEN
        fetcher = mock(WebsiteFetcher.class);
        extractor = mock(LinkExtractor.class);

        // WHEN
        manager = new CrawlManager(fetcher, extractor, "example.com", true, 2);
    }


    @Test
    public void testGettersReturnNonNullValues() {
        // THEN
        assertNotNull(manager.getAllLinks());
        assertEquals(fetcher, manager.getFetcher());
        assertEquals(extractor, manager.getExtractor());
        assertEquals("example.com", manager.getDomain());
        assertTrue(manager.getSubDomainCheck());
    }

    @Test
    public void testSubmitNewWorker_CompletesCrawlWithOneLink() throws Exception {
        // GIVEN
        String url = "https://example.com";
        String html = "<a href=\"/about\">About Us</a>";

        when(fetcher.fetchContent(url)).thenReturn(html);
        when(extractor.extractLinks(eq(html), eq(url), eq("example.com"), eq(true)))
                .thenReturn(Collections.singletonList(new Link("About Us", "https://example.com/about")));

        // WHEN
        CompletableFuture<?> crawlFuture = manager.startCrawling(url, Duration.ofSeconds(5));
        crawlFuture.get(3, TimeUnit.SECONDS);

        // THEN
        assertTrue(manager.isFinished());
        assertTrue(manager.isShutDown());
    }

    @Test
    public void testCrawlSkipsDuplicateLinks() throws Exception {
        // GIVEN
        String url = "https://example.com";
        String html = "<a href=\"/about\">About Us</a>";

        // All links point back to /about
        when(fetcher.fetchContent(anyString())).thenReturn(html);
        when(extractor.extractLinks(anyString(), anyString(), anyString(), anyBoolean()))
                .thenReturn(Collections.singletonList(new Link("About Us", "https://example.com/about")));

        // WHEN
        CompletableFuture<?> crawlFuture = manager.startCrawling(url, Duration.ofSeconds(5));
        crawlFuture.get(3, TimeUnit.SECONDS);

        // THEN
        assertTrue(manager.isFinished());
        assertTrue(manager.isShutDown());
    }

    @Test
    public void testTimeoutTriggersShutdown() throws Exception {
        // GIVEN
        String url = "https://example.com";
        when(fetcher.fetchContent(url)).thenAnswer(_ -> {
            Thread.sleep(3000); // delay fetch to force timeout
            return "<html></html>";
        });
        when(extractor.extractLinks(anyString(), anyString(), anyString(), anyBoolean()))
                .thenReturn(Collections.emptyList());

        // WHEN
        CompletableFuture<?> crawlFuture = manager.startCrawling(url, Duration.ofMillis(500));
        crawlFuture.get(2, TimeUnit.SECONDS);

        // THEN
        assertTrue(manager.isShutDown());
    }

    @Test
    public void testShutdownNow_StopsExecution() {
        manager.shutdownNow();
        assertTrue(manager.isShutDown());
    }

}
