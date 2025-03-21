package com.yummy.patentSys.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Relation {
    private String relationId;
    private String fromId;
    private String toId;
    private String relation; // Use String to represent ENUM
}
