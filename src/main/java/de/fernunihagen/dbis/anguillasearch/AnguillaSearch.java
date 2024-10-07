package de.fernunihagen.dbis.anguillasearch;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        String[] seedUrls = {"http://gruyere24.cheesy4", "http://tangy-gruyere.cheesy4", "http://edam24.cheesy4"};
        Crawler crawler = new Crawler(seedUrls);
        crawler.crawl(16);
        NetworkGraph graph = new NetworkGraph(crawler);
        try {
            graph.draw("figures/net-graph.png");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
