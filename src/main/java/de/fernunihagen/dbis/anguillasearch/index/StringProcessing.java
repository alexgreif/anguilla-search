package de.fernunihagen.dbis.anguillasearch.index;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains static methods to preprocess strings for indexing and searching.
 * Preprocessing involves tokenization, lemmatization, setting all characters to lowercase and removal of stopwords.
 * This preprocessing is applied to search queries and web pages' text contents.
 */
class StringProcessing {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringProcessing.class);

    private StringProcessing() {}

    /**
     * Extracts tokens from a string and maps them to their number of occurence.
     * This method is used for search queries and page content.
     * 
     * @param text {@code String} to search for tokens
     * @return {@code Map} of tokens to their number of occurence
     */
    static Map<String, Integer> getTokenCounts(String text) {
        List<String> tokens = getTokens(text);
        Map<String, Integer> tokenToCount = new HashMap<>();
        for (String token : tokens) {
            tokenToCount.merge(token, 1, Integer::sum);
        }
        return tokenToCount;
    }

    /**
     * Extracts tokens from a string.
     * Returns a list of all tokens in order of appearance in string, including duplicates.
     * This method does most of the work of the getTokenCounts method.
     * 
     * @param text {@code String} to search for tokens
     * @return {@code List} of tokens
     */
    static List<String> getTokens(String text) {
        // Lemmatize
        List<String> tokens = lemmatize(text);

        // Remove stop words
        Set<String> stopwords;
        try {
            stopwords = loadStopwords();
            tokens.removeAll(stopwords);
        } catch(IOException e) {
            LOGGER.error("No stopwords.txt found, so no stop words were removed.");
        }
        
        return tokens;
    }

    /**
     * Lemmatizes a String.
     * Only returns lemmata that contain letters.
     * 
     * @param text {@code String} to be lemmatized
     * @return {@code List} of lemmata
     */
    static List<String> lemmatize(String text) {
        // Prepare pipeline
        Properties props = new Properties();
        props.setProperty("annotators","tokenize,ssplit,pos,lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Apply pipeline to input
        Annotation annot = new Annotation(text);
        annot.set(CoreAnnotations.TextAnnotation.class, text);
        pipeline.annotate(annot);

        // Convert each lemma to lower case and dispose of all lemmata that do not contain letters
        List<String> lemmata = new ArrayList<>();
        for (CoreLabel token : annot.get(CoreAnnotations.TokensAnnotation.class)) {
            String lemma = token.lemma().toLowerCase();
            if (lemma.matches(".*[a-z].*")) {
                lemmata.add(lemma);
            }
        }
        return lemmata;
    }

    /**
     * Loads file containing stop words.
     * 
     * @return {@code Set} of stop words
     * @throws IOException
     */
    private static Set<String> loadStopwords() throws IOException{
        Set<String> stopwords = new HashSet<>();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("stopwords.txt");
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            for(String line; (line = br.readLine()) != null; ) {
                if (!line.isEmpty()) {
                    stopwords.add(line);
                }
            }
        }
        return stopwords;
    }
}
