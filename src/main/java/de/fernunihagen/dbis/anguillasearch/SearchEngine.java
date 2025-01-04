package de.fernunihagen.dbis.anguillasearch;
import com.google.gson.JsonElement;

import com.google.gson.JsonArray;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import java.util.Scanner;

public class SearchEngine {
    Crawler crawler;
    Index index;
    
    public SearchEngine() throws IOException {
        this.crawler = new Crawler(getAllSeedUrls());
        crawler.crawl();
        System.out.println("Pages indexed: " + crawler.nPages());
        System.out.println("...");
        this.index = new Index(crawler.getPages());
        System.out.println("Search Engine ready");
        System.out.println();
    }

    public void run() {
        System.out.println("You can start your search now...");
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter search query (or ’exit’ to quit):");
            String query = scanner.nextLine();
            if (query.equals("exit")) {
                return;
            }
            this.search(query);
        }
    }

    private void search(String query) {
        Index.SearchResult results = index.search(query);
        results.prettyPrint();
    }

    private static List<String> getAllSeedUrls() throws IOException {
        // Load the metadata from the JSON file
        List<String> fileNames = new ArrayList<>();
        fileNames.add("intranet/cheesy1-f126d0d3.json");
        fileNames.add("intranet/cheesy2-c79b0581.json");
        fileNames.add("intranet/cheesy3-7fdaa098.json");
        fileNames.add("intranet/cheesy4-a31d2f0d.json");
        //fileNames.add("intranet/cheesy5-d861877d.json");
        fileNames.add("intranet/cheesy6-54ae2b2e.json");

        List<String> seedUrls = new ArrayList<>();
        for (String fileName : fileNames) {
            for (String seedUrl : getSeedUrls(fileName)) {
                seedUrls.add(seedUrl);
            }
        }
        return seedUrls;
    }

    private static List<String> getSeedUrls(String net) throws IOException {
        JsonArray seedUrlsJson = Utils.parseJSONFile(net).get("Seed-URLs").getAsJsonArray();
        List<String> seedUrls = new ArrayList<>();
        for (JsonElement url : seedUrlsJson) {
            seedUrls.add(url.getAsString());
        }
        return seedUrls;
    }
}
