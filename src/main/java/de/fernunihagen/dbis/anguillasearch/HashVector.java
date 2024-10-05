package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map.Entry;

public class HashVector extends Vector {
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

    @Override
    public double dot(Vector other) {
        double sum = 0;
        for (Entry<Integer, Double> entry : this.map.entrySet()) {
            sum += entry.getValue() * other.get(entry.getKey());
        }
        return sum;
    }

    public double dot(HashVector other) {
        Set<Integer> nonZeroIndices = new HashSet<>(this.map.keySet());
        nonZeroIndices.retainAll(other.map.keySet());
        double sum = 0;
        for (int i : nonZeroIndices) {
            sum += this.get(i) * other.get(i);
        }
        return sum;
    }

    @Override
    public double euclidianNorm() {
        double sum = 0;
        for (double value : this.map.values()) {
            sum += Math.pow(value, 2);
        }
        return Math.sqrt(sum);
    }

    @Override
    public void normalize() {
        double norm = this.euclidianNorm();
        for (Entry<Integer, Double> entry : this.map.entrySet()) {
            this.set(entry.getKey(), entry.getValue() / norm);
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
}
