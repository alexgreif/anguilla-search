package de.fernunihagen.dbis.anguillasearch;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Collection;

/**
 * A Forward Index organizes tokens by page.
 * Includes methods for creating and reading Forward Indices.
 * Once created, a Forward Index cannot be modified.
 */
public class ForwardIndex {
    private final Map<String, ForwardIndexEntry> map;

    /**
     * Creates a Forward Index from a Collection of Pages
     * 
     * @param pages {@code Collection} of pages for which to build Forward Index
     */
    public ForwardIndex(Collection<Page> pages) {
        map = new HashMap<>(); 
        for (Page page : pages) {
            map.put(page.url, new ForwardIndexEntry(page.tokens()));
        }
    }

    /**
     * Retrieves number of pages in this Forward Index.
     * 
     * @return number of pages in this Forward Index
     */
    public int nPages() {
        return map.size();
    }

    /**
     * Returns set of entries of this Forward Index.
     * 
     * @return {@code Set} of entries of this Forward Index
     */
    public Set<Entry<String, ForwardIndexEntry>> entrySet() {
        return map.entrySet();
    }

    /**
     * Retrieves tokens of a specific page and their numbers of occurence.
     * 
     * @param url url of the page whose tokens are to be retrieved
     * @return {@code CountMap} of tokens and their number of occurence on given page
     */
    public CountMap<String> getTokensOf(String url) {
        return map.get(url).tokenToCount;
    }

    /**
     * Retrieves tokens of a specific page and their normalized numbers of occurence.
     * Numbers of occurence are normalized, so that their euclidean norm equals 1.
     * 
     * @param url url of the page whose tokens are to be retrieved
     * @return {@code NormalizedVectorMap} of tokens and their normalized numbers of occurence
     */
    public NormalizedVectorMap getVectorOf(String url) {
        return map.get(url).normalizedCountVector;
    }

    /**
     * Returns number of tokens of a given web page.
     * This can be the total number of tokens or the number of distinct tokens.
     * 
     * @param url url of the page whose number of tokens is to be returned
     * @param distinct if true, number of distinct tokens will be returned, if false, total number of tokens will be returned
     * @return distinct or total number of tokens of given web page
     */
    public int nTokens(String url, boolean distinct) {
        return distinct ? this.getTokensOf(url).nDistinct() : this.getTokensOf(url).totalCount();
    }

    /**
     * One entry of a Forward Index for a single web page.
     */
    protected class ForwardIndexEntry {
        protected final CountMap<String> tokenToCount;
        protected final NormalizedVectorMap normalizedCountVector;

        protected ForwardIndexEntry(CountMap<String> tokenCounts) {
            this.tokenToCount = tokenCounts;
            this.normalizedCountVector = new NormalizedVectorMap(tokenCounts.getMap());
        }
    }
}
