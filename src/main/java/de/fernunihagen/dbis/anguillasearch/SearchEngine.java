package de.fernunihagen.dbis.anguillasearch;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import de.fernunihagen.dbis.anguillasearch.index.Index;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An index consists of a Forward Index, a Reverse Index and Page Ranks.
 */
public class SearchEngine {
    Index index;
    Map<String, Double> urlToPageRank;
    static final double COSINESIMILARITYWEIGHT = 1;
    static final double TFIDFWEIGHT = 0;
    static final double PAGERANKWEIGHT = 1;
    
    /**
     * Constructs Search Engine from Collection of pages.
     * 
     * @param pages {@code Collection} of {@code Page}s for which to create index
     */
    public SearchEngine(Collection<Page> pages) {
        this.index = new Index(pages);
        this.urlToPageRank = PageRank.compute(pages, 0.85, Math.pow(10,-12));
    }

    /**
     * Starts search engine
     */
    public void run() {
        System.out.println("You can start your search now...");
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter search query (or ’exit’ to quit):");
            String query = scanner.nextLine();
            if (query.equals("exit")) {
                return;
            }
            this.search(query);
        }
    }

    /**
     * Search web pages in index for a query.
     * Calculates final score as weighted average of Cosine Similarity, TfIdf and Page Rank.
     * 
     * @param query the search query
     * @param cosineSimilarityWeight
     * @param tfIdfWeight
     * @param pageRankWeight
     * @return Search results sorted by score
     */
    public SearchResult search(String query, double cosineSimilarityWeight, double tfIdfWeight, double pageRankWeight) {
        Map<String, Double> urlToTfIdf = index.tfIdfQuery(query);
        SearchResult results = new SearchResult();
        for (Map.Entry<String, Double> entry : urlToTfIdf.entrySet()) {
            String url = entry.getKey();
            double tfIdf = entry.getValue();
            double cosineSimilarity = index.cosineSimilarity(url, query);
            double pageRank = this.urlToPageRank.get(url);
            double score = computeScore(cosineSimilarity, tfIdf, pageRank, cosineSimilarityWeight, tfIdfWeight, pageRankWeight);
            results.add(new SearchResult.SearchResultEntry(url, score, cosineSimilarity, tfIdf, pageRank));
        }

        return results;
    }

    /**
     * Search web pages in index for a query.
     * Calculates final score as weighted average of Cosine Similarity, TfIdf and Page Rank.
     * Uses default weights defined as class attributes.
     * 
     * @param query the search query
     * @return Search results sorted by score
     */
    public SearchResult search(String query) {
        return search(query, COSINESIMILARITYWEIGHT, TFIDFWEIGHT, PAGERANKWEIGHT);
    }

    /**
     * Computes overall score as weighted average of Cosine Similarity, TfIdf and Page Rank.
     * Uses default weights defined as class attributes. 
     * 
     * @param cosineSimilarity
     * @param tfIdf
     * @param pageRank
     * @return overall score
     */
    private static double computeScore(double cosineSimilarity, double tfIdf, double pageRank) {
        return computeScore(cosineSimilarity, tfIdf, pageRank,
        COSINESIMILARITYWEIGHT, TFIDFWEIGHT, PAGERANKWEIGHT);
    }

    /**
     * Compute overall score as weighted average of Cosine Similarity, TfIdf and Page Rank.
     * 
     * @param cosineSimilarity
     * @param tfIdf
     * @param pageRank
     * @param cosineSimilarityWeight
     * @param tfIdfWeight
     * @param pageRankWeight
     * @return overall score
     */
    private static double computeScore(double cosineSimilarity, double tfIdf, double pageRank,
    double cosineSimilarityWeight, double tfIdfWeight, double pageRankWeight) {
        double score = cosineSimilarity * cosineSimilarityWeight;
        score += tfIdf * tfIdfWeight;
        score += pageRank * pageRankWeight;
        score = score / (cosineSimilarityWeight + tfIdfWeight + pageRankWeight);
        return score;
    }

    /**
     * Results of a search by all three metrics Cosine Similarity, TfIdf and Page Rank.
     */
    public static class SearchResult {
        SortedSet<SearchResultEntry> results;

        public SearchResult() {
            this.results = new TreeSet<>();
        }

        public void add(SearchResultEntry entry) {
            this.results.add(entry);
        }

        public List<String> getUrls() {
            List<String> urls = new ArrayList<>();
            for (SearchResultEntry entry : this.results) {
                urls.add(entry.url);
            }
            return urls;
        }

        public String toString() {
            return this.results.toString();
        }

        public void prettyPrint() {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i=0; i<94; i++) {
                stringBuilder.append("-");
            }
            String horizontalLine = stringBuilder.toString();
            System.out.println(horizontalLine);
            String urlFormat = "%1$-45s";
            System.out.println(String.format(urlFormat, "| URL") + " | " +
            "Score" + " | " +
            "Cosine Similarity" + " | " +
            "TfIdf" + " | " +
            "PageRank" + " | ");
            System.out.println(horizontalLine);
            
            for (SearchResultEntry entry : this.results) {
                System.out.println(String.format(urlFormat, "| " + entry.url) + " | " +
                StringUtils.center(String.format("%.3f", entry.score), 5) + " | " +
                StringUtils.center(String.format("%.3f", entry.cosineSimilarity), 17) + " | " +
                StringUtils.center(String.format("%.3f", entry.tfIdf), 5) + " | " +
                StringUtils.center(String.format("%.3f", entry.pageRank), 8) + " | ");
            }
            System.out.println(horizontalLine);
            System.out.println();
        }

        static class SearchResultEntry implements Comparable<SearchResultEntry> {
            String url;
            Double score;
            Double cosineSimilarity;
            Double tfIdf;
            Double pageRank;

            SearchResultEntry(String url, Double score, Double cosineSimilarity, Double tfIdf, Double pageRank) {
                this.url = url;
                this.score = score;
                this.cosineSimilarity = cosineSimilarity;
                this.tfIdf = tfIdf;
                this.pageRank = pageRank;
            }

            @Override
            public int compareTo(final SearchResultEntry other) {
                return Double.compare(other.score, this.score);
            }
        }
    }
}


