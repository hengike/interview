package com.ecosia;

import com.ecosio.JsonFileWriter;
import com.ecosio.dto.Link;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonFileWriterTest {

    @Test
    void testToJson_withEscaping() {
        List<Link> links = List.of(
                new Link("Regular Label", "http://example.com"),
                new Link("Quote \"Test\"", "http://test.com/path\"q"),
                new Link("Backslash\\Test", "http://site.com/\\path")
        );

        String json = JsonFileWriter.toJson(links);

        assertTrue(json.contains("\"label\": \"Quote \\\"Test\\\"\""));
        assertTrue(json.contains("\"url\": \"http://test.com/path\\\"q\""));
        assertTrue(json.contains("Backslash\\\\Test"));
        assertTrue(json.contains("http://site.com/\\\\path"));

        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
    }
}