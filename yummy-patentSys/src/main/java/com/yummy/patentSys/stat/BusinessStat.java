package com.yummy.patentSys.stat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务统计对象
 *
 * @Author yummy
 * @Date 2024-11-08
 */

@Data
@NoArgsConstructor
public class BusinessStat {

    private String name;
    private String value;

    public BusinessStat(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
