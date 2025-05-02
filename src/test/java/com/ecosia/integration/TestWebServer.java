package com.ecosia.integration;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestWebServer extends NanoHTTPD {

    public TestWebServer(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri().equals("/") ? "/index.html" : session.getUri();
        try {
            String content = Files.readString(Paths.get("src/test/resources/testsite" + uri));
            return newFixedLengthResponse(Response.Status.OK, "text/html", content);
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not found");
        }
    }
}
