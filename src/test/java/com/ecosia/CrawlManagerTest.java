package com.ecosia;

import com.ecosio.CrawlManager;
import com.ecosio.LinkExtractor;
import com.ecosio.WebsiteFetcher;
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
        crawlFuture.get(1, TimeUnit.SECONDS);

        // THEN
        assertTrue(manager.isShutDown());
    }

    @Test
    public void testShutdownNow_StopsExecution() {
        manager.shutdownNow();
        assertTrue(manager.isShutDown());
    }

}
