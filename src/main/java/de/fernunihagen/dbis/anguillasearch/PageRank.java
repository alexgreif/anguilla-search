package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;

/**
 * Implements the PageRank Algorithm
 */
public class PageRank {

    private PageRank() {}

    /**
     * Computes PageRanks for a set of web pages.
     * 
     * @param pages the pages whose PageRanks are to be computed
     * @param dampingFactor damping factor of the PageRank algorithm
     * @param epsilon if the summed absolute differences between two iterations are smaller than epsilon, the algorithm will terminate
     * @return {@code Map} from the pages URLs to their PageRanks
     */
    public static Map<String, Double> compute(Collection<Page> pages, double dampingFactor, double epsilon) {
        // Set up the two data structures needed for computation
        Map<String, Set<String>> urlToIncomingLinks = new HashMap<>();
        Map<String, Double> urlToinvNumOutLinks = new HashMap<>();
        for (Page page : pages) {
            urlToIncomingLinks.put(page.url, page.getIncomingLinks());
            urlToinvNumOutLinks.put(page.url, 1. / page.nOutgoingLinks(true));
        }

        // Initialize ranks
        Double initialRank = 1. / urlToIncomingLinks.size();
        Map<String, Double> urlToRank = new HashMap<>();
        for (String url : urlToIncomingLinks.keySet()) {
            urlToRank.put(url, initialRank);
        }

        // Iterate until diffsum is smaller than epsilon
        double rankSource = (1 - dampingFactor) * initialRank;
        double diffSum = 1;
        while (diffSum >= epsilon) {
            Map<String, Double> newRanks = new HashMap<>();
            diffSum = 0;
            for (Entry<String, Set<String>> entry : urlToIncomingLinks.entrySet()) {
                double simpleRank = 0.;
                for (String link : entry.getValue()) {
                    simpleRank += urlToRank.get(link) * urlToinvNumOutLinks.get(link);
                }
                double newRank = dampingFactor * simpleRank + rankSource;
                newRanks.put(entry.getKey(), newRank);
                double diff = newRank - urlToRank.get(entry.getKey());
                diffSum += Math.abs(diff);
            }
            urlToRank = newRanks;
        }

        return urlToRank;
    }
}
