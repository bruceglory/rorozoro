package com.cczero.rorozoro.resource;

import com.cczero.rorozoro.resource.annotation.Id;
import lombok.Getter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

public class ResourceDefinition {
    private Class clazz;

    public ResourceDefinition(Class clazz) {
        this.clazz = clazz;
    }

}
