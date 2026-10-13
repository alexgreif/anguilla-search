package de.fernunihagen.dbis.anguillasearch.searchengine;

import java.util.Map;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import de.fernunihagen.dbis.anguillasearch.Page;
import de.fernunihagen.dbis.anguillasearch.searchengine.index.Index;

/**
 * Finds web pages matching a search query and sorts the results by fit and relevance.
 * To do so, the Search Engine uses the Index and PageRank.
 */
public class SearchEngine {
    private Index index;
    private Map<String, Double> urlToPageRank;
    private Searcher searcher;
    /**
     * Weights for TfIdf, Cosine Similarity and PageRank.
     * In exactly that order.
     */
    private static final double[] SCOREWEIGHTS = {0, 1, 1};
    
    /**
     * Constructs Search Engine from Collection of pages.
     * 
     * @param pages {@code Collection} of {@code Page}s for which to create index
     */
    public SearchEngine(Collection<Page> pages) {
        this.index = new Index(pages);
        this.urlToPageRank = PageRank.compute(pages, 0.85, Math.pow(10,-12), (int) Math.pow(10,6),false);
        this.searcher = new Searcher();
    }

    /**
     * Search web pages in index for a query.
     * Results are sorted in descending order by a total score, calculated as weighted
     * average of standardized values of Cosine Similarity, TfIdf and Page Rank.
     * 
     * @param query the search query
     * @param print if {@code true}, prints results in a neatly formatted table
     * @return Search results sorted by score
     */
    public SortedSet<SearchResultEntry> search(String query, Boolean print) {
        // PageRank mean ist zwar 1/n, aber der gesparte Rechenaufwand wäre es nicht Wert das durch einen Sonderfall auszunutzen
        return search(query, SCOREWEIGHTS, print);
    }

    /**
     * Search web pages in index for a query.
     * Results are sorted in descending order by a total score, calculated as weighted
     * average of standardized values of Cosine Similarity, TfIdf and Page Rank.
     * 
     * @param query the search query
     * @param scoreWeights weights for TfIdf, Cosine Similarity and PageRank, in that exact order
     * @param print if {@code true}, prints results in a neatly formatted table
     * @return Search results sorted by score
     */
    public SortedSet<SearchResultEntry> search(String query, double[] scoreWeights, Boolean print) {
        // PageRank mean ist zwar 1/n, aber der gesparte Rechenaufwand wäre es nicht Wert das durch einen Sonderfall auszunutzen
        SortedSet<SearchResultEntry> results = this.searcher.search(query, scoreWeights);
        if (Boolean.TRUE.equals(print)) {
            printResults(results);
        }
        return results;
    }

    /**
     * Print the search results as a table sorted by total score in descending order.
     * Table columns are URL, total score, cosine similarity, TfIdf score, PageRank.
     */
    private static void printResults(SortedSet<SearchResultEntry> results) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<94; i++) {
            stringBuilder.append("-");
        }
        String horizontalLine = stringBuilder.toString();
        System.out.println(horizontalLine);
        String urlFormat = "%1$-45s";
        System.out.println(String.format(urlFormat, "| URL") + " | " +
        "Score" + "  | " +
        "TfIdf" + " | " +
        "Cosine Similarity" + " | " +
        "PageRank" + " | ");
        System.out.println(horizontalLine);
        
        for (SearchResultEntry entry : results) {
            System.out.println(String.format(urlFormat, "| " + entry.url) + " | " +
            StringUtils.center(String.format("%.3f", entry.score), 6) + " | " +
            StringUtils.center(String.format("%.3f", entry.tfIdf), 5) + " | " +
            StringUtils.center(String.format("%.3f", entry.cosineSimilarity), 17) + " | " +
            StringUtils.center(String.format("%.3f", entry.pageRank), 8) + " | ");
        }
        System.out.println(horizontalLine);
        System.out.println();
    }

    /**
     * Performs search and computes relevant metrics.
     */
    private class Searcher {
        /**
         * The query string
         */
        private String query;
        /**
         * Weights for TfIdf, Cosine Similarity and PageRank.
         * In exactly that order.
         */
        private double[] scoreWeights;
        /**
         * {@code Array} of URLs of pages containing at least one search token.
         */
        private String[] urls;
        /**
         * 2d-{@code Array} of raw scores by web pages.
         * "Rows" (first index) are pages.
         * "Columns" (second index) are the three scores TfIdf, Cosine Similarity and PageRank, in this exact order.
         */
        private double[][] rawScores;
        /**
         * 2d-{@code Array} of standardized scores by web pages.
         * "Rows" (first index) are pages.
         * "Columns" (second index) are the three scores TfIdf, Cosine Similarity and PageRank, in this exact order.
         */
        private double[][] standardizedScores;
        /**
         * {@code Array} of totalScore for each web page
         */
        private double[] totalScores;
        /**
         * The number of pages containing at least one search token
         */
        private int nPages;

        /**
         * Constructor that does nothing.
         * Except for constructing.
         */
        private Searcher() {}

        /**
         * Finds pages containing at least one of the query tokens.
         * Returns results ordered in descending order by total score.
         * 
         * @param query the search query
         * @param scoreWeights weights for TfIdf, Cosine Similarity and PageRank, in that exact order
         * @return {@code SortedSet} of found pages
         */
        private SortedSet<SearchResultEntry> search(String query, double[] scoreWeights) {
            // Set up instance variables
            Map<String, Double> urlToTfIdf = SearchEngine.this.index.tfIdfQuery(query);
            this.nPages = urlToTfIdf.size();
            this.query = query;
            this.scoreWeights = scoreWeights;
            this.urls = new String[this.nPages];
            this.rawScores = new double[this.nPages][3];
            this.standardizedScores = new double[this.nPages][3];
            this.totalScores = new double[this.nPages];
            
            // Compute scores
            double[] sums = {0., 0., 0.};
            double[] squareSums = {0., 0., 0.};
            this.setUrlsAndRawScores(sums, squareSums, urlToTfIdf);
            this.setStandardizedAndTotalScores(sums, squareSums);

            // Structure and return results
            SortedSet<SearchResultEntry> results = new TreeSet<>();
            for (int i=0; i<nPages; i++) {
                results.add(new SearchResultEntry(urls[i], totalScores[i], rawScores[i][0], rawScores[i][1], rawScores[i][2]));
            }
            return results;
        }

        /**
         * Computes standardized scores and then total score.
         * Saves results in instance variables.
         * 
         * @param sums {@code Array} containing the sum for each score
         * @param squareSums {@code Array} containing the sum of squared values for each score
         */
        private void setStandardizedAndTotalScores(double[] sums, double[] squareSums) {
            // Calculate means and standard deviations for the 3 scores
            double[] means = new double[3];
            double[] standardDeviations = new double[3];
            for (int i=0; i<3; i++) {
                means[i] = sums[i] / this.nPages;
                double sd = Math.sqrt(squareSums[i] / this.nPages - means[i] * means[i]);
                standardDeviations[i] = sd != 0 ? sd : 1;
                // If standard deviation is zero, we get NaN when standardizing because of zero division.
                // However, if sd is zero, all values are the same, so there would be no information in that score anyway.
                // By assigning an arbitrary non-zero value to standardDeviations[i], the i-th of the three metrics
                // will basically be ignored, which is exactly what we want in that case.
            }
            
            // Calculate total score based on standardized scores and weights
            for (int i=0; i<this.nPages; i++) {
                for (int j=0; j<3; j++) {
                    this.standardizedScores[i][j] = (rawScores[i][j] - means[j]) / standardDeviations[j];
                }
                this.totalScores[i] = weightedAverage(this.standardizedScores[i], this.scoreWeights);
            }
        }

        /**
         * Assigns the correct values to instance variables for URLs and raw scores.
         * Also computes sum of values and sum of squared values for each score in passed {@code Arrays}.
         * 
         * @param sums {@code Array} for sums of values
         * @param squareSums {@code Array} for sums of scores
         * @param urlToTfIdf {@code Map} from pages containing at least one of the search tokens to their TfIdf for the query
         */
        private void setUrlsAndRawScores(double[] sums, double[] squareSums, Map<String, Double> urlToTfIdf) {
            
            Map<String, Double> cosineScores = index.cosineSimilarities(urlToTfIdf.keySet(), query);
            // Loop through pages to get scores and calculate sum and sum of squared values
            int i = 0;
            for (Map.Entry<String, Double> entry : urlToTfIdf.entrySet()) {
                // Retrieve/compute scores
                String url = entry.getKey();
                this.urls[i] = url;
                rawScores[i][0] = entry.getValue(); // TfIdf
                rawScores[i][1] = cosineScores.get(url); // Cosine Similarity
                rawScores[i][2] = SearchEngine.this.urlToPageRank.get(url); // PageRank
    
                // Sum and sum of squared values
                for (int j=0; j<3; j++) {
                    double score = rawScores[i][j];
                    sums[j] += score;
                    squareSums[j] += score * score;
                }
    
                // Move index along with pages
                i++;
            }
        }

        /**
         * Helper method to compute a weighted average.
         * Values and weights are matched by index in {@code Array}.
         * 
         * @param values {@code Array} of the values to be averaged
         * @param weights {@code Array} of the corresponding weights
         * @return
         */
        private static double weightedAverage(double[] values, double[] weights) {
            int n = Math.min(values.length, weights.length);
            double sum = 0;
            double weightSum = 0;
            for (int i=0; i<n; i++) {
                if (weights[i] == 0) {
                    continue;
                }
                sum += values[i] * weights[i];
                weightSum += weights[i];
            }
            if (weightSum == 0) {
                throw new IllegalArgumentException("Weights cannot sum to 0");
            }
            return sum / weightSum;
        }
    }

    /**
     * Search Result Entry for a single page.
     * Consists of the page's URL along with search metrics.
     */
    private static class SearchResultEntry implements Comparable<SearchResultEntry> {
        private String url;
        private Double score;
        private Double tfIdf;
        private Double cosineSimilarity;
        private Double pageRank;

        /**
         * Construct a Search Result Entry for a single web page
         * 
         * @param url
         * @param score
         * @param cosineSimilarity
         * @param tfIdf
         * @param pageRank
         */
        private SearchResultEntry(String url, Double score, Double tfIdf, Double cosineSimilarity, Double pageRank) {
            this.url = url;
            this.score = score;
            this.tfIdf = tfIdf;
            this.cosineSimilarity = cosineSimilarity;
            this.pageRank = pageRank;
        }

        /**
         * Compares search entries by descending score, then by URL to retain tied pages.
         * This method helps sort search results by total score.
         */
        @Override
        public int compareTo(final SearchResultEntry other) {
            int scoreComparison = Double.compare(other.score, this.score);
            return scoreComparison != 0 ? scoreComparison : this.url.compareTo(other.url);
        }
    }
}


