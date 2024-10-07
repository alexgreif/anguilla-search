package de.fernunihagen.dbis.anguillasearch;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.LinkedHashMap;

public class Index {
    ForwardIndex fi;
    ReverseIndex ri;
    List<Page> pages;
    List<String> tokens;
    

    public Index(String[] seedUrls) {
        Crawler crawler = new Crawler(seedUrls);
        crawler.crawl();
        this.pages = new ArrayList<>(crawler.getPages());
        this.fi = new ForwardIndex(crawler.getPages());
        this.ri = new ReverseIndex(fi);

    }

    // Alternativ constructor for tests
    public Index(List<Page> pageList) {
        this.fi = new ForwardIndex(pageList);
        this.ri = new ReverseIndex(fi);
    }

    public SearchResult tfIdfSearch(String query) {
        CountMap<String> queryTokens = StringProcessing.process(query);
        ReverseIndex.TfIdfMap queryTokenResult = new ReverseIndex.TfIdfMap();
        for (String token : queryTokens.keySet()) {
            queryTokenResult.merge(ri.getPagesContaining(token));
        }
        return new SearchResult(queryTokenResult);
    }

    public SearchResult vectorSearch(String query) {
        CountMap<String> queryTokens = StringProcessing.process(query);
        // System.out.println("Found following tokens and weights:");
        // for (Entry<String, Integer> entry : queryTokens.entrySet()) {
        //     System.out.println(entry.getKey() + ": " + entry.getValue());
        // }
        // System.out.println("If you want to assign a new weight to a token, type 'token: weight'");
        // System.out.println("When you are done, submit an empty line.");
        // while (True) {
        //     System.in.
        // }
        NormalizedVectorMap normalizedQueryVector = new NormalizedVectorMap(queryTokens.getMap());
        Map<String, Double> results = new HashMap<>();
        for (Entry<String, ForwardIndex.ForwardIndexEntry> entry : this.fi.entrySet()) {
            double cosineSimilarity = normalizedQueryVector.dotThisBased(entry.getValue().normalizedCountVector);
            if (cosineSimilarity != 0) {
                results.put(entry.getKey(), cosineSimilarity);
            }
        }
        return new SearchResult(results);
    }

    public static class SearchResult {
        Map<String, Double> results;

        public SearchResult(ReverseIndex.TfIdfMap tfIdfMap) {
            this.results = new LinkedHashMap<>(tfIdfMap.getMap());
            this.sort();
        }

        public SearchResult(Map<String, Double> resultsMap) {
            this.results = new LinkedHashMap<>(resultsMap);
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

        public void prettyPrint() {
            for (Entry<String, Double> entry : this.results.entrySet()) {
                System.out.println(entry.getKey() + ": " + String.format("%.3f", entry.getValue()));
            }
        }
    }

    // public class ForwardIndex {
    //     private final Map<String, ForwardIndexEntry> map;

    //     public ForwardIndex(Collection<Page> pages) {
    //         map = new HashMap<>(); 
    //         for (Page page : pages) {
    //             map.put(page.url, new ForwardIndexEntry(page.tokens()));
    //         }
    //     }

    //     void computeVector()

    //     public int nPages() {
    //         return map.size();
    //     }

    //     public Set<Entry<String, ForwardIndexEntry>> entrySet() {
    //         return map.entrySet();
    //     }

    //     public CountMap<String> getTokensOf(String url) {
    //         return map.get(url).tokenCounts;
    //     }

    //     public int nTokens(String url, boolean distinct) {
    //         return distinct ? this.getTokensOf(url).nDistinct() : this.getTokensOf(url).totalCount();
    //     }

    //     public String toString() {
    //         return map.toString();
    //     }

    //     public class ForwardIndexEntry {
    //         CountMap<String> tokenCounts;
    //         AbstractVector tokenVector;

    //         ForwardIndexEntry(CountMap<String> tokenCounts) {
    //             this.tokenCounts = tokenCounts;
    //             tokenVector = new HashVector(tokenCounts.getMap());
    //         }
    //     }
    // }

    // public class ReverseIndex {
    //     private Map<String, TfIdfMap> tokenToPagesMap;
    
    //     public ReverseIndex(ForwardIndex fi) {
    //         tokenToPagesMap = new HashMap<>();
    //         int nPages = fi.nPages();
    
    //         // Build initial map with tokens and pages
    //         for (Entry<String, CountMap<String>> pageTokens : fi.entrySet().tokenCounts) {
    //             String url = pageTokens.getKey();
    //             CountMap<String> tkns = pageTokens.getValue();
    //             for (Entry<String, Integer> tkn : tkns.entrySet()) {
    //                 this.add(tkn.getKey(), url, tkn.getValue());
    //             }
    //         }
    
    //         // Convert counts to tfIdf
    //         for (Entry<String, TfIdfMap> entry : this.entrySet()) {
    //             TfIdfMap urls = entry.getValue();
    //             int nPagesWithToken = urls.nPages();
    //             for (Entry<String, Double> elem : urls.entrySet()) {
    //                 String url = elem.getKey();
    //                 int nTokensOnPage = fi.nTokens(url, false);
    //                 int countTokenOnPage = elem.getValue().intValue();
    //                 double tfIdf = computeTfIdf(countTokenOnPage, nTokensOnPage, nPages, nPagesWithToken);
    //                 urls.setTfIdf(url, tfIdf);
    //             }
    //         }
    //     }
    
    //     public boolean containsToken(String token) {
    //         return tokenToPagesMap.containsKey(token);
    //     }
    
    //     public Set<String> getUrls(String token) {
    //         return this.getPagesContaining(token).getUrls();
    //     }
    
    //     private void add(String token, String url, int count) {
    //         tokenToPagesMap.putIfAbsent(token, new TfIdfMap());
    //         tokenToPagesMap.get(token).setTfIdf(url, (double)count);
    //     }
    
    //     static double computeTfIdf(int countTokenOnPage, int nTokensOnPage, int nPages, int nPagesWithToken) {
    //         double tf = (double)countTokenOnPage / nTokensOnPage;
    //         double idf = Math.log((double)nPages / nPagesWithToken);
    //         return tf * idf;
    //     }
    
    //     public TfIdfMap getPagesContaining(String token) {
    //         return tokenToPagesMap.get(token);
    //     }
            
    //     public double getTfIdf(String token, String url) {
    //         return this.getPagesContaining(token).getTfIdfOf(url);
    //     }
    
    //     public Set<Entry<String, TfIdfMap>> entrySet() {
    //         return tokenToPagesMap.entrySet();
    //     }
    
    //     public String toString() {
    //         return tokenToPagesMap.toString();
    //     }
    
    //     public static class TfIdfMap {
    //         Map<String, Double> map;
    
    //         TfIdfMap() {
    //             this.map = new HashMap<>();
    //         }
    
    //         TfIdfMap(String url, int count) {
    //             this.map = new HashMap<>();
    //             this.map.put(url, (double)count);
    //         }
    
    //         boolean containsPage(String url) {
    //             return map.containsKey(url);
    //         }
    
    //         Double getTfIdfOf(String url) {
    //             return map.getOrDefault(url, 0.);
    //         }
    
    //         void setTfIdf(String url, Double tfIdf) {
    //             map.put(url, tfIdf);
    //         }
    
    //         int nPages() {
    //             return map.size();
    //         }
    
    //         Set<String> getUrls() {
    //             return map.keySet();
    //         } 
    
    //         void merge(TfIdfMap other) {
    //             other.map.forEach((k, v) -> this.map.merge(k, v, Double::sum));
    //         }
            
    //         public Set<Entry<String, Double>> entrySet() {
    //             return this.map.entrySet();
    //         }
        
    //         public Map<String, Double> getMap() {
    //             return this.map;
    //         }
    
    //         public String toString() {
    //             return map.toString();
    //         }
    //     }
    //}
}


