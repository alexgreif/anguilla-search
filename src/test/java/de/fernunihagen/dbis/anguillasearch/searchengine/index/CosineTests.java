package de.fernunihagen.dbis.anguillasearch.searchengine.index;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for cosine similarity.
 */
class CosineTests {


    @Test
    void equalVectors() {
        // Create two vector with random positive double values
        double[] vectorA = { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] vectorB = { 0.1, 0.2, 0.3, 0.4, 0.5 };

        // The cosine similarity of two equal vectors should be 1.0
        NormalizedVector vecA = new NormalizedVector(arrayToMap(vectorA));
        NormalizedVector vecB = new NormalizedVector(arrayToMap(vectorB));
        assertTrue(Math.abs(vecA.dotThisBased(vecB) - 1) < 0.0001);
        assertTrue(Math.abs(vecB.dotThisBased(vecA) - 1) < 0.0001);
    }

    @Test
    void orthogonalVectors() {
        // Create two orthogonal vectors
        double[] vectorA = { 1.0, 0.0, 0.0 };
        double[] vectorB = { 0.0, 1.0, 0.0 };
        // The cosine similarity of two orthogonal vectors should be 0.0
        NormalizedVector vecA = new NormalizedVector(arrayToMap(vectorA));
        NormalizedVector vecB = new NormalizedVector(arrayToMap(vectorB));
        assertEquals(0, vecA.dotThisBased(vecB));
        assertEquals(0, vecB.dotThisBased(vecA));
    }

    @Test
    void randomVectors() {
        // Create two random vectors
        double[] vectorA = { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] vectorB = { 0.5, 0.4, 0.3, 0.2, 0.1 };
        // The cosine similarity of two random positive vectors should be between 0.0 and 1.0
        NormalizedVector vecA = new NormalizedVector(arrayToMap(vectorA));
        NormalizedVector vecB = new NormalizedVector(arrayToMap(vectorB));
        assertTrue(vecA.dotThisBased(vecB) > 0.0);
        assertTrue(vecA.dotThisBased(vecB) < 1.0);
        assertTrue(vecB.dotThisBased(vecA) > 0.0);
        assertTrue(vecB.dotThisBased(vecA) < 1.0);
    }

    @Test
    void specificResults() {
        // Create two vectors with specific values
        double[] vectorA = { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] vectorB = { 0.5, 0.4, 0.3, 0.2, 0.1 };
        // The cosine similarity of these vectors should be approximately 0.6364
        NormalizedVector vecA = new NormalizedVector(arrayToMap(vectorA));
        NormalizedVector vecB = new NormalizedVector(arrayToMap(vectorB));
        assertTrue(Math.abs(vecA.dotThisBased(vecB) - 0.6364) < 0.0001);
        assertTrue(Math.abs(vecB.dotThisBased(vecA) - 0.6364) < 0.0001);
    }

    @Test
    void repeatedComparisonsPreserveVectorComponents() {
        NormalizedVector vector = new NormalizedVector(Map.of("a", 1.0, "b", 1.0));
        NormalizedVector firstAxis = new NormalizedVector(Map.of("a", 1.0));
        NormalizedVector secondAxis = new NormalizedVector(Map.of("b", 1.0));
        double expected = 1.0 / Math.sqrt(2.0);

        assertEquals(expected, vector.dotThisBased(firstAxis), 0.0001);
        assertEquals(expected, vector.dotThisBased(secondAxis), 0.0001);
        assertEquals(expected, vector.dotThisBased(firstAxis), 0.0001);
        assertEquals(expected, firstAxis.dotThisBased(vector), 0.0001);
        assertEquals(expected, secondAxis.dotThisBased(vector), 0.0001);
    }

    private static Map<String, Double> arrayToMap(double[] array) {
        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            double value = array[i];
            if (value != 0) {
                map.put(String.valueOf(i), array[i]);
            }
        }
        return map;
    }
}
