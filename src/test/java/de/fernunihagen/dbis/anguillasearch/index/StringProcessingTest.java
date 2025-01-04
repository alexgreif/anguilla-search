package de.fernunihagen.dbis.anguillasearch.index;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the crawler.
 */
class StringProcessingTest {
    static String text;
    static List<String> tokens;
    static List<String> noStops;

    @BeforeAll
    static void setUp() {
        // Used AI to generate random text
        text = "Natural Language Processing (NLP), as a multidisciplinary domain,"
        + "amalgamates computational linguistics and statistical methodologies;"
        + " it endeavors to facilitate the seamless interaction between humans and machines. "
        + "Should one delve into the intricacies of syntactic parse trees or semantic ambiguity"
        + " resolution, one may ascertain the profound implications of machine learning on "
        + "understanding human language. Consequently, if researchers continue to innovate, "
        + "the prospects of achieving near-human proficiency in linguistic comprehension are "
        + "not merely optimistic but, indeed, incumbent upon advancing neural architectures "
        + "and algorithmic paradigms!";

        String[] tokenArray = {
            "natural", "language", "processing", "nlp", "as", "a", "multidisciplinary", "domain",
            "amalgamates", "computational", "linguistics", "and", "statistical", "methodologies",
            "it", "endeavors", "to", "facilitate", "the", "seamless", "interaction", "between",
            "humans", "and", "machines", "should", "one", "delve", "into", "the", "intricacies",
            "of", "syntactic", "parse", "trees", "or", "semantic", "ambiguity", "resolution",
            "one", "may", "ascertain", "the", "profound", "implications", "of", "machine",
            "learning", "on", "understanding", "human", "language", "consequently", "if",
            "researchers", "continue", "to", "innovate", "the", "prospects", "of", "achieving",
            "near", "human", "proficiency", "in", "linguistic", "comprehension", "are", "not",
            "merely", "optimistic", "but", "indeed", "incumbent", "upon", "advancing", "neural",
            "architectures", "and", "algorithmic", "paradigms"
        };

        String[] noStopsArray = {
            "natural", "language", "processing", "nlp", "multidisciplinary", "domain",
            "amalgamates", "computational", "linguistics", "statistical", "methodologies",
            "endeavors", "facilitate", "seamless", "interaction", 
            "humans", "machines", "should", "one", "delve", "intricacies",
            "syntactic", "parse", "trees", "semantic", "ambiguity", "resolution",
            "one", "may", "ascertain", "profound", "implications", "machine",
            "learning", "understanding", "human", "language", "consequently", 
            "researchers", "continue", "innovate", "prospects", "achieving",
            "near", "human", "proficiency", "linguistic", "comprehension",
            "merely", "optimistic", "indeed", "incumbent", "advancing", "neural",
            "architectures", "algorithmic", "paradigms"
        };


        tokens = new ArrayList<>(Arrays.asList(tokenArray));
        noStops = new ArrayList<>(Arrays.asList(noStopsArray));
    }

    @Test
    void tokenize() {
        List<String> computedTokens = StringProcessing.tokenize(text);
        System.out.println(computedTokens);

        assertEquals(tokens.size(), computedTokens.size());
        for (int i = 0; i < tokens.size(); i++) {
            assertEquals(tokens.get(i), computedTokens.get(i));
        } 
    }

    @Test
    void stopwords() {
        // Remove stopwords
        List<String> noStopsComputed;
        try {
            noStopsComputed = StringProcessing.removeStopwords(tokens);
        } catch (IOException e) {
            System.out.println(e);
            assertTrue(false);
            return;
        }

        System.out.println(noStopsComputed);

        // Compare to manually removed stopwords
        assertEquals(noStops.size(), noStopsComputed.size());
        for (int i = 0; i < noStops.size(); i++) {
            assertEquals(noStops.get(i), noStopsComputed.get(i));
        }   
    }

    @Test
    void lemmatization() {
        // Only compares size
        List<String> lemmatizedTokens = new ArrayList<>();
        for (String token : noStops) {
            lemmatizedTokens.add(StringProcessing.lemmatize(token));
        }
        System.out.println(lemmatizedTokens);

        assertEquals(noStops.size(), lemmatizedTokens.size());
    }
}