package com.yummy.patentSys.domain;

import com.yummy.common.annotation.Excel;
import com.yummy.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI模型配置实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiModelConfig extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /** 配置ID */
    private Long id;
    
    /** 模型名称 */
    @Excel(name = "模型名称")
    private String name;
    
    /** 模型代码(如gpt-3.5-turbo, DeepSeek-R1等) */
    @Excel(name = "模型代码")
    private String model;
    
    /** API base URL */
    @Excel(name = "API URL")
    private String baseUrl;
    
    /** API key */
    @Excel(name = "API Key")
    private String apiKey;
    
    /** 最大token数量 */
    @Excel(name = "最大Token")
    private Integer maxTokens;
    
    /** 温度参数 */
    @Excel(name = "温度")
    private Float temperature;
    
    /** 是否为默认模型(0否 1是) */
    @Excel(name = "是否默认", readConverterExp = "0=否,1=是")
    private String isDefault;
    
    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
    
    /** 备注 */
    private String remark;
}
