package com.ecosio;

import java.io.IOException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException { //TODO handle errors
        String baseUrl = "https://orf.at/";  // Replace with actual URL
        if (args.length != 0) {
            baseUrl = args[0];
        }

        WebsiteFetcher websiteFetcher = new WebsiteFetcher();
        System.out.println(websiteFetcher.fetchContent(baseUrl));
    }
}