package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;

public class PageRank {
    private Map<String, Double> map;

    public PageRank(Crawler crawler, double epsilon, int maxIters, boolean verbose) {
        Collection<Page> pages = crawler.getPages();
        // Get incoming and outgoing Links
        Map<String, Set<String>> incomingLinks = new HashMap<>();
        for (Page page : pages) {
            incomingLinks.put(page.url, page.getIncomingLinks());
        }
        Map<String, Double> invNumOutLinks = new HashMap<>();
        for (Page page : pages) {
            invNumOutLinks.put(page.url, 1. / page.nOutgoingLinks(true));
        }
        // Initialize
        Set<String> urls = incomingLinks.keySet();
        Double initialRank = 1. / urls.size();
        this.map = new HashMap<>();
        for (String url : urls) {
            this.map.put(url, initialRank);
        }

        // Iterate
        this.compute(incomingLinks, invNumOutLinks, epsilon, maxIters, verbose);
    }

    private void compute(Map<String, Set<String>> incomingLinks, Map<String, Double> invNumOutLinks, double epsilon, double maxIters, boolean verbose) {
        double diffSum = 1;
        int i = 0;
        if (verbose) {
            while (diffSum >= epsilon && i < maxIters) {
                System.out.println("Iteration " + i);
                System.out.println("Summed Differences = " + diffSum);
                this.prettyPrint();
                diffSum = this.iterate(incomingLinks, invNumOutLinks);
                i++;
            }
            System.out.println("Iteration " + i);
            System.out.println("Summed Differences = " + diffSum);
            this.prettyPrint();
        } else {
            while (diffSum >= epsilon && i < maxIters) {
                diffSum = this.iterate(incomingLinks, invNumOutLinks);
                i++;
            }
        }
    }

    private double iterate(Map<String, Set<String>> incomingLinks, Map<String, Double> invNumOutLinks) {
        Map<String, Double> newRanks = new HashMap<>();
        double diffSum = 0;
        for (Entry<String, Set<String>> entry : incomingLinks.entrySet()) {
            double newRank = 0.;
            for (String link : entry.getValue()) {
                newRank += this.map.get(link) * invNumOutLinks.get(link);
            }
            newRanks.put(entry.getKey(), newRank);
            double diff = newRank - this.map.get(entry.getKey());
            diffSum += Math.abs(diff);
        }
        this.map = newRanks;
        return diffSum;
    }

    public Map<String, Double> getMap() {
        return this.map;
    }

    public double getRankOf(String url) {
        return this.map.get(url);
    }

    public void prettyPrint() {
        for (Entry<String, Double> entry : this.map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();
    }

    public String toString() {
        return this.map.toString();
    }
}
