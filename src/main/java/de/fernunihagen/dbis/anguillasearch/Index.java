package de.fernunihagen.dbis.anguillasearch;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;

public class Index {
    ForwardIndex fi;
    ReverseIndex ri;
    List<Page> pages;
    List<String> tokens;
    

    public Index(String[] seedUrls) {
        Crawler crawler = new Crawler(seedUrls);
        crawler.crawl();
        this.pages = new ArrayList<>(crawler.getPages());
        this.fi = new ForwardIndex(crawler.getPages());
        this.ri = new ReverseIndex(fi);

    }

    // Alternativ constructor for tests
    public Index(List<Page> pageList) {
        this.fi = new ForwardIndex(pageList);
        this.ri = new ReverseIndex(fi);
    }

    public SearchResult tfIdfSearch(String query) {
        CountMap<String> queryTokens = StringProcessing.process(query);
        ReverseIndex.TfIdfMap queryTokenResult = new ReverseIndex.TfIdfMap();
        for (String token : queryTokens.keySet()) {
            queryTokenResult.merge(ri.getPagesContaining(token));
        }
        return new SearchResult(queryTokenResult);
    }

    public SearchResult vectorSearch(String query) {
        CountMap<String> queryTokens = StringProcessing.process(query);
        // System.out.println("Found following tokens and weights:");
        // for (Entry<String, Integer> entry : queryTokens.entrySet()) {
        //     System.out.println(entry.getKey() + ": " + entry.getValue());
        // }
        // System.out.println("If you want to assign a new weight to a token, type 'token: weight'");
        // System.out.println("When you are done, submit an empty line.");
        // while (True) {
        //     System.in.
        // }
        NormalizedVectorMap normalizedQueryVector = new NormalizedVectorMap(queryTokens.getMap());
        Map<String, Double> results = new HashMap<>();
        for (Entry<String, ForwardIndex.ForwardIndexEntry> entry : this.fi.entrySet()) {
            double cosineSimilarity = normalizedQueryVector.dotThisBased(entry.getValue().normalizedCountVector);
            if (cosineSimilarity != 0) {
                results.put(entry.getKey(), cosineSimilarity);
            }
        }
        return new SearchResult(results);
    }

    public static class SearchResult {
        Map<String, Double> results;

        public SearchResult(ReverseIndex.TfIdfMap tfIdfMap) {
            this.results = new LinkedHashMap<>(tfIdfMap.getMap());
            this.sort();
        }

        public SearchResult(Map<String, Double> resultsMap) {
            this.results = new LinkedHashMap<>(resultsMap);
            this.sort();
        }

        public void sort() {
            List<Entry<String, Double>> list = new ArrayList<>(this.results.entrySet());
            list.sort(Entry.comparingByValue(Collections.reverseOrder()));
            Map<String, Double> resultsSorted = new LinkedHashMap<>();
            for (Entry<String, Double> entry : list) {
                resultsSorted.put(entry.getKey(), entry.getValue());
            }
            this.results = resultsSorted;
        }

        public List<String> getUrls() {
            return new ArrayList<>(this.results.keySet());
        }

        public String toString() {
            return results.toString();
        }

        public void prettyPrint() {
            for (Entry<String, Double> entry : this.results.entrySet()) {
                System.out.println(entry.getKey() + ": " + String.format("%.3f", entry.getValue()));
            }
        }
    }
}


