package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map.Entry;

public class HashVector extends AbstractVector {
    Map<Integer, Double> map;

    public HashVector(int dims) {
        this.dims = dims;
        this.map = new HashMap<>();
    }

    public HashVector(int dims, Map<Integer, Double> map) {
        this.dims = dims;
        this.map = new HashMap<>(map);
    }

    public HashVector(double[] valueArray) {
        this.dims = valueArray.length;
        this.map = new HashMap<>();
        double value;
        for (int i = 0; i < this.dims; i++) {
            if ((value = valueArray[i]) != 0) {
                this.map.put(i, value);
            }
        }
    }

    public HashVector(Vector vector) {
        this.dims = vector.getDims();
        this.map = new HashMap<>();
        double value;
        for (int i = 0; i < this.dims; i++) {
            if ((value = vector.get(i)) != 0) {
                this.map.put(i, value);
            }
        }
    }

    public static HashVector fromCountMap(CountMap countMap, boolean normalize) {
        // Not yet implemented
        return new HashVector(1);
    }

    @Override
    public double dot(Vector other) {
        double sum = 0;
        for (Entry<Integer, Double> entry : this.map.entrySet()) {
            sum += entry.getValue() * other.get(entry.getKey());
        }
        return sum;
    }

    public double dotSymmetrical(HashVector other) {
        // Best to use if both vectors have quite a few non-zero entries
        // but intersection is rather small
        Set<Integer> nonZeroIndices = this.nonZeroIndices();
        nonZeroIndices.retainAll(other.nonZeroIndices());
        double sum = 0;
        for (int i : nonZeroIndices) {
            sum += this.get(i) * other.get(i);
        }
        return sum;
    }

    public double dotAsymmetrical(HashVector other) {
        // Better to use if one vector has fewer non-zero values than other
        // but unclear which one
        if (this.nonZeroIndices().size() < other.nonZeroIndices().size()) {
            return this.dotThisBased(other);
        }
        return other.dotThisBased(this);
    }

    public double dotThisBased(HashVector other) {
        // Better to use if this has fewer non-zero entries than other
        Set<Integer> nonZeroIndices = this.nonZeroIndices();
        nonZeroIndices.retainAll(other.nonZeroIndices());
        double sum = 0;
        for (Entry<Integer, Double> entry : this.map.entrySet()) {
            sum += entry.getValue() * other.get(entry.getKey());
        }
        return sum;
    }

    @Override
    public double euclidianNorm() {
        double sum = 0;
        for (double value : this.map.values()) {
            sum += value*value;
        }
        return Math.sqrt(sum);
    }

    @Override
    public void normalize() {
        double invNorm = 1 / this.euclidianNorm();
        for (Entry<Integer, Double> entry : this.map.entrySet()) {
            this.set(entry.getKey(), entry.getValue() * invNorm);
        }
    }

    public void set(int index, double value) {
        this.exceptionIfInvalidIndex(index);
        this.map.put(index, value);
    }

    public double get(int index) {
        this.exceptionIfInvalidIndex(index);
        return this.map.getOrDefault(index, 0.);
    }

    private Set<Integer> nonZeroIndices() {
        return this.map.keySet();
    }
}
