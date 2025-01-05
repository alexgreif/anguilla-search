package de.fernunihagen.dbis.anguillasearch.index;

import java.util.Map;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import de.fernunihagen.dbis.anguillasearch.Page;

import java.util.Collection;

/**
 * A Forward Index organizes tokens by page.
 * Includes methods for creating and reading Forward Indices.
 * Once created, a Forward Index cannot be modified.
 */
class ForwardIndex {
    private final Map<String, ForwardIndexEntry> urlToFiEntry;

    /**
     * Creates a Forward Index from a Collection of Pages
     * 
     * @param pages {@code Collection} of pages for which to build Forward Index
     */
    ForwardIndex(Collection<Page> pages) {
        urlToFiEntry = new HashMap<>(); 
        for (Page page : pages) {
            this.add(page);
        }
    }

    /**
     * Retrieves number of pages in this Forward Index.
     * This method is used by Reverse Index Constructor to compute TfIdf.
     * 
     * @return number of pages in this Forward Index
     */
    int nPages() {
        return urlToFiEntry.size();
    }

    /**
     * Returns number of tokens of a given web page.
     * This can be the total number of tokens or the number of distinct tokens.
     * This method is used by Reverse Index Constructor to compute TfIdf.
     * 
     * @param url url of the page whose number of tokens is to be returned
     * @param distinct if true, number of distinct tokens will be returned, if false, total number of tokens will be returned
     * @return distinct or total number of tokens of given web page
     */
    int nTokens(String url, boolean distinct) {
        return distinct ? this.getTokensOf(url).size() : this.getTokensOf(url).values().stream().reduce(0, Integer::sum);
    }

    /**
     * Retrieves tokens of a specific page and their normalized numbers of occurence.
     * Numbers of occurence are normalized, so that their euclidean norm equals 1.
     * This method is used by Index class to compute Cosine Similarity.
     * 
     * @param url url of the page whose tokens are to be retrieved
     * @return {@code NormalizedVectorMap} of tokens and their normalized numbers of occurence
     */
    NormalizedVectorMap getVectorOf(String url) {
        return urlToFiEntry.get(url).normalizedCountVector;
    }

    /**
     * Returns set of entries of this Forward Index.
     * Key is a page's URL and value is a {@code Map} from the tokens on that page to their number of occurences.
     * This method is used by Reverse Index Constructor.
     * 
     * @return {@code Set} of URL to "token-to-count map" Entries
     */
    Set<Entry<String, Map<String, Integer>>> urlToTokenToCountEntrySet() {
        Set<Entry<String, Map<String, Integer>>> entrySet = new HashSet<>();
        for (Entry<String, ForwardIndexEntry> entry : urlToFiEntry.entrySet()) {
            entrySet.add(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().tokenToCount));
        }
        return entrySet;
    }

    /**
     * Retrieves tokens of a specific page and their numbers of occurence.
     * This Method is used to compute number of tokens on a page by nTokens method.
     * 
     * @param url url of the page whose tokens are to be retrieved
     * @return {@code Map} of tokens to their number of occurence on given page
     */
    private Map<String, Integer> getTokensOf(String url) {
        return urlToFiEntry.get(url).tokenToCount;
    }

    /**
     * Adds a page to this Forward Index.
     * This method is only used by the Forward Index Constructor.
     * 
     * @param page the page to add
     */
    private void add(Page page) {
        urlToFiEntry.put(page.url, new ForwardIndexEntry(page));
    }

    /**
     * One entry of a Forward Index for a single web page.
     * Includes the page's raw and normalized token counts.
     */
    private class ForwardIndexEntry {
        private final Map<String, Integer> tokenToCount;
        private final NormalizedVectorMap normalizedCountVector;

        private ForwardIndexEntry(Page page) {
            this.tokenToCount = StringProcessing.getTokenCounts(page.rawText());
            this.normalizedCountVector = new NormalizedVectorMap(this.tokenToCount);
        }
    }
}
