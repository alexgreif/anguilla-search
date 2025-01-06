package de.fernunihagen.dbis.anguillasearch.searchengine.index;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void lemmatization() {
        // Only compares size
        List<String> lemmataComputed = StringProcessing.lemmatize(text);
        
        int shorterLength = Math.min(tokens.size(), lemmataComputed.size());
        for (int i=0; i<shorterLength; i++) {
            System.out.println(tokens.get(i) + ":" + lemmataComputed.get(i));
        }

        assertEquals(tokens.size(), lemmataComputed.size());
    }

    @Test
    void process() {
        // Remove stopwords
        List<String> noStopsComputed = StringProcessing.getTokens(text);

        int shorterLength = Math.min(noStops.size(), noStopsComputed.size());
        for (int i=0; i<shorterLength; i++) {
            System.out.println(noStops.get(i) + ":" + noStopsComputed.get(i));
        }
        
        assertEquals(noStops.size(), noStopsComputed.size());
    }
}