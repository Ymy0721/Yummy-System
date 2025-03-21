package com.yummy.patentSys.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entity {
    private String entityId;
    private String name;
    private int start;
    private int end;
    private String type; // Use String to represent ENUM
    private int patentId;
}
