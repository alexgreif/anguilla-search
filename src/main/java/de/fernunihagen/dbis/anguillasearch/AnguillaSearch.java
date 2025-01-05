package de.fernunihagen.dbis.anguillasearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * Main class of the AnguillaSearch project.
 */
public final class AnguillaSearch {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnguillaSearch.class);
    
    private AnguillaSearch() {
    }

    /**
     * Main method.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {

        // Print start message to logger
        LOGGER.info("Starting AnguillaSearch...");

        /*
         * Set the java.awt.headless property to true to prevent awt from opening windows.
         * If the property is not set to true, the program will throw an exception when trying to 
         * generate the graph visualizations in a headless environment.
         */
        System.setProperty("java.awt.headless", "true");
        LOGGER.info("Java awt GraphicsEnvironment headless: {}", java.awt.GraphicsEnvironment.isHeadless());

        // Make Graph
        String[] seedUrls = {"http://gruyere24.cheesy4", "http://tangy-gruyere.cheesy4", "http://edam24.cheesy4"};
        Crawler crawler = new Crawler(seedUrls);
        crawler.crawl(16);
        NetworkGraph graph = new NetworkGraph(crawler);
        try {
            graph.draw("figures/net-graph.png");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        // Start Search Engine
        try {
            crawler = new Crawler(getAllSeedUrls());
        } catch (IOException e) {
            System.out.println("Failed to read seed URLs");
            LOGGER.error(e.getMessage());
            return;
        }
        crawler.crawl();
        System.out.println();
        System.out.println("Preparing search engine");
        System.out.println("...");
        SearchEngine eng = new SearchEngine(crawler.getPages());
        System.out.println("Search engine ready");
        System.out.println("Pages indexed: " + crawler.nPages());
        System.out.println("You can now start searching");
        System.out.println();
        
        // Run Search Engine
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter search query (or ’exit’ to quit):");
            String query = scanner.nextLine();
            if (query.equals("exit")) {
                scanner.close();
                break;
            }
            System.out.println();
            eng.search(query, true);
        }
    }

    /**
     * Makes list of all seed URLs
     * 
     * @return {@code List} of all seed URLs
     * @throws IOException
     */
    private static List<String> getAllSeedUrls() throws IOException {
        // Load the metadata from the JSON file
        List<String> filePaths = new ArrayList<>();
        filePaths.add("intranet/cheesy1-f126d0d3.json");
        filePaths.add("intranet/cheesy2-c79b0581.json");
        filePaths.add("intranet/cheesy3-7fdaa098.json");
        filePaths.add("intranet/cheesy4-a31d2f0d.json");
        // filePaths.add("intranet/cheesy5-d861877d.json");
        filePaths.add("intranet/cheesy6-54ae2b2e.json");

        List<String> seedUrls = new ArrayList<>();
        for (String filePath : filePaths) {
            for (String seedUrl : getSeedUrls(filePath)) {
                seedUrls.add(seedUrl);
            }
        }
        return seedUrls;
    }

    /**
     * Retrieves seed URLs from a given json file
     * 
     * @param filePath path of the file from which to retrieve seed URLs
     * @return {@code List} of seed URLs from the given file
     * @throws IOException
     */
    private static List<String> getSeedUrls(String filePath) throws IOException {
        JsonArray seedUrlsJson = Utils.parseJSONFile(filePath).get("Seed-URLs").getAsJsonArray();
        List<String> seedUrls = new ArrayList<>();
        for (JsonElement url : seedUrlsJson) {
            seedUrls.add(url.getAsString());
        }
        return seedUrls;
    }
}
