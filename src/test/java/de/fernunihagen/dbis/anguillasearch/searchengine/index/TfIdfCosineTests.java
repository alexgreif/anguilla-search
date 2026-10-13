package de.fernunihagen.dbis.anguillasearch.searchengine.index;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import de.fernunihagen.dbis.anguillasearch.Page;

class TfIdfCosineTests {
    private Index index() {
        return new Index(List.of(
            new Page("first", "apple banana", "", "", Map.of()),
            new Page("second", "apple", "", "", Map.of()),
            new Page("third", "orange", "", "", Map.of())
        ));
    }

    @Test
    void usesDifferentCorpusIdfWeightsForDocumentAndQueryComponents() {
        Index index = index();
        double appleIdf = Math.log(3.0 / 2.0);
        double bananaIdf = Math.log(3.0);
        double expected = appleIdf / Math.sqrt(appleIdf * appleIdf + bananaIdf * bananaIdf);

        // The unweighted count-vector result would instead be 1 / sqrt(2).
        assertEquals(expected, index.cosineSimilarity("first", "apple"), 0.000001);
        assertEquals(expected, index.cosineSimilarity("second", "apple banana"), 0.000001);
        assertEquals(1.0, index.cosineSimilarity("first", "apple banana"), 0.000001);
        Map<String, Double> batch = index.cosineSimilarities(List.of("first", "second"), "apple banana");
        assertEquals(1.0, batch.get("first"), 0.000001);
        assertEquals(expected, batch.get("second"), 0.000001);
    }

    @Test
    void weightsRepeatedQueryTokensBeforeNormalization() {
        Index index = index();
        double appleWeight = 2.0 * Math.log(3.0 / 2.0);
        double bananaWeight = Math.log(3.0);
        double expected = appleWeight / Math.sqrt(appleWeight * appleWeight + bananaWeight * bananaWeight);

        assertEquals(expected, index.cosineSimilarity("second", "apple apple banana"), 0.000001);
    }

    @Test
    void handlesUnknownTokensAndEmptyQueries() {
        Index index = index();
        assertEquals(0.0, index.cosineSimilarity("first", "zzzznotawordzzzz"));
        assertEquals(0.0, index.cosineSimilarity("first", ""));
        assertEquals(index.cosineSimilarity("first", "apple"),
            index.cosineSimilarity("first", "apple zzzznotawordzzzz"), 0.000001);
    }

    @Test
    void handlesZeroIdfAndEmptyDocuments() {
        Index common = new Index(List.of(
            new Page("first", "apple", "", "", Map.of()),
            new Page("second", "apple", "", "", Map.of())
        ));
        assertEquals(0.0, common.cosineSimilarity("first", "apple"));
        Index empty = new Index(List.of(new Page("empty", "", "", "", Map.of())));
        assertEquals(0.0, empty.cosineSimilarity("empty", "apple"));
    }
}
