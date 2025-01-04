package de.fernunihagen.dbis.anguillasearch;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Maps a key to a count. Adding an element will increase it's count by one.
 * This class is used by several other classes, for example for counting links or tokens.
 */
public class CountMap<T> {
    private Map<T, Integer> map;
    private int sum;

    /**
     * Intantiates an empty CountMap.
     */
    public CountMap() {
        this.map = new HashMap<>();
        this.sum = 0;
    }

    /**
     * Increments the count of the element by 1, or sets it to 1 if the key did not exist before.
     *  
     * @param key the key whose count is supposed to be incremented
     */
    public void add(T key) {
        this.map.merge(key, 1, Integer::sum);
        this.sum++;
    }

    /**
     * Retrieves the count of a particular element
     * 
     * @param key key whose count is supposed to be retrieved
     * @return count of given key
     */
    public int getCountOf(T key) {
        return this.map.get(key);
    }

    /**
     * Retrieves the sum of all counts.
     * 
     * @return sum of counts
     */
    public int totalCount() {
        return this.sum;
    }

    /**
     * Retrieves the number of distinct elements
     * 
     * @return number of distinct elements
     */
    public int nDistinct() {
        return this.map.size();
    }

    /**
     * Retrieves all keys
     * 
     * @return {@code Set} of all keys
     */
    public Set<T> keySet() {
        return map.keySet();
    }
    
    /**
     * Returns a {@code Set} of individual entries.
     * This method is used when indexing, to create a vector representation of tokens from the map.
     * 
     * @return {@code Set} of entries
     */
    public Set<Entry<T, Integer>> entrySet() {
        return map.entrySet();
    }

    /**
     * Returns a {@code Map} of keys to counts.
     * This method is used when indexing, to create a vector representation of tokens from the map.
     * 
     * @return {@code Map} of keys to counts
     */
    public Map<T, Integer> getMap() {
        return this.map;
    }
}
