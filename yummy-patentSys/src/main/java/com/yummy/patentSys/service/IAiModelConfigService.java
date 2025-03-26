package com.yummy.patentSys.service;

import com.yummy.patentSys.domain.AiModelConfig;
import java.util.List;

/**
 * AI模型配置服务接口
 */
public interface IAiModelConfigService {
    /**
     * 查询AI模型配置
     * 
     * @param id 配置ID
     * @return AI模型配置
     */
    public AiModelConfig selectAiModelConfigById(Long id);

    /**
     * 查询AI模型配置列表
     * 
     * @param aiModelConfig 查询参数
     * @return AI模型配置集合
     */
    public List<AiModelConfig> selectAiModelConfigList(AiModelConfig aiModelConfig);

    /**
     * 获取默认AI模型配置
     * 
     * @return AI模型配置
     */
    public AiModelConfig getDefaultModelConfig();
    
    /**
     * 新增AI模型配置
     * 
     * @param aiModelConfig AI模型配置
     * @return 结果
     */
    public int insertAiModelConfig(AiModelConfig aiModelConfig);

    /**
     * 修改AI模型配置
     * 
     * @param aiModelConfig AI模型配置
     * @return 结果
     */
    public int updateAiModelConfig(AiModelConfig aiModelConfig);

    /**
     * 删除AI模型配置信息
     * 
     * @param id 配置ID
     * @return 结果
     */
    public int deleteAiModelConfigById(Long id);

    /**
     * 批量删除AI模型配置
     * 
     * @param ids 需要删除的配置ID
     * @return 结果
     */
    public int deleteAiModelConfigByIds(Long[] ids);
}
