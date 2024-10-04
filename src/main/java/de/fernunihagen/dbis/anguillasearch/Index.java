package de.fernunihagen.dbis.anguillasearch;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.LinkedHashMap;

public class Index {
    ForwardIndex fi;
    ReverseIndex ri;

    public Index(String[] seedUrls) {
        Crawler crawler = new Crawler(seedUrls);
        crawler.crawl();
        this.fi = new ForwardIndex(crawler.getPages());
        this.ri = new ReverseIndex(fi);
    }

    // Alternativ constructor for tests
    public Index(List<Page> pageList) {
        this.fi = new ForwardIndex(pageList);
        this.ri = new ReverseIndex(fi);
    }

    public SearchResult search(String query) {
        List<String> queryTokens = StringProcessing.process(query);
        ReverseIndex.TfIdfMap queryTokenResult = new ReverseIndex.TfIdfMap();
        for (String token : queryTokens) {
            queryTokenResult.merge(ri.getPagesContaining(token));
        }
        return new SearchResult(queryTokenResult);
    }

    public static class SearchResult {
        Map<String, Double> results;

        public SearchResult(ReverseIndex.TfIdfMap tfIdfMap) {
            this.results = new LinkedHashMap<>(tfIdfMap.getMap());
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
    }
}


