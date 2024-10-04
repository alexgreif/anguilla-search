package de.fernunihagen.dbis.anguillasearch;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Collection;

public class ForwardIndex {
    private final Map<String, List<String>> map;

    public ForwardIndex(Collection<Page> pages) {
        map = new HashMap<>(); 
        for (Page page : pages) {
            map.put(page.url, page.tokens());
        }
    }

    public int nPages() {
        return map.size();
    }

    public Set<Entry<String, List<String>>> entrySet() {
        return map.entrySet();
    }

    public List<String> getTokensOf(String url) {
        return map.get(url);
    }

    public int nTokens(String url) {
        return this.getTokensOf(url).size();
    }

    public String toString() {
        return map.toString();
    }
}
