package de.fernunihagen.dbis.anguillasearch;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Collection;

public class ForwardIndex {
    private final Map<String, ForwardIndexEntry> map;

    public ForwardIndex(Collection<Page> pages) {
        map = new HashMap<>(); 
        for (Page page : pages) {
            map.put(page.url, new ForwardIndexEntry(page.tokens()));
        }
    }

    public int nPages() {
        return map.size();
    }

    public Set<Entry<String, ForwardIndexEntry>> entrySet() {
        return map.entrySet();
    }

    public CountMap<String> getTokensOf(String url) {
        return map.get(url).tokenCounts;
    }

    public int nTokens(String url, boolean distinct) {
        return distinct ? this.getTokensOf(url).nDistinct() : this.getTokensOf(url).totalCount();
    }

    public String toString() {
        return map.toString();
    }

    public class ForwardIndexEntry {
        CountMap<String> tokenCounts;
        NormalizedVectorMap normalizedCountVector;

        ForwardIndexEntry(CountMap<String> tokenCounts) {
            this.tokenCounts = tokenCounts;
            this.normalizedCountVector = new NormalizedVectorMap(tokenCounts.getMap());
        }

        public String toString() {
            return this.tokenCounts.toString() + "\n" + this.normalizedCountVector.toString();
        }
    }
}
