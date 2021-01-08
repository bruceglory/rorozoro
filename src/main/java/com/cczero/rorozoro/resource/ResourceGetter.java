package com.cczero.rorozoro.resource;

import java.util.Map;

public class ResourceGetter {
    private Map<Class, ResourceStorage> storageMap;

    public <E> E getResourceById(Class<E> clz, int id) {
        return (E)storageMap.get(clz).get(id);
    }
}
