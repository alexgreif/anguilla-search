package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;

/**
 * Implements the PageRank Algorithm
 */
class PageRank {

    private PageRank() {}

    /**
     * Computes PageRanks for a set of web pages.
     * 
     * @param pages the pages whose PageRanks are to be computed
     * @param dampingFactor damping factor of the PageRank algorithm
     * @param epsilon if the summed absolute differences between two iterations are smaller than epsilon, the algorithm will terminate
     * @param maxIters number of iteration after which to terminate algorithm even if it has not converged yet
     * @param verbose if {@code true}, prints PageRanks for every iteration
     * @return {@code Map} from the pages' URLs to their PageRanks
     */
    static Map<String, Double> compute(Collection<Page> pages, double dampingFactor, double epsilon, int maxIters, Boolean verbose) {
        // Set up the two data structures needed for computation
        Map<String, Set<String>> urlToIncomingLinks = new HashMap<>();
        Map<String, Double> urlToinvNumOutLinks = new HashMap<>();
        for (Page page : pages) {
            urlToIncomingLinks.put(page.url, page.incomingLinksSet());
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
        if (!Boolean.TRUE.equals(verbose)) {
            urlToRank = iterate(
                urlToRank, rankSource, urlToIncomingLinks, urlToinvNumOutLinks,
                dampingFactor, epsilon, maxIters);
        } else {
            urlToRank = iterateVerbose(
                urlToRank, rankSource, urlToIncomingLinks, urlToinvNumOutLinks,
                dampingFactor, epsilon, maxIters);
        }
        return urlToRank;
    }

    /**
     * Performs iterations of PageRank Algorithm.
     * Stops when sum of differences is smaller than {@code epsilon} or
     * {@code maxIters} iterations have been performed.
     * 
     * @param initialUrlToRank
     * @param rankSource
     * @param urlToIncomingLinks
     * @param urlToinvNumOutLinks
     * @param dampingFactor
     * @param epsilon
     * @param maxIters
     * @return
     */
    private static Map<String, Double> iterate(
        Map<String, Double> initialUrlToRank, double rankSource,
        Map<String, Set<String>> urlToIncomingLinks, Map<String, Double> urlToinvNumOutLinks,
        double dampingFactor, double epsilon, int maxIters) {
        Map<String, Double> urlToRank = initialUrlToRank;
        double diffSum = 1;
        int i = 1;
        while (diffSum >= epsilon && i != maxIters+1) {
            // Iterate over web pages
            Map<String, Double> newRanks = new HashMap<>();
            diffSum = 0;
            for (Entry<String, Set<String>> entry : urlToIncomingLinks.entrySet()) {
                double simpleRank = 0.;
                for (String link : entry.getValue()) {
                    simpleRank += urlToRank.get(link) * urlToinvNumOutLinks.get(link);
                }
                double newRank = dampingFactor * simpleRank + rankSource;
                String url = entry.getKey();
                newRanks.put(url, newRank);
                double diff = newRank - urlToRank.get(entry.getKey());
                diffSum += Math.abs(diff);
            }
            urlToRank = newRanks; // Assign new PageRanks
            i++;
        }
        return urlToRank;
    }

    /**
     * Performs iterations of PageRank Algorithm.
     * Stops when sum of differences is smaller than {@code epsilon} or
     * {@code maxIters} iterations have been performed.
     * Prints updated PageRanks in every iteration.
     * Repeats a lot of the same code to avoid additional nesting with conditionals
     * for performance and readability reasons.
     * 
     * @param initialUrlToRank
     * @param rankSource
     * @param urlToIncomingLinks
     * @param urlToinvNumOutLinks
     * @param dampingFactor
     * @param epsilon
     * @param maxIters
     * @return
     */
    private static Map<String, Double> iterateVerbose(
        Map<String, Double> initialUrlToRank, double rankSource,
        Map<String, Set<String>> urlToIncomingLinks, Map<String, Double> urlToinvNumOutLinks,
        double dampingFactor, double epsilon, int maxIters
        ) {
        Map<String, Double> urlToRank = initialUrlToRank;
        double diffSum = 1;
        int i = 1;
        int maxUrlLength = urlToRank.keySet().stream().map(s->s.length()).max(Integer::compare).get();
        while (diffSum >= epsilon && i != maxIters+1) {
            System.out.println(String.format("Iteration %s", i));
            // Iterate over web pages
            Map<String, Double> newRanks = new HashMap<>();
            diffSum = 0;
            for (Entry<String, Set<String>> entry : urlToIncomingLinks.entrySet()) {
                double simpleRank = 0.;
                for (String link : entry.getValue()) {
                    simpleRank += urlToRank.get(link) * urlToinvNumOutLinks.get(link);
                }
                double newRank = dampingFactor * simpleRank + rankSource;
                String url = entry.getKey();
                newRanks.put(url, newRank);
                double diff = newRank - urlToRank.get(entry.getKey());
                diffSum += Math.abs(diff);
                String urlFormat = "%1$-" + (maxUrlLength+1) + "s";
                System.out.println(String.format(urlFormat, url) + " | " + String.format("%.5f", newRank));
            }
            urlToRank = newRanks; // Assign new PageRanks
            i++;
            System.out.println();
        }
        return urlToRank;
    }
}
