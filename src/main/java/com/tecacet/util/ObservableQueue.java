package com.tecacet.util;

import java.util.ArrayList;
import java.util.LinkedList;

public class ObservableQueue<T> {

    protected LinkedList<T> list = new LinkedList<T>();
    protected ArrayList<QueueListener<T>> listeners = new ArrayList<QueueListener<T>>();

    public void queue(T j) {
        list.add(j);
        for (QueueListener<T> listener : listeners) {
            listener.itemInserted(j);
        }
    }

    public T getNext() {
        T j = list.removeFirst();
        for (QueueListener<T> listener : listeners) {
            listener.itemRemoved(j);
        }
        return j;
    }

    public void addQueueListener(QueueListener<T> l) {
        listeners.add(l);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}
