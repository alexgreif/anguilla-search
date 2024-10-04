package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.List;
import java.util.Set;

import java.util.HashMap;
import java.util.Map.Entry;

public class ReverseIndex {
    private Map<String, TfIdfMap> tokenToPagesMap;

    public ReverseIndex(ForwardIndex fi) {
        tokenToPagesMap = new HashMap<>();
        int nPages = fi.nPages();

        // Build initial map with tokens and pages
        for (Entry<String, List<String>> pageTokens : fi.entrySet()) {
            String url = pageTokens.getKey();
            List<String> tkns = pageTokens.getValue();
            for (String tkn : tkns) {
                this.add(tkn, url);
            }
        }

        // Convert counts to tfIdf
        for (Entry<String, TfIdfMap> entry : this.entrySet()) {
            TfIdfMap urls = entry.getValue();
            int nPagesWithToken = urls.nPages();
            for (Entry<String, Double> elem : urls.entrySet()) {
                String url = elem.getKey();
                int nTokensOnPage = fi.nTokens(url);
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

    private void add(String token, String url) {
        if (!tokenToPagesMap.containsKey(token)) {
            tokenToPagesMap.put(token, new TfIdfMap(url));
        } else {
            tokenToPagesMap.get(token).addOrIncrement(url);
        }
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

        TfIdfMap(String url) {
            this.map = new HashMap<>();
            this.map.put(url, 1.);
        }

        void addOrIncrement(String url) {
            this.map.merge(url, 1., Double::sum);
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
