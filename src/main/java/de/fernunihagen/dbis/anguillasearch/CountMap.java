package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Collection;

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

    public int getCountOf(T key) {
        return this.map.get(key);
    }

    public int totalCount() {
        return this.sum;
    }

    public int nDistinct() {
        return this.map.size();
    }

    public Set<T> keySet() {
        return map.keySet();
    }

    public Collection<Integer> values() {
        return map.values();
    }

    public Set<Entry<T, Integer>> entrySet() {
        return map.entrySet();
    }

    public Map<T, Integer> getMap() {
        return this.map;
    }

    public String toString() {
        return this.map.toString();
    }
}
