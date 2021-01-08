package com.cczero.rorozoro.resource;

import com.cczero.rorozoro.resource.annotation.Resource;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;


import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class ResourceManager {
    public static final TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);

    Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    @Autowired
    private ConversionService conversionService;

    public static void main(String[] args) {
        ResourceManager resourceManager = new ResourceManager();
        resourceManager.loadAllResource();
    }
    public void loadAllResource() {
        //加载所有注解为Resource的类
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackages("com.cczero.rorozoro")
                .addScanners(new SubTypesScanner()));
        Set<Class<?>> resourceClass = reflections.getTypesAnnotatedWith(Resource.class);
        for (Class<?> aClass : resourceClass) {
            loadResource(aClass, "classpath:mapresource.csv");
        }
    }

    public <E> void loadResource(Class<E> clz, String resourceName) {
        ResourceStorage<E> objectResourceStorage = new ResourceStorage<>();
        for (E e : readResourceFile(clz, resourceName)) {
            for (Field declaredField : e.getClass().getDeclaredFields()) {
                try {
                    Object id = declaredField.get(e);
                    if (id instanceof Integer) {
                        objectResourceStorage.put((Integer)id, e);
                    }
                } catch (IllegalAccessException illegalAccessException) {
                    logger.error("", e);
                }
            }

        }
    }

    public <E> List<E> readResourceFile(Class<E> clz, String resourceName) {
        try {
            List<E> result = new ArrayList<>();
            File file = ResourceUtils.getFile(resourceName);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
            BufferedReader br = new BufferedReader(isr);
            String content;
            String headerContext = null;
            boolean readHeader = false;
            List<String> contents = new ArrayList<>();
            while ((content = br.readLine()) != null) {
                //初始化
                if (!readHeader) {
                    readHeader = true;
                    headerContext = content;
                } else {
                    contents.add(content);
                }
            }

            if (headerContext == null) {
                logger.error("表格格式有问题");
                return result;
            }
            //取第一行初始化headers
            String[] headerName = headerContext.split(",");

            for (String objectValue : contents) {
                E object = newInstance(clz);
                //设置各个字段的值
                String[] split = objectValue.split(",");
                for (int i = 0; i < split.length; i++) {
                    if (!StringUtils.hasLength(headerName[i])) {
                        continue;
                    }
                    try {
                        Field declaredField = clz.getDeclaredField(headerName[i]);
                        declaredField.setAccessible(true);
                        try {
                            declaredField.set(object, covert(split[i], declaredField));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    }
                }
                result.add(object);
            }
            return result;
        } catch (FileNotFoundException e) {
            logger.error("not found resource file,file name:{}", resourceName);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private Object covert(String content, Field field) {
        return conversionService.convert(content, sourceType, new TypeDescriptor(field));
    }

    public <E> E newInstance(Class<E> clz) {

        try {
            return clz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
