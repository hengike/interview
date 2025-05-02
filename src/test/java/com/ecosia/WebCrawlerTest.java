package com.ecosia;

import com.ecosio.CrawlManager;
import com.ecosio.WebCrawler;
import com.ecosio.dto.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WebCrawlerTest {

    private CrawlManager crawlManagerMock;
    private WebCrawler webCrawler;

    @BeforeEach
    public void setUp() {
        crawlManagerMock = mock(CrawlManager.class);
        webCrawler = new WebCrawler(crawlManagerMock, Duration.ofSeconds(2));
    }

    @Test
    public void testStartCrawlingDelegatesToManager() {
        String url = "https://example.com";
        webCrawler.startCrawling(url);
        verify(crawlManagerMock).startCrawling(url);
    }

    @Test
    public void testGetAllLinksSortedReturnsSortedList() {
        Link l1 = new Link("https://b.com", "https://b.com");
        Link l2 = new Link("https://a.com", "https://a.com");

        when(crawlManagerMock.getAllLinks()).thenReturn(List.of(l1, l2));

        List<Link> sorted = webCrawler.getAllLinksSorted();
        assertEquals("https://a.com", sorted.get(0).getUrl());
        assertEquals("https://b.com", sorted.get(1).getUrl());
    }

    @Test
    public void testWaitForItToFinishSuccess() {
        when(crawlManagerMock.isFinished()).thenReturn(false, false, true);
        webCrawler.waitForItToFinish();
        verify(crawlManagerMock).shutdownExecutor();
    }

    @Test
    public void testWaitForItToFinishTimeout() {
        when(crawlManagerMock.isFinished()).thenReturn(false);
        webCrawler.waitForItToFinish();
        verify(crawlManagerMock).shutdownNow();
    }
}
