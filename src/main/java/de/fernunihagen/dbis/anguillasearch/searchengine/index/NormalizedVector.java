package de.fernunihagen.dbis.anguillasearch.searchengine.index;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashMap;

/**
 * Create normalized vectors from non-normalized ones.
 * Normalized in this context meaning vectors with a euclidean norm of 1.
 * Also offers methods to compute dot products between normalized vectors.
 * 
 * This class is used to store normalized vector representations of the tokens on a
 * web page (as part of the Forward Index) or in a query string in a memory efficient way.
 * Since the dot product between two normalized vectors is equivalent to their cosine similarity,
 * this provides an efficient means to calculating the match between a web page and a
 * query string.
 */
class NormalizedVector {
    /**
     * Map representation of the vector.
     * Components without entries in this map are assumed to equal 0.
     */
    private Map<String, Double> map;

    /**
     * Constructs a normalized vector with a euclidean norm of 1.
     * Components without entries in the provided map are assumed to equal 0.
     */
    NormalizedVector(Map<String, ? extends Number> map) {
        // Calculate inverse Euclidean norm
        double sum = 0;
        for (Number number : map.values()) {
            double numDouble = number.doubleValue();
            sum += numDouble*numDouble;
        }
        Double invNorm = 1 / Math.sqrt(sum);

        // Put normalized entries
        this.map = new HashMap<>();
        for (Entry<String, ? extends Number> entry : map.entrySet()) {
            this.map.put(entry.getKey(), entry.getValue().doubleValue()*invNorm);
        }
    }

    /**
     * Computes the dot product between this and other vector.
     * This implementation of the dot product is best to use if
     * this vector has fewer non-zero entries than other vector.
     * The dot product between two normalized vectors is equivalent to their cosine similarity.
     * 
     * @param other vector with whom dot product is to be computed
     * @return dot product between this and other
     */
    double dotThisBased(NormalizedVector other) {
        double sum = 0;
        for (Entry<String, Double> entry : this.map.entrySet()) {
            sum += entry.getValue() * other.get(entry.getKey());
        }
        return sum;
    }

    /**
     * Computes the dot product between this and other vector.
     * This implementation of the dot product is best to use if
     * both vectors have quite a few non-zero entries but their intersection is rather small.
     * The dot product between two normalized vectors is equivalent to their cosine similarity.
     * 
     * @param other vector with whom dot product is to be computed
     * @return dot product between this and other
     */
    double dotSymmetrical(NormalizedVector other) {
        Set<String> nonZeroIndices = this.map.keySet();
        nonZeroIndices.retainAll(other.map.keySet());
        double sum = 0;
        for (String i : nonZeroIndices) {
            sum += this.get(i) * other.get(i);
        }
        return sum;
    }

    /**
     * Computes the dot product between this and other vector.
     * This implementation of the dot product is best to use if
     * one vector has fewer non-zero values than the other, but it's unknown which one.
     * The dot product between two normalized vectors is equivalent to their cosine similarity.
     * 
     * @param other vector with whom dot product is to be computed
     * @return dot product between this and other
     */
    double dotAsymmetrical(NormalizedVector other) {
        if (this.map.size() < other.map.size()) {
            return this.dotThisBased(other);
        }
        return other.dotThisBased(this);
    }

    /**
     * Returns string representation of non-zero vector entries.
     */
    public String toString() {
        return this.map.toString();
    }

    /**
     * Retrieves component of vector by index.
     * This method is used in dot product computation.
     * 
     * @param index component at this index will be retrieved
     * @return vector component at given index
     */
    private double get(String index) {
        return this.map.getOrDefault(index, 0.);
    }
}
 