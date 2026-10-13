package de.fernunihagen.dbis.anguillasearch.searchengine.index;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Map.Entry;

import de.fernunihagen.dbis.anguillasearch.Page;

/**
 * Index a Collection of web pages.
 * Provides methods to search these pages for a query string and
 * assess the match between a page and the search query.
 */
public class Index {
    private ForwardIndex forwardIndex;
    private ReverseIndex reverseIndex;

    /**
     * Creates an index for a collection of web pages.
     * 
     * @param pages the {@code Collection} of web pages to be indexed
     */
    public Index(Collection<Page> pages) {
        forwardIndex = new ForwardIndex(pages);
        reverseIndex = new ReverseIndex(forwardIndex);
        forwardIndex.initializeTfIdfVectors(reverseIndex);
    }

    /**
     * Finds all pages that contain at least one token of a query string.
     * Results are sorted in descending order by total TfIdf score for the query.
     * 
     * @param query the query string
     * @return {@code List} of URL and TfIdf, sorted by TfIdf in descending order
     */
    public List<Entry<String, Double>> searchQuery(String query) {
        List<Entry<String, Double>> resultsList = new LinkedList<>(tfIdfQuery(query).entrySet());
        Collections.sort(
            resultsList,
            (Entry<String, Double> r1, Entry<String, Double> r2) -> Double.compare(r2.getValue(), r1.getValue())
        );
        return resultsList;
    }

    /**
     * Computes TfIdf scores of all web pages in index for a given query string.
     * To do so, TfIdf scores for individual query tokens are summed.
     * 
     * @param query the query string
     * @return {@code Map} of URL to TfIdf score
     */
    public Map<String, Double> tfIdfQuery(String query) {
        Map<String, Integer> queryTokens = StringProcessing.getTokenCounts(query);
        Map<String, Double> urlToTfIdf = new HashMap<>();
        for (String token : queryTokens.keySet()) {
            reverseIndex.getTfIdfsFor(token).forEach((url, tfIdf) -> urlToTfIdf.merge(url, tfIdf, Double::sum));
        }
        return urlToTfIdf;
    }

    /**
     * Computes TF-IDF cosine similarity between a web page and a search query.
     * Query weights use the same corpus IDF values as document weights.
     * 
     * @param url the URL of the web page to be compared
     * @param query the search query
     * @return Cosine Similarity between page and query
     */
    public double cosineSimilarity(String url, String query) {
        return queryVector(query).dotThisBased(this.forwardIndex.getVectorOf(url));
    }

    /**
     * Computes candidate similarities using one shared, normalized query vector.
     *
     * @param urls candidate document URLs
     * @param query the search query
     * @return document URLs and their TF-IDF cosine similarities
     */
    public Map<String, Double> cosineSimilarities(Collection<String> urls, String query) {
        NormalizedVector vector = queryVector(query);
        Map<String, Double> similarities = new HashMap<>();
        for (String url : urls) {
            similarities.put(url, vector.dotThisBased(forwardIndex.getVectorOf(url)));
        }
        return similarities;
    }

    private NormalizedVector queryVector(String query) {
        Map<String, Integer> queryTokens = StringProcessing.getTokenCounts(query);
        Map<String, Double> queryWeights = new HashMap<>();
        for (Entry<String, Integer> token : queryTokens.entrySet()) {
            queryWeights.put(token.getKey(), token.getValue() * reverseIndex.getIdfFor(token.getKey()));
        }
        // Dividing query counts by the query length would not change its normalized direction.
        return new NormalizedVector(queryWeights);
    }
}
