package de.fernunihagen.dbis.anguillasearch;

import java.util.LinkedHashSet;
import java.util.Map;

public class NamedVector extends AbstractVector{
    AbstractVector vector;
    LinkedHashSet<String> names;

    public NamedVector(AbstractVector vector, LinkedHashSet<String> names) {
        this.vector = vector;
        this.names = names;
    }

    public NamedVector(Map<String, Double> map, LinkedHashSet<String> names) {
        int dims = names.size();
        this.names = names;
        this.vector = new HashVector(dims);
    }

    public double get(int index) {
        return vector.get(index);
    }

    public void set(int index, double value) {
        vector.set(index, value);
    }
}
