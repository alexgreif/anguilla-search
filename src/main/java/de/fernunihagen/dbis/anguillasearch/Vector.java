package de.fernunihagen.dbis.anguillasearch;

import java.util.Iterator;

public interface Vector {
    public double dot(Vector other);

    public double euclidianNorm();

    public double cosineSimilarity(Vector other);

    public void normalize();

    public Iterator<Double> iterator();

    public int getDims();

    public double get(int index);
    public void set(int index, double value);
}
