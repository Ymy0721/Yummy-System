package com.yummy.patentSys.stat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityRelationship {
    String entity1;
    String entity1Type;
    String relation;
    String entity2;
    String entity2Type;
    Integer degree;
}
