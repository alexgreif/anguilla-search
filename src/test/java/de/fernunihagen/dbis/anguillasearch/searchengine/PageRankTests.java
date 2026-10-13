package de.fernunihagen.dbis.anguillasearch.searchengine;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import java.util.logging.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.joda.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.fernunihagen.dbis.anguillasearch.Crawler;
import de.fernunihagen.dbis.anguillasearch.Utils;

/**
 * Unit tests for the page rank algorithm.
 */
class PageRankTests {
    private static final Logger logger = Logger.getLogger(PageRankTests.class.getName());
    static List<JsonObject> testJSONs = new ArrayList<>();
    static List<Map<String, Double>> pageRankForAllIntranets;
    static double dampingFactor;

    @BeforeAll
    static void setUp() throws IOException {
        // Load the metadata from the JSON file
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy1-f126d0d3.json"));
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy4-a31d2f0d.json"));
        //testJSONs.add(Utils.parseJSONFile("intranet/cheesy5-d861877d.json"));
        testJSONs.add(Utils.parseJSONFile("intranet/cheesy6-54ae2b2e.json"));

        // Create a map of crawler instances and page rank instances
        pageRankForAllIntranets = new ArrayList<>();
        for (JsonObject testJSON : testJSONs) {
            // Extract the seed URLs from the JSON file
            String[] seedUrls = new Gson().fromJson(testJSON.get("Seed-URLs"), String[].class);
            
 
            Crawler crawler = new Crawler(seedUrls);
            crawler.crawl();
            System.out.println(LocalDateTime.now() + " crawled");
            dampingFactor = 0.85;
            double epsilon = Math.pow(10, -10);
            int maxIters = (int) Math.pow(10, 6);
            Map<String, Double> urlToPageRank = PageRank.compute(
                crawler.getPages(), dampingFactor, epsilon, maxIters, false
                ); // Verbose
            System.out.println(LocalDateTime.now() + " PageRanked");

            // Add the crawler and page rank instances to the map
            pageRankForAllIntranets.add(urlToPageRank);

            // Verbose output Net 6 with simple PageRank for A4.3
            if (testJSON.get("Net-Name").getAsString().equals("54ae2b2e")) {
                PageRank.compute(crawler.getPages(), 1, epsilon, 30, true);
            }
        }
    }

    @Test
    void sumOfPageRank() {
        for (Map<String, Double> pageRankMap : pageRankForAllIntranets) {
            // Get the sum of the page ranks
            double pageRankSum = pageRankMap.values().stream().mapToDouble(Double::doubleValue).sum();
            // Log the sum of the page ranks
            logger.log(Level.INFO, "Sum of PageRank: {0}", pageRankSum);
            // Verify that the sum of the page ranks is close to 1
            assertTrue(Math.abs(pageRankSum - 1.0) < 0.001);
        }

    }
    
    @Test
    void seedPageRank() {
        for (JsonObject testJSON : testJSONs) {
            // Extract the seed URLs from the JSON file
            String[] seedUrls = new Gson().fromJson(testJSON.get("Seed-URLs"), String[].class);

            int numPages = new Gson().fromJson(testJSON.get("Num-Websites"), Integer.class);
            


            // Get the page rank of the seed URLs
            for (String seedUrl : seedUrls) {
                
                
                // Get PageRank for seed url
                double seedPageRank = -99;
                for (Map<String, Double> pageRankMap : pageRankForAllIntranets) {
                    Double rank;
                    if ((rank = pageRankMap.get(seedUrl)) != null) {
                        seedPageRank = rank;
                        break;
                    }
                }
                // Adjust the damping factor to match your implementation
                double rankSource = (1.0 - dampingFactor) * (1.0 / numPages);
                
                assertTrue(Math.abs(seedPageRank - rankSource) < 0.001);

            }
        }
    }

    @Test
    void correctPageRankScores() {
        // Create a map with URLs and the correct page rank scores
        // These scores will be used to verify the correctness of the page rank algorithm
        Map<String, Double> correctPageRankScores = Map.of(
            "http://cheddar24.cheesy6", 0.0375,
            "http://brie24.cheesy6", 0.3326,
            "http://crumbly-cheddar.cheesy6", 0.3097,
            "http://nutty-cheddar24.cheesy6", 0.3202);

        // Verify that the page rank scores are correct
        for (Map.Entry<String, Double> entry : correctPageRankScores.entrySet()) {
            String url = entry.getKey();
            double correctPageRank = entry.getValue();

            // Get PageRank for url
            double pageRankScore = -99;
            for (Map<String, Double> pageRankMap : pageRankForAllIntranets) {
                Double rank;
                if ((rank = pageRankMap.get(url)) != null) {
                    pageRankScore = rank;
                    break;
                }
            }
            // Test
            assertTrue(Math.abs(pageRankScore - correctPageRank) < 0.001);

            
        }
    }
}