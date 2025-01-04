package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.Set;

import java.util.HashMap;
import java.util.Map.Entry;

public class ReverseIndex {
    private Map<String, UrlToTfIdfMap> tokenToUrlToTfIdf;

    /**
     * Create Reverse Index from Forward Index.
     * 
     * @param forwardIndex Forward Index based on which Reverse Index is to be created
     */
    public ReverseIndex(ForwardIndex forwardIndex) {
        tokenToUrlToTfIdf = new HashMap<>();
        int nPages = forwardIndex.nPages();

        // Build initial map with count instead of TfIdf
        for (Entry<String, ForwardIndex.ForwardIndexEntry> urlForwardIndexEntry : forwardIndex.entrySet()) {
            String url = urlForwardIndexEntry.getKey();
            CountMap<String> tkns = urlForwardIndexEntry.getValue().tokenToCount;
            for (Entry<String, Integer> tkn : tkns.entrySet()) {
                this.add(tkn.getKey(), url, tkn.getValue());
            }
        }

        // Convert counts to tfIdf
        for (Entry<String, UrlToTfIdfMap> entry : this.entrySet()) {
            UrlToTfIdfMap urlToTfIdfMap = entry.getValue();
            int nPagesWithToken = urlToTfIdfMap.nPages();
            for (Entry<String, Double> elem : urlToTfIdfMap.entrySet()) {
                String url = elem.getKey();
                int nTokensOnPage = forwardIndex.nTokens(url, false);
                int countTokenOnPage = elem.getValue().intValue();
                double tfIdf = computeTfIdf(countTokenOnPage, nTokensOnPage, nPages, nPagesWithToken);
                urlToTfIdfMap.setTfIdf(url, tfIdf);
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
        return this.getPagesContaining(token).getUrls();
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
        tokenToUrlToTfIdf.putIfAbsent(token, new UrlToTfIdfMap());
        tokenToUrlToTfIdf.get(token).setTfIdf(url, (double)count);
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
     * @return {@code UrlToTfIdfMap} from URLs of pages that contain token to corresponding TfIdf Score
     */
    public UrlToTfIdfMap getPagesContaining(String token) {
        return tokenToUrlToTfIdf.getOrDefault(token, new UrlToTfIdfMap());
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
        return tokenToUrlToTfIdf.getOrDefault(token, new UrlToTfIdfMap()).containsPage(url);
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
        return this.getPagesContaining(token).getTfIdfOf(url);
    }

    /**
     * Returns {@code Set} of tokens and their corresponding UrlToIdfMap.
     * Only used in ReverseIndex Constructor.
     * 
     * @return {@code Set} of tokens and their corresponding UrlToIdfMap
     */
    private Set<Entry<String, UrlToTfIdfMap>> entrySet() {
        return tokenToUrlToTfIdf.entrySet();
    }

    /**
     * Maps URLs to TfIdf.
     */
    static class UrlToTfIdfMap {
        private Map<String, Double> urlToTfIdf;

        /**
         * Instantiates empty TfIdfMap.
         */
        UrlToTfIdfMap() {
            this.urlToTfIdf = new HashMap<>();
        }

        /**
         * Tests if this UrlToTfIdfMap contains a page with a given URL.
         * This method is used only in ReverseIndexTets.
         * 
         * @param url the URL to search for
         * @return {@code true} if map contains page, {@code false} if it does not  
         */
        boolean containsPage(String url) {
            return urlToTfIdf.containsKey(url);
        }

        /**
         * Retrieves TfIdf of a page.
         * Returns 0 for pages that that this UrlToTfIdfMap does not contain.
         * ???
         * 
         * @param url url of page whose TfIdf is to be retrieved
         * @return TfIdf of given page
         */
        Double getTfIdfOf(String url) {
            return urlToTfIdf.getOrDefault(url, 0.);
        }

        /**
         * Sets TfIdf value of given URL.
         * 
         * @param url URL of page whose TfIdf value is to be set
         * @param tfIdf new TfIdf value
         */
        private void setTfIdf(String url, Double tfIdf) {
            urlToTfIdf.put(url, tfIdf);
        }

        /**
         * Retrieves number of web pages in this UrlToTfIdfMap.
         * This method is used to retrieve the number of pages that have at least one token when creating the reverse index, to then calculate TfIdf scores.
         * 
         * @return number of pages in this UrlToTfIdfMap
         */
        int nPages() {
            return urlToTfIdf.size();
        }

        /**
         * Retrieves all URLs in this UrlToTfIdfMap.
         * 
         * @return {@code Set} of all URLs in this UrlToTfIdfMap
         */
        Set<String> getUrls() {
            return urlToTfIdf.keySet();
        } 

        /**
         * Merges two UrlToTfIdfMaps.
         * TfIdfs scores of URLs that are present in both Maps will be summed.
         * The UrlToTfIdfMaps for multiple individual tokens can be merged to compute total TfIdf scores for more than one token.
         * 
         * @param other the {@code UrlToTfIdfMap} with which the original map is to be merged
         */
        void merge(UrlToTfIdfMap other) {
            other.urlToTfIdf.forEach((k, v) -> this.urlToTfIdf.merge(k, v, Double::sum));
        }
        
        private Set<Entry<String, Double>> entrySet() {
            return this.urlToTfIdf.entrySet();
        }
    
        public Map<String, Double> getMap() {
            return this.urlToTfIdf;
        }
    }
}
