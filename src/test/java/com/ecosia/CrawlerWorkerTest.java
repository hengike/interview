package com.ecosia;

import com.ecosio.CrawlManager;
import com.ecosio.CrawlerWorker;
import com.ecosio.LinkExtractor;
import com.ecosio.WebsiteFetcher;
import com.ecosio.dto.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CrawlerWorkerTest {

    private WebsiteFetcher fetcher;
    private LinkExtractor extractor;
    private CrawlManager crawlManager;
    private ConcurrentLinkedQueue<String> urlQueue;
    private CopyOnWriteArrayList<Link> allLinks;

    @BeforeEach
    public void setup() {
        fetcher = mock(WebsiteFetcher.class);
        extractor = mock(LinkExtractor.class);
        crawlManager = mock(CrawlManager.class);

        urlQueue = new ConcurrentLinkedQueue<>();
        allLinks = new CopyOnWriteArrayList<>();
    }

    @Test
    public void testWorkerProcessesUrlAndExtractsLinks() throws Exception {
        // GIVEN
        String startUrl = "https://example.com";
        String html = "<a href='https://example.com/page1'>Link</a>";
        Link link = new Link("https://example.com/page1", "https://example.com/page1", "https://example.com/page1");

        urlQueue.offer(startUrl);

        when(crawlManager.getUrlQueue()).thenReturn(urlQueue);
        when(crawlManager.getFetcher()).thenReturn(fetcher);
        when(crawlManager.getExtractor()).thenReturn(extractor);
        when(crawlManager.getDomain()).thenReturn("example.com");
        when(crawlManager.getSubDomainCheck()).thenReturn(true);
        when(crawlManager.getAllLinks()).thenReturn(allLinks);
        when(fetcher.fetchContent(startUrl)).thenReturn(html);
        when(extractor.extractLinks(html, startUrl, "example.com", true)).thenReturn(List.of(link));

        CrawlerWorker worker = new CrawlerWorker(crawlManager);

        // WHEN
        Thread thread = new Thread(worker);
        thread.start();
        thread.join(500); // wait up to 500ms

        // THEN
        assertTrue(allLinks.contains(link));
    }
}
