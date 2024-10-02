package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class CountMap<T> {
    private Map<T, Integer> map;
    private int sum;

    public CountMap() {
        this.map = new HashMap<>();
        this.sum = 0;
    }

    public void add(T key) {
        this.map.merge(key, 1, Integer::sum);
        this.sum++;
    }

    public int totalCount() {
        return this.sum;
    }

    public int nDistinct() {
        return this.map.size();
    }

    public Set<T> getKeys() {
        return map.keySet();
    }
}
