package com.cczero.rorozoro.resource;

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

public class ResourceManager {
    public static final TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);

    Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    @Autowired
    private ConversionService conversionService;

    public <E> List<E> readResourceFile(Class<E> clz, String resourceName) {
        try {
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
                return null;
            }
            //取第一行初始化headers
            List<E> result = new ArrayList<>();
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
            logger.error("not foundException,{}", resourceName);
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
