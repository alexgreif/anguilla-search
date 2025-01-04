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

/**
 * An index consists of a Forward Index, a Reverse Index and Page Ranks.
 */
public class Index {
    ForwardIndex fi;
    ReverseIndex ri;
    PageRank pr;
    static final double cosineSimilarityWeight = 1;
    static final double tfIdfWeight = 0;
    static final double pageRankWeight = 1;
    
    /**
     * Constructs index from seed URLs.
     * This constructor is used only in tests.
     * 
     * @param seedUrls {@code Array} of seed URLs to be used as a starting point for the index
     */
    public Index(String[] seedUrls) {
        Crawler crawler = new Crawler(seedUrls);
        crawler.crawl();
        this.fi = new ForwardIndex(crawler.getPages());
        this.ri = new ReverseIndex(fi);
        this.pr = new PageRank(crawler.getPages(), 0.85, Math.pow(10,-12), false);
    }

    /**
     * Constructs index from Collection of pages.
     * 
     * @param pages {@code Collection} of {@code Page}s for which to create index
     */
    public Index(Collection<Page> pages) {
        this.fi = new ForwardIndex(pages);
        this.ri = new ReverseIndex(fi);
        this.pr = new PageRank(pages, 0.85, Math.pow(10,-12), false);
    }

    /**
     * Search web pages in index for a query.
     * Combines Cosine Similarity, TfIdf and Page Rank for final score.
     * 
     * @param query the search query
     * @return Search results sorted by score
     */
    public SearchResult search(String query) {
        // Get URLs of pages containing at least one of the tokens and calculate TfIdf summed for all tokens
        CountMap<String> queryTokens = StringProcessing.process(query);
        ReverseIndex.UrlToTfIdfMap queryTokenResult = new ReverseIndex.UrlToTfIdfMap();
        for (String token : queryTokens.keySet()) {
            queryTokenResult.merge(ri.getPagesContaining(token));
        }

        // Calculate other metrics
        NormalizedVectorMap normalizedQueryVector = new NormalizedVectorMap(queryTokens.getMap());
        SearchResult results = new SearchResult();
        for (String url : queryTokenResult.getUrls()) {
            double tfIdf = queryTokenResult.getTfIdfOf(url);
            double cosineSimilarity = normalizedQueryVector.dotThisBased(fi.getVectorOf(url));
            double pageRank = this.pr.getRankOf(url);
            double score = computeScore(cosineSimilarity, tfIdf, pageRank);
            results.add(new SearchResult.SearchResultEntry(url, score, cosineSimilarity, tfIdf, pageRank));
        }

        return results;
    }

    /**
     * Compute overall score as weighted average of Cosine Similarity, TfIdf and Page Rank
     * 
     * @param cosineSimilarity
     * @param tfIdf
     * @param pageRank
     * @return overall score
     */
    private static double computeScore(double cosineSimilarity, double tfIdf, double pageRank) {
        double score = cosineSimilarity * cosineSimilarityWeight;
        score += tfIdf * tfIdfWeight;
        score += pageRank * pageRankWeight;
        score = score / (cosineSimilarityWeight + tfIdfWeight + pageRankWeight);
        return score;
    }

    /**
     * Search using only TfIdf.
     * Used only for testing.
     * 
     * @param query the search query
     * @return search results sorted descending by TfIdf
     */
    public SimpleSearchResult tfIdfSearch(String query) {
        CountMap<String> queryTokens = StringProcessing.process(query);
        ReverseIndex.UrlToTfIdfMap queryTokenResult = new ReverseIndex.UrlToTfIdfMap();
        for (String token : queryTokens.keySet()) {
            queryTokenResult.merge(ri.getPagesContaining(token));
        }
        return new SimpleSearchResult(queryTokenResult);
    }


    /**
     * Search using only Cosine Similarity.
     * Used only for testing.
     * 
     * @param query the search query
     * @return search results sorted descending by Cosine Similarity
     */
    public SimpleSearchResult vectorSearch(String query) {
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
        return new SimpleSearchResult(results);
    }

    /**
     * Results of search using only one metric.
     * Used only for testing.
     */
    public static class SimpleSearchResult {
        Map<String, Double> results;

        public SimpleSearchResult(ReverseIndex.UrlToTfIdfMap tfIdfMap) {
            this.results = new LinkedHashMap<>(tfIdfMap.getMap());
            this.sort();
        }

        public SimpleSearchResult(Map<String, Double> resultsMap) {
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

    /**
     * Results of a search by all three metric Cosine Similarity, TfIdf and Page Rank.
     */
    public static class SearchResult {
        SortedSet<SearchResultEntry> results;

        public SearchResult() {
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
            String urlFormat = "%1$-45s";
            System.out.println(String.format(urlFormat, "| URL") + " | " +
            "Score" + " | " +
            "Cosine Similarity" + " | " +
            "TfIdf" + " | " +
            "PageRank" + " | ");
            System.out.println("-----------------------------------------------" +
            "-----------------------------------------------");
            
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


