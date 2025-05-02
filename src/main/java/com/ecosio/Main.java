package com.ecosio;

import com.ecosio.dto.Link;

import java.io.IOException;
import java.util.List;

import static com.ecosio.utility.WebUtility.getDomain;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException { //TODO handle errors
        String baseUrl = "https://orf.at/";  // Replace with actual URL
        if (args.length != 0) {
            baseUrl = args[0];
        }

        WebsiteFetcher websiteFetcher = new WebsiteFetcher();
        String html = websiteFetcher.fetchContentNew(baseUrl);
        LinkExtractor linkExtractor = new LinkExtractor();
        List<Link> linkList = linkExtractor.extractLinks(html, baseUrl, getDomain(baseUrl));
        for (Link link : linkList) {
            System.out.println(link);
        }
    }
}