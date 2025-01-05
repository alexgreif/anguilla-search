package de.fernunihagen.dbis.anguillasearch.index;

import java.util.Map;
import java.util.Set;

import java.util.HashMap;
import java.util.Map.Entry;

public class ReverseIndex {
    private Map<String, Map<String, Double>> tokenToUrlToTfIdf;

    /**
     * Create Reverse Index from Forward Index.
     * 
     * @param forwardIndex Forward Index based on which Reverse Index is to be created
     */
    public ReverseIndex(ForwardIndex forwardIndex) {
        tokenToUrlToTfIdf = new HashMap<>();
        int nPages = forwardIndex.nPages();

        // Build initial map with count instead of TfIdf
        for (Entry<String, Map<String, Integer>> urlForwardIndexEntry : forwardIndex.urlToTokenToCountEntrySet()) {
            String url = urlForwardIndexEntry.getKey();
            Map<String, Integer> tokenToCount = urlForwardIndexEntry.getValue();
            for (Entry<String, Integer> tokenCountEntry : tokenToCount.entrySet()) {
                this.add(tokenCountEntry.getKey(), url, tokenCountEntry.getValue());
            }
        }

        // Convert counts to tfIdf
        for (Entry<String, Map<String, Double>> entry : this.entrySet()) {
            Map<String, Double> urlToTfIdfMap = entry.getValue();
            int nPagesWithToken = urlToTfIdfMap.size();
            for (Entry<String, Double> elem : urlToTfIdfMap.entrySet()) {
                String url = elem.getKey();
                int nTokensOnPage = forwardIndex.nTokens(url, false);
                int countTokenOnPage = elem.getValue().intValue();
                double tfIdf = computeTfIdf(countTokenOnPage, nTokensOnPage, nPages, nPagesWithToken);
                urlToTfIdfMap.put(url, tfIdf);
            }
        }
    }

    /**
     * Checks if this Reverse Index contains a specific token.
     * 
     * @param token token to search for
     * @return {@code true} if Reverse Index contains token, {@code false} if it does not
     */
    public boolean containsToken(String token) {
        return tokenToUrlToTfIdf.containsKey(token);
    }

    /**
     * Retrieves URLs of web pages that contain a specific token.
     * 
     * @param token token whose containing web page's URLs are to be retrieved
     * @return {@code Set} of URLs of web pages that contain the given token
     */
    public Set<String> getUrls(String token) {
        return this.getPagesContaining(token).keySet();
    }

    /**
     * Adds URL and corresponding count to token.
     * Creates entry for token if none exists so far.
     * This method is only used by ReverseIndex Constructor as it sets the TfIdf field to a count instead, which is only a preliminary step.
     * 
     * @param token token to which entry is to be added
     * @param url url that is to be added to token's entries
     * @param count count of token on web page corresponding to given URL
     */
    private void add(String token, String url, int count) {
        tokenToUrlToTfIdf.putIfAbsent(token, new HashMap<>());
        tokenToUrlToTfIdf.get(token).put(url, (double)count);
    }

    /**
     * Computes TfIdf.
     * This method is only used by ReverseIndex Constructor.
     * 
     * @param countTokenOnPage number of occurences of token on page
     * @param nTokensOnPage number of total tokens on page
     * @param nPages total number of pages
     * @param nPagesWithToken number of pages containing token
     * @return
     */
    private static double computeTfIdf(int countTokenOnPage, int nTokensOnPage, int nPages, int nPagesWithToken) {
        double tf = (double)countTokenOnPage / nTokensOnPage;
        double idf = Math.log((double)nPages / nPagesWithToken);
        return tf * idf;
    }

    /**
     * Retrieves URL and TfIdf Score of web pages containing a given token.
     * 
     * @param token token whose containing pages are to be retrieved
     * @return {@code Map} from URLs of pages that contain token to corresponding TfIdf Score
     */
    public Map<String, Double> getPagesContaining(String token) {
        return tokenToUrlToTfIdf.getOrDefault(token, new HashMap<>());
    }

    /**
     * Checks whether a given page contains a given token.
     * This method is used only for testing purposes.
     * 
     * @param token token whose occurence on page is to be checked
     * @param url URL of web page on which to look for token
     * @return {@code true} if the page contains the token, {@code false} if it does not
     */
    public Boolean tokenOnPage(String token, String url) {
        return tokenToUrlToTfIdf.getOrDefault(token, new HashMap<>()).containsKey(url);
    }
    
    /**
     * Retrieves TfIdf Score from a given token and page.
     * Only used for testing.
     * 
     * @param token
     * @param url URL of the page
     * @return TfIdf Score from given token and page
     */
    public double getTfIdf(String token, String url) {
        return this.getPagesContaining(token).get(url);
    }

    /**
     * Returns {@code Set} of tokens and their corresponding UrlToIdfMap.
     * Only used in ReverseIndex Constructor.
     * 
     * @return {@code Set} of tokens and their corresponding UrlToIdfMap
     */
    private Set<Entry<String, Map<String, Double>>> entrySet() {
        return tokenToUrlToTfIdf.entrySet();
    }
}
