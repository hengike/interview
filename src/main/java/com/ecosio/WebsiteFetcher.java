package com.ecosio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WebsiteFetcher {

    public static final int TIMEOUT_SECONDS = 3;
    private static final Logger logger = Logger.getLogger(WebsiteFetcher.class.getName());

    @Deprecated //TODO cleanup
    public String fetchContent(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public String fetchContentNew(String urlString) throws IOException, InterruptedException {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                    .build();

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
