package de.fernunihagen.dbis.anguillasearch.index;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Map.Entry;

import de.fernunihagen.dbis.anguillasearch.CountMap;
import de.fernunihagen.dbis.anguillasearch.Page;

public class Index {
    ForwardIndex forwardIndex;
    ReverseIndex reverseIndex;

    public Index(Collection<Page> pages) {
        forwardIndex = new ForwardIndex(pages);
        reverseIndex = new ReverseIndex(forwardIndex);
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
            (Entry<String, Double> r1, Entry<String, Double> r2) -> Double.compare(r1.getValue(), r2.getValue())
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
        CountMap<String> queryTokens = StringProcessing.getTokenCounts(query);
        Map<String, Double> urlToTfIdf = new HashMap<>();
        for (String token : queryTokens.keySet()) {
            reverseIndex.getPagesContaining(token).forEach((url, tfIdf) -> urlToTfIdf.merge(url, tfIdf, Double::sum));
        }
        return urlToTfIdf;
    }

    public double cosineSimilarity(String url, String query) {
        CountMap<String> queryTokens = StringProcessing.getTokenCounts(query);
        NormalizedVectorMap queryVector = new NormalizedVectorMap(queryTokens.getMap());
        return queryVector.dotThisBased(this.forwardIndex.getVectorOf(url));
    }
}
