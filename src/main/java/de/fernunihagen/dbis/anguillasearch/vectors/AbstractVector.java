package de.fernunihagen.dbis.anguillasearch.vectors;

import java.util.Arrays;
import java.util.Iterator;

public abstract class AbstractVector implements Iterable<Double>, Vector {
    protected int dims;

    public double dot(Vector other) {
        this.exceptionIfDifferentLengths(other);
        int sum = 0;
        for (int i = 0; i < this.dims; i++) {
            sum += this.get(i) * other.get(i);
        }
        return sum;
    }

    public double euclidianNorm() {
        double sum = 0;
        for (double entry : this) {
            sum += entry*entry;
        }
        return Math.sqrt(sum);
    }

    public double cosineSimilarity(Vector other) {
        return this.dot(other) / (this.euclidianNorm() * other.euclidianNorm());
    }

    public void normalize() {
        double invNorm = this.euclidianNorm();
        for (int i = 0; i < this.dims; i++) {
            this.set(i, this.get(i) * invNorm);
        }
    }

    public Iterator<Double> iterator() {
        Double[] valueArray = new Double[this.dims];
        for (int i = 0; i < valueArray.length; i++) {
            valueArray[i] = this.get(i);
        }
        return Arrays.asList(valueArray).iterator();
    }

    protected void exceptionIfInvalidIndex(int index) {
        if (index >= this.dims || index < 0) {
            throw new IllegalArgumentException(
                index + " is not a valid index in vector of dimension " + this.dims
                ); 
        }
    }

    protected void exceptionIfDifferentLengths(Vector other) {
        if (this.dims != other.getDims()) {
            throw new IllegalArgumentException(
                "This has dimension " + this.dims + ", other has dimension " + other.getDims()
                ); 
        }
    }

    public int getDims() {
        return this.dims;
    }
} 