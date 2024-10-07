package de.fernunihagen.dbis.anguillasearch;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

public class Index {
    ForwardIndex fi;
    ReverseIndex ri;
    PageRank pr;
    final static double cosineSimilarityWeight = 1;
    final static double tfIdfWeight = 0;
    final static double pageRankWeight = 1;
    

    public Index(String[] seedUrls) {
        Crawler crawler = new Crawler(seedUrls);
        crawler.crawl();
        this.fi = new ForwardIndex(crawler.getPages());
        this.ri = new ReverseIndex(fi);
        this.pr = new PageRank(crawler.getPages(), 0.85, Math.pow(10,-12), false);
    }

    // Alternativ constructor for tests
    public Index(Collection<Page> pages) {
        this.fi = new ForwardIndex(pages);
        this.ri = new ReverseIndex(fi);
        this.pr = new PageRank(pages, 0.85, Math.pow(10,-12), false);
    }

    public SearchResult2 search(String query) {
        // Calculate tfIdf and get urls of pages containing at least one of the tokens
        CountMap<String> queryTokens = StringProcessing.process(query);
        ReverseIndex.TfIdfMap queryTokenResult = new ReverseIndex.TfIdfMap();
        for (String token : queryTokens.keySet()) {
            queryTokenResult.merge(ri.getPagesContaining(token));
        }

        // Calculate other metrics
        NormalizedVectorMap normalizedQueryVector = new NormalizedVectorMap(queryTokens.getMap());
        SearchResult2 results = new SearchResult2();
        for (String url : queryTokenResult.getUrls()) {
            double tfIdf = queryTokenResult.getTfIdfOf(url);
            double cosineSimilarity = normalizedQueryVector.dotThisBased(fi.getVectorOf(url));
            double pageRank = this.pr.getRankOf(url);
            double score = score(cosineSimilarity, tfIdf, pageRank);
            results.add(new SearchResult2.SearchResultEntry(url, score, cosineSimilarity, tfIdf, pageRank));
        }

        return results;
    }

    private static double score(double cosineSimilarity, double tfIdf, double pageRank) {
        double score = cosineSimilarity * cosineSimilarityWeight;
        score += tfIdf * tfIdfWeight;
        score += pageRank * pageRankWeight;
        score = score / (cosineSimilarityWeight + tfIdfWeight + pageRankWeight);
        return score;
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

    public static class SearchResult2 {
        SortedSet<SearchResultEntry> results;

        public SearchResult2() {
            this.results = new TreeSet<>();
        }

        public void add(SearchResultEntry entry) {
            this.results.add(entry);
        }

        public List<String> getUrls() {
            List<String> urls = new ArrayList<>();
            for (SearchResultEntry entry : this.results) {
                urls.add(entry.url);
            }
            return urls;
        }

        public String toString() {
            return this.results.toString();
        }

        public void prettyPrint() {
            String urlFormat = "%1$-37s";
            System.out.println(String.format(urlFormat, "| URL") + " | " +
            "Score" + " | " +
            "Cosine Similarity" + " | " +
            "TfIdf" + " | " +
            "PageRank" + " | ");
            System.out.println("-------------------------------------------" +
            "-------------------------------------------");
            
            for (SearchResultEntry entry : this.results) {
                System.out.println(String.format(urlFormat, "| " + entry.url) + " | " +
                StringUtils.center(String.format("%.3f", entry.score), 5) + " | " +
                StringUtils.center(String.format("%.3f", entry.cosineSimilarity), 17) + " | " +
                StringUtils.center(String.format("%.3f", entry.tfIdf), 5) + " | " +
                StringUtils.center(String.format("%.3f", entry.pageRank), 8) + " | ");
            }
        }

        static class SearchResultEntry implements Comparable<SearchResultEntry> {
            String url;
            Double score;
            Double cosineSimilarity;
            Double tfIdf;
            Double pageRank;

            SearchResultEntry(String url, Double score, Double cosineSimilarity, Double tfIdf, Double pageRank) {
                this.url = url;
                this.score = score;
                this.cosineSimilarity = cosineSimilarity;
                this.tfIdf = tfIdf;
                this.pageRank = pageRank;
            }

            @Override
            public int compareTo(final SearchResultEntry other) {
                return Double.compare(other.score, this.score);
            }
        }
    }
}


