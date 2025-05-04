package com.ecosia;

import com.ecosio.CrawlManager;
import com.ecosio.LinkExtractor;
import com.ecosio.WebCrawler;
import com.ecosio.WebsiteFetcher;
import com.ecosio.dto.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WebCrawlerTest {

    private CrawlManager crawlManagerMock;
    private WebCrawler webCrawler;

    @BeforeEach
    public void setUp() {
        crawlManagerMock = mock(CrawlManager.class);
        when(crawlManagerMock.startCrawling(anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));
        webCrawler = new WebCrawler(crawlManagerMock, Duration.ofSeconds(2));
    }

    @Test
    public void testStartCrawlingDelegatesToManager() {
        String url = "https://example.com";
        webCrawler.startCrawling(url);
        verify(crawlManagerMock).startCrawling(url, Duration.ofSeconds(2));
    }

    @Test
    public void testGetAllLinksSortedReturnsSortedList() {
        // GIVEN
        CrawlManager realManager = new CrawlManager(mock(WebsiteFetcher.class), mock(LinkExtractor.class), "example.com", true, 2);
        Link l1 = new Link("https://b.com", "https://b.com");
        Link l2 = new Link("https://a.com", "https://a.com");
        realManager.getAllLinks().add(l1);
        realManager.getAllLinks().add(l2);

        WebCrawler webCrawler = new WebCrawler(realManager, Duration.ofMinutes(1));

        // WHEN
        List<Link> sorted = webCrawler.getAllLinksSorted();

        // THEN
        assertEquals("https://a.com", sorted.get(0).getUrl());
        assertEquals("https://b.com", sorted.get(1).getUrl());
    }

}
