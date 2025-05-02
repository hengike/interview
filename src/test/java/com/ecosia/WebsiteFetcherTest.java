package com.ecosia;

import com.ecosio.WebsiteFetcher;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

public class WebsiteFetcherTest {

    private static HttpServer server;
    private static String baseUrl;
    private WebsiteFetcher fetcher;

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0); // 0 = random free port
        int port = server.getAddress().getPort();
        baseUrl = "http://localhost:" + port;

        server.createContext("/ok", exchange -> respond(exchange, 200, "Success response"));
        server.createContext("/error", exchange -> respond(exchange, 500, "Server error"));
        server.createContext("/slow", exchange -> {
            try {
                Thread.sleep(5000); // simulate delay > timeout
                respond(exchange, 200, "Late response");
            } catch (InterruptedException e) {
                exchange.sendResponseHeaders(500, -1);
            }
        });

        server.setExecutor(null); // default executor
        server.start();
    }

    @AfterAll
    static void stopServer() {
        server.stop(0);
    }

    @BeforeEach
    void setUp() {
        fetcher = new WebsiteFetcher();
    }

    @Test
    void fetchContentNew_returnsContent_on200OK() throws Exception {
        // GIVEN
        String url = baseUrl + "/ok";

        // WHEN
        String result = fetcher.fetchContentNew(url);

        // THEN
        assertEquals("Success response", result);
    }

    @Test
    void fetchContentNew_throwsIOException_onNon200() {
        // GIVEN
        String url = baseUrl + "/error";

        // WHEN & THEN
        IOException ex = assertThrows(IOException.class, () ->
                fetcher.fetchContentNew(url)
        );
        assertTrue(ex.getMessage().contains("Wrong HTTP response: 500"));
    }

    @Test
    void fetchContentNew_throwsIOException_onTimeout() {
        // GIVEN
        String url = baseUrl + "/slow";

        // WHEN & THEN
        assertThrows(IOException.class, () ->
                fetcher.fetchContentNew(url)
        );
    }

    private static void respond(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] response = body.getBytes();
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }
}
