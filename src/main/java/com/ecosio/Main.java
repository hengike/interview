package com.ecosio;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException { //TODO handle errors
        String baseUrl = "https://orf.at/";  // Replace with actual URL
        if (args.length != 0) {
            baseUrl = args[0];
        }

        WebsiteFetcher websiteFetcher = new WebsiteFetcher();
        System.out.println(websiteFetcher.fetchContentNew(baseUrl));
    }
}