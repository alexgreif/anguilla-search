package de.fernunihagen.dbis.anguillasearch.index;

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
        // Replace the cosineSimilarity method with your implementation
        NormalizedVector vecA = new NormalizedVector(arrayToMap(vectorA));
        NormalizedVector vecB = new NormalizedVector(arrayToMap(vectorB));
        assertTrue(Math.abs(vecA.dotThisBased(vecB) - 1) < 0.0001);
        assertTrue(Math.abs(vecA.dotAsymmetrical(vecB) - 1) < 0.0001);
        assertTrue(Math.abs(vecA.dotSymmetrical(vecB) - 1) < 0.0001);
        assertTrue(Math.abs(vecB.dotThisBased(vecA) - 1) < 0.0001);
        assertTrue(Math.abs(vecB.dotAsymmetrical(vecA) - 1) < 0.0001);
        assertTrue(Math.abs(vecB.dotSymmetrical(vecA) - 1) < 0.0001);

        // and remove the following line
    }

    @Test
    void orthogonalVectors() {
        // Create two orthogonal vectors
        double[] vectorA = { 1.0, 0.0, 0.0 };
        double[] vectorB = { 0.0, 1.0, 0.0 };
        // The cosine similarity of two orthogonal vectors should be 0.0
        // Replace the cosineSimilarity method with your implementation
        NormalizedVector vecA = new NormalizedVector(arrayToMap(vectorA));
        NormalizedVector vecB = new NormalizedVector(arrayToMap(vectorB));
        assertEquals(0, vecA.dotThisBased(vecB));
        assertEquals(0, vecA.dotAsymmetrical(vecB));
        assertEquals(0, vecA.dotSymmetrical(vecB));
        assertEquals(0, vecB.dotThisBased(vecA));
        assertEquals(0, vecB.dotAsymmetrical(vecA));
        assertEquals(0, vecB.dotSymmetrical(vecA));

        // and remove the following line!
    }

    @Test
    void randomVectors() {
        // Create two random vectors
        double[] vectorA = { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] vectorB = { 0.5, 0.4, 0.3, 0.2, 0.1 };
        // The cosine similarity of two random positive vectors should be between 0.0 and 1.0
        // Replace the cosineSimilarity method with your implementation
        NormalizedVector vecA = new NormalizedVector(arrayToMap(vectorA));
        NormalizedVector vecB = new NormalizedVector(arrayToMap(vectorB));
        assertTrue(vecA.dotThisBased(vecB) > 0.0);
        assertTrue(vecA.dotThisBased(vecB) < 1.0);
        assertTrue(vecA.dotAsymmetrical(vecB) > 0.0);
        assertTrue(vecA.dotAsymmetrical(vecB) < 1.0);
        assertTrue(vecA.dotSymmetrical(vecB) > 0.0);
        assertTrue(vecA.dotSymmetrical(vecB) < 1.0);
        assertTrue(vecB.dotThisBased(vecA) > 0.0);
        assertTrue(vecB.dotThisBased(vecA) < 1.0);
        assertTrue(vecB.dotAsymmetrical(vecA) > 0.0);
        assertTrue(vecB.dotAsymmetrical(vecA) < 1.0);
        assertTrue(vecB.dotSymmetrical(vecA) > 0.0);
        assertTrue(vecB.dotSymmetrical(vecA) < 1.0);

        // and remove the following line!
    }

    @Test
    void specificResults() {
        // Create two vectors with specific values
        double[] vectorA = { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] vectorB = { 0.5, 0.4, 0.3, 0.2, 0.1 };
        // The cosine similarity of these vectors should be 0.7
        // Replace the cosineSimilarity method with your implementation
        NormalizedVector vecA = new NormalizedVector(arrayToMap(vectorA));
        NormalizedVector vecB = new NormalizedVector(arrayToMap(vectorB));
        assertTrue(Math.abs(vecA.dotThisBased(vecB) - 0.6364) < 0.0001);
        assertTrue(Math.abs(vecA.dotAsymmetrical(vecB) - 0.6364) < 0.0001);
        assertTrue(Math.abs(vecA.dotSymmetrical(vecB) - 0.6364) < 0.0001);
        assertTrue(Math.abs(vecB.dotThisBased(vecA) - 0.6364) < 0.0001);
        assertTrue(Math.abs(vecB.dotAsymmetrical(vecA) - 0.6364) < 0.0001);
        assertTrue(Math.abs(vecB.dotSymmetrical(vecA) - 0.6364) < 0.0001);

        // and remove the following line!
    }

    private static <T> Map<String, Double> arrayToMap(double[] array) {
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