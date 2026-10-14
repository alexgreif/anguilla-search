package de.fernunihagen.dbis.anguillasearch.searchengine;

import java.util.List;
import java.util.Map.Entry;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.fernunihagen.dbis.anguillasearch.Crawler;
import de.fernunihagen.dbis.anguillasearch.Utils;
import de.fernunihagen.dbis.anguillasearch.searchengine.index.Index;
/**
 * Unit tests for the search.
 */
class SearchTests {

    @Test
    void findCorrectURLs() throws IOException {
        JsonObject testJSON = Utils.parseJSONFile("intranet/cheesy1-f126d0d3.json");
        // Extract the seed URLs from the JSON file
        String[] seedUrls = new Gson().fromJson(testJSON.get("Seed-URLs"), String[].class);
        // Extract the query from the JSON file
        String[] query = new Gson().fromJson(testJSON.get("Query-Token"), String[].class);
        // Extract the expected URLs from the JSON file
        String[] expectedURLs = new Gson().fromJson(testJSON.get("Query-URLs"), String[].class);
        // Execute a search with the given query in the given network via the seed URLs
        Crawler crawler = new Crawler(seedUrls);
        crawler.crawl();
        Index index = new Index(crawler.getPages());

        List<Entry<String, Double>> results = index.searchQuery(String.join(", ", query));
        List<String> foundUrls = new LinkedList<>();
        for (Entry<String, Double> entry : results) {
            foundUrls.add(entry.getKey());
        }

        // Print results
        System.out.println("Expected\n" + Arrays.asList(expectedURLs) + "\n");
        System.out.println("Found\n" + foundUrls);

        // Verify that the found URLs are correct, i.e. the same as stated in the JSON file
        assertTrue(foundUrls.containsAll(Arrays.asList(expectedURLs)));


    }
}