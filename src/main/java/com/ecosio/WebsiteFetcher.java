package com.ecosio;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.logging.Logger;

/**
 * Fetches website content using Java's {@link HttpClient}, handling redirects and timeouts.
 */
public class WebsiteFetcher {

    public static final int TIMEOUT_SECONDS = 3;
    private static final Logger logger = Logger.getLogger(WebsiteFetcher.class.getName());
    static final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
            .build();

    public String fetchContent(String urlString) throws IOException, InterruptedException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                logger.warning("Received non-OK response: " + response.statusCode() + " from " + urlString);
                throw new IOException("Wrong HTTP response: " + response.statusCode());
            }

            return response.body();
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            logger.severe("Error fetching content from: " + urlString + " - " + e.getMessage());
            throw e;
        }
    }
}
