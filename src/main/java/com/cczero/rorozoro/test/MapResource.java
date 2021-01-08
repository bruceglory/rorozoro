package com.cczero.rorozoro.test;

import com.cczero.rorozoro.resource.annotation.Id;
import com.cczero.rorozoro.resource.annotation.Resource;
import lombok.Data;

@Resource
@Data
public class MapResource {
    @Id
    private int id;
}
