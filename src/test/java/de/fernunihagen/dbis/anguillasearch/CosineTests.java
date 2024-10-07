package de.fernunihagen.dbis.anguillasearch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.fernunihagen.dbis.anguillasearch.vectors.HashVector;
import de.fernunihagen.dbis.anguillasearch.vectors.Vector;

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
        Vector vecA = new HashVector(vectorA);
        Vector vecB = new HashVector(vectorB);
        assertEquals(1.0, vecA.cosineSimilarity(vecB));

        // and remove the following line
    }

    @Test
    void orthogonalVectors() {
        // Create two orthogonal vectors
        double[] vectorA = { 1.0, 0.0, 0.0 };
        double[] vectorB = { 0.0, 1.0, 0.0 };
        // The cosine similarity of two orthogonal vectors should be 0.0
        // Replace the cosineSimilarity method with your implementation
        Vector vecA = new HashVector(vectorA);
        Vector vecB = new HashVector(vectorB);
        assertEquals(0.0, vecA.cosineSimilarity(vecB));

        // and remove the following line!
    }

    @Test
    void randomVectors() {
        // Create two random vectors
        double[] vectorA = { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] vectorB = { 0.5, 0.4, 0.3, 0.2, 0.1 };
        // The cosine similarity of two random positive vectors should be between 0.0 and 1.0
        // Replace the cosineSimilarity method with your implementation
        Vector vecA = new HashVector(vectorA);
        Vector vecB = new HashVector(vectorB);
        assertTrue(vecA.cosineSimilarity(vecB) > 0.0);
        assertTrue(vecA.cosineSimilarity(vecB) < 1.0);

        // and remove the following line!
    }

    @Test
    void specificResults() {
        // Create two vectors with specific values
        double[] vectorA = { 0.1, 0.2, 0.3, 0.4, 0.5 };
        double[] vectorB = { 0.5, 0.4, 0.3, 0.2, 0.1 };
        // The cosine similarity of these vectors should be 0.7
        // Replace the cosineSimilarity method with your implementation
        Vector vecA = new HashVector(vectorA);
        Vector vecB = new HashVector(vectorB);
        assertTrue(Math.abs(vecA.cosineSimilarity(vecB) - 0.6364) < 0.0001);

        // and remove the following line!
    }
}