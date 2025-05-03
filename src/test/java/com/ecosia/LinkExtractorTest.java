package com.ecosia;

import com.ecosio.LinkExtractor;
import com.ecosio.dto.Link;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LinkExtractorTest {

    private final LinkExtractor extractor = new LinkExtractor();

    @Test
    void testExtractValidLinks() {
        // GIVEN
        String html = "<a href=\"/page1\">Link 1</a> <a href=\"http://example.com/page2\">Link 2</a>";

        // WHEN
        List<Link> links = extractor.extractLinks(html, "http://example.com", "example.com", false);

        // THEN
        assertEquals(2, links.size());
        assertEquals("Link 1", links.getFirst().getLabel());
        assertEquals("http://example.com/page1", links.getFirst().getUrl());
    }

    @Test
    void testExcludeSchemes() {
        // GIVEN
        String html = """
                <a href="mailto:test@example.com">Email</a>
                <a href="javascript:void(0)">JS</a>
                <a href="tel:123456">Call</a>
                <a href="/home">Home</a>
                """;

        // WHEN
        List<Link> links = extractor.extractLinks(html, "http://example.com", "example.com", false);

        // THEN
        assertEquals(1, links.size());
        assertEquals("Home", links.getFirst().getLabel());
        assertEquals("http://example.com/home", links.getFirst().getUrl());
    }

    @Test
    void testExcludeExternalDomain() {
        // GIVEN
        String html = "<a href=\"http://external.com/page\">External</a>" +
                "<a href=\"http://example.com/page\">Internal</a>";

        // WHEN
        List<Link> links = extractor.extractLinks(html, "http://example.com", "example.com", false);

        // THEN
        assertEquals(1, links.size());
        assertEquals("Internal", links.getFirst().getLabel());
    }

    @Test
    void testEmptyLabelUsesUrl() {
        // GIVEN
        String html = "<a href=\"/page\"></a>";

        // WHEN
        List<Link> links = extractor.extractLinks(html, "http://example.com", "example.com", false);

        // THEN
        assertEquals(1, links.size());
        assertEquals("http://example.com/page", links.getFirst().getLabel());
    }

    @Test
    void testHandlesSpacesInText() {
        // GIVEN
        String html = "<a href=\"/space\">   This    is    spaced    </a>";

        // WHEN
        List<Link> links = extractor.extractLinks(html, "http://example.com", "example.com", false);

        // THEN
        assertEquals(1, links.size());
        assertEquals("This is spaced", links.getFirst().getLabel());
    }
}