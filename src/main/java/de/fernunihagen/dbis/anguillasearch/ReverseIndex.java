package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.Set;

import java.util.HashMap;
import java.util.Map.Entry;

public class ReverseIndex {
    private Map<String, TfIdfMap> tokenToPagesMap;

    public ReverseIndex(ForwardIndex fi) {
        tokenToPagesMap = new HashMap<>();
        int nPages = fi.nPages();

        // Build initial map with tokens and pages
        for (Entry<String, ForwardIndex.ForwardIndexEntry> pageTokens : fi.entrySet()) {
            String url = pageTokens.getKey();
            CountMap<String> tkns = pageTokens.getValue().tokenCounts;
            for (Entry<String, Integer> tkn : tkns.entrySet()) {
                this.add(tkn.getKey(), url, tkn.getValue());
            }
        }

        // Convert counts to tfIdf
        for (Entry<String, TfIdfMap> entry : this.entrySet()) {
            TfIdfMap urls = entry.getValue();
            int nPagesWithToken = urls.nPages();
            for (Entry<String, Double> elem : urls.entrySet()) {
                String url = elem.getKey();
                int nTokensOnPage = fi.nTokens(url, false);
                int countTokenOnPage = elem.getValue().intValue();
                double tfIdf = computeTfIdf(countTokenOnPage, nTokensOnPage, nPages, nPagesWithToken);
                urls.setTfIdf(url, tfIdf);
            }
        }
    }

    public boolean containsToken(String token) {
        return tokenToPagesMap.containsKey(token);
    }

    public Set<String> getUrls(String token) {
        return this.getPagesContaining(token).getUrls();
    }

    private void add(String token, String url, int count) {
        tokenToPagesMap.putIfAbsent(token, new TfIdfMap());
        tokenToPagesMap.get(token).setTfIdf(url, (double)count);
    }

    static double computeTfIdf(int countTokenOnPage, int nTokensOnPage, int nPages, int nPagesWithToken) {
        double tf = (double)countTokenOnPage / nTokensOnPage;
        double idf = Math.log((double)nPages / nPagesWithToken);
        return tf * idf;
    }

    public TfIdfMap getPagesContaining(String token) {
        return tokenToPagesMap.get(token);
    }
        
    public double getTfIdf(String token, String url) {
        return this.getPagesContaining(token).getTfIdfOf(url);
    }

    public Set<Entry<String, TfIdfMap>> entrySet() {
        return tokenToPagesMap.entrySet();
    }

    public String toString() {
        return tokenToPagesMap.toString();
    }

    public static class TfIdfMap {
        Map<String, Double> map;

        TfIdfMap() {
            this.map = new HashMap<>();
        }

        TfIdfMap(String url, int count) {
            this.map = new HashMap<>();
            this.map.put(url, (double)count);
        }

        boolean containsPage(String url) {
            return map.containsKey(url);
        }

        Double getTfIdfOf(String url) {
            return map.getOrDefault(url, 0.);
        }

        void setTfIdf(String url, Double tfIdf) {
            map.put(url, tfIdf);
        }

        int nPages() {
            return map.size();
        }

        Set<String> getUrls() {
            return map.keySet();
        } 

        void merge(TfIdfMap other) {
            other.map.forEach((k, v) -> this.map.merge(k, v, Double::sum));
        }
        
        public Set<Entry<String, Double>> entrySet() {
            return this.map.entrySet();
        }
    
        public Map<String, Double> getMap() {
            return this.map;
        }

        public String toString() {
            return map.toString();
        }
    }
}
