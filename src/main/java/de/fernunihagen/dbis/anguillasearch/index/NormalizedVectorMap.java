package de.fernunihagen.dbis.anguillasearch.index;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.HashMap;

public class NormalizedVectorMap {
    Map<String, Double> map;

    public NormalizedVectorMap(Map<String, ? extends Number> map) {
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

    // Constructor from array for tests
    public static NormalizedVectorMap fromArray(double[] array) {
        Map<String, Double> map = new HashMap<>();
        for (int i = 0; i < array.length; i++) {
            double value = array[i];
            if (value != 0) {
                map.put(String.valueOf(i), array[i]);
            }
        }
        return new NormalizedVectorMap(map);
    }

    private double get(String index) {
        return this.map.getOrDefault(index, 0.);
    }

    public double dotSymmetrical(NormalizedVectorMap other) {
        // Best to use if both vectors have quite a few non-zero entries
        // but intersection is rather small
        Set<String> nonZeroIndices = this.map.keySet();
        nonZeroIndices.retainAll(other.map.keySet());
        double sum = 0;
        for (String i : nonZeroIndices) {
            sum += this.get(i) * other.get(i);
        }
        return sum;
    }

    public double dotAsymmetrical(NormalizedVectorMap other) {
        // Better to use if one vector has fewer non-zero values than other
        // but unclear which one
        if (this.map.size() < other.map.size()) {
            return this.dotThisBased(other);
        }
        return other.dotThisBased(this);
    }

    public double dotThisBased(NormalizedVectorMap other) {
        // Better to use if this has fewer non-zero entries than other
        double sum = 0;
        for (Entry<String, Double> entry : this.map.entrySet()) {
            sum += entry.getValue() * other.get(entry.getKey());
        }
        return sum;
    }

    public String toString() {
        return this.map.toString();
    }
}
 