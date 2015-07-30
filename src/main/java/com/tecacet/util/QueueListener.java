package com.tecacet.util;

public interface QueueListener<T> {
    
    void itemInserted(T job);

    void itemRemoved(T job);
}