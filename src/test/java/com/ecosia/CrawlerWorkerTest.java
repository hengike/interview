package com.ecosia;

import com.ecosio.CrawlerWorker;
import com.ecosio.LinkExtractor;
import com.ecosio.WebsiteFetcher;
import com.ecosio.dto.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class CrawlerWorkerTest {

    private WebsiteFetcher fetcher;
    private LinkExtractor extractor;
    private ConcurrentLinkedQueue<String> urlQueue;
    private CopyOnWriteArrayList<Link> allLinks;
    private ExecutorService executor;

    @BeforeEach
    public void setup() {
        fetcher = mock(WebsiteFetcher.class);
        extractor = mock(LinkExtractor.class);
        urlQueue = new ConcurrentLinkedQueue<>();
        allLinks = new CopyOnWriteArrayList<>();
        executor = Executors.newFixedThreadPool(2);
    }

    @Test
    public void testWorkerProcessesUrlAndExtractsLinks() throws InterruptedException, IOException {
        String startUrl = "https://example.com";
        String html = "<a href='https://example.com/page1'>Link</a>";

        Link link = new Link("https://example.com/page1", "https://example.com/page1");
        when(fetcher.fetchContentNew(startUrl)).thenReturn(html);
        when(extractor.extractLinks(html, startUrl, "example.com"))
                .thenReturn(List.of(link));

        urlQueue.offer(startUrl);
        Runnable worker = new CrawlerWorker(fetcher, extractor, urlQueue, allLinks, "example.com", executor, 2);
        executor.submit(worker);

        TimeUnit.MILLISECONDS.sleep(200); // let the worker do its job
        executor.shutdownNow();

        assertTrue(allLinks.contains(link));
    }
}
