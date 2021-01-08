package com.cczero.rorozoro.resource;

import java.util.HashMap;
import java.util.Map;

public class ResourceStorage<T> {
    private Map<Integer, T> values = new HashMap<>();

    public void put(int id, T resource) {
        values.put(id, resource);
    }

    public T get(int id) {
        return values.get(id);
    }
}
