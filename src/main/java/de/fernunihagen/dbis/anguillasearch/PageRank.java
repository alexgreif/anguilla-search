package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

public class PageRank {
    private Map<String, Double> urlToRank;
    private double dampingFactor;
    private double rankSource;
    private double epsilon;
    private double maxIters;
    private boolean verbose;

    public PageRank(Collection<Page> pages, double dampingFactor, double epsilon, boolean verbose) {
        this(pages, dampingFactor, epsilon, -1, verbose);
    }

    public PageRank(Collection<Page> pages, double dampingFactor, double epsilon, int maxIters, boolean verbose) {
        
        // Create three needed data structures from page list
        Set<String> urls = new HashSet<>();
        Map<String, Set<String>> incomingLinks = new HashMap<>();
        Map<String, Double> invNumOutLinks = new HashMap<>();
        for (Page page : pages) {
            incomingLinks.put(page.url, page.getIncomingLinks());
            invNumOutLinks.put(page.url, 1. / page.nOutgoingLinks(true));
            urls.add(page.url);
        }

        // Initialize instance variables
        Double initialRank = 1. / urls.size();
        this.urlToRank = new HashMap<>();
        for (String url : urls) {
            this.urlToRank.put(url, initialRank);
        }
        this.dampingFactor = dampingFactor;
        this.rankSource = (1 - dampingFactor) * initialRank;
        this.epsilon = epsilon;
        this.maxIters = maxIters;
        this.verbose = verbose;
        
        // Compute PageRank
        this.compute(incomingLinks, invNumOutLinks);
    }

    private void computeLimitedIters(Map<String, Set<String>> incomingLinks, Map<String, Double> invNumOutLinks) {
        double diffSum = 1;
        int i = 0;
        if (this.verbose) {
            while (diffSum >= this.epsilon && i < this.maxIters) {
                System.out.println("Iteration " + i);
                System.out.println("Summed Differences = " + diffSum);
                this.prettyPrint();
                diffSum = this.nextIteration(incomingLinks, invNumOutLinks);
                i++;
            }
            System.out.println("Iteration " + i);
            System.out.println("Summed Differences = " + diffSum);
            this.prettyPrint();
        } else {
            while (diffSum >= this.epsilon && i < this.maxIters) {
                diffSum = this.nextIteration(incomingLinks, invNumOutLinks);
                i++;
            }
        }
    }

    private void compute(Map<String, Set<String>> incomingLinks, Map<String, Double> invNumOutLinks) {
        double diffSum = 1;
        int i = 0;
        if (this.verbose) {
            while (diffSum >= this.epsilon) {
                System.out.println("Iteration " + i);
                System.out.println("Summed Differences = " + diffSum);
                this.prettyPrint();
                diffSum = this.nextIteration(incomingLinks, invNumOutLinks);
                i++;
            }
            System.out.println("Iteration " + i);
            System.out.println("Summed Differences = " + diffSum);
            this.prettyPrint();
        } else {
            while (diffSum >= this.epsilon) {
                diffSum = this.nextIteration(incomingLinks, invNumOutLinks);
                i++;
            }
        }
    }

    private double nextIteration(Map<String, Set<String>> incomingLinks, Map<String, Double> invNumOutLinks) {
        Map<String, Double> newRanks = new HashMap<>();
        double diffSum = 0;
        for (Entry<String, Set<String>> entry : incomingLinks.entrySet()) {
            double simpleRank = 0.;
            for (String link : entry.getValue()) {
                simpleRank += this.urlToRank.get(link) * invNumOutLinks.get(link);
            }
            double newRank = this.dampingFactor * simpleRank + this.rankSource;
            newRanks.put(entry.getKey(), newRank);
            double diff = newRank - this.urlToRank.get(entry.getKey());
            diffSum += Math.abs(diff);
        }
        this.urlToRank = newRanks;
        return diffSum;
    }

    public Map<String, Double> getMap() {
        return this.urlToRank;
    }

    public double getRankOf(String url) {
        return this.urlToRank.get(url);
    }

    public void prettyPrint() {
        for (Entry<String, Double> entry : this.urlToRank.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();
    }

    public String toString() {
        return this.urlToRank.toString();
    }
}
