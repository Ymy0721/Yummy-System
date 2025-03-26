package com.yummy.patentSys.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yummy.common.utils.DateUtils;
import com.yummy.patentSys.mapper.AiModelConfigMapper;
import com.yummy.patentSys.domain.AiModelConfig;
import com.yummy.patentSys.service.IAiModelConfigService;

/**
 * AI模型配置Service实现
 */
@Service
public class AiModelConfigServiceImpl implements IAiModelConfigService {
    @Autowired
    private AiModelConfigMapper aiModelConfigMapper;

    /**
     * 查询AI模型配置
     * 
     * @param id 配置ID
     * @return AI模型配置
     */
    @Override
    public AiModelConfig selectAiModelConfigById(Long id) {
        return aiModelConfigMapper.selectAiModelConfigById(id);
    }

    /**
     * 查询AI模型配置列表
     * 
     * @param aiModelConfig 查询参数
     * @return AI模型配置集合
     */
    @Override
    public List<AiModelConfig> selectAiModelConfigList(AiModelConfig aiModelConfig) {
        return aiModelConfigMapper.selectAiModelConfigList(aiModelConfig);
    }

    /**
     * 获取默认AI模型配置
     */
    @Override
    public AiModelConfig getDefaultModelConfig() {
        return aiModelConfigMapper.selectDefaultAiModelConfig();
    }
    
    /**
     * 新增AI模型配置
     * 
     * @param aiModelConfig AI模型配置
     * @return 结果
     */
    @Override
    @Transactional
    public int insertAiModelConfig(AiModelConfig aiModelConfig) {
        aiModelConfig.setCreateTime(DateUtils.getNowDate());
        
        // 如果设置为默认模型，则清除其他默认标记
        if ("1".equals(aiModelConfig.getIsDefault())) {
            aiModelConfigMapper.clearDefaultFlag();
        }
        
        return aiModelConfigMapper.insertAiModelConfig(aiModelConfig);
    }

    /**
     * 修改AI模型配置
     * 
     * @param aiModelConfig AI模型配置
     * @return 结果
     */
    @Override
    @Transactional
    public int updateAiModelConfig(AiModelConfig aiModelConfig) {
        aiModelConfig.setUpdateTime(DateUtils.getNowDate());
        
        // 如果设置为默认模型，则清除其他默认标记
        if ("1".equals(aiModelConfig.getIsDefault())) {
            aiModelConfigMapper.clearDefaultFlag();
        }
        
        return aiModelConfigMapper.updateAiModelConfig(aiModelConfig);
    }

    /**
     * 删除AI模型配置
     * 
     * @param id 配置ID
     * @return 结果
     */
    @Override
    public int deleteAiModelConfigById(Long id) {
        return aiModelConfigMapper.deleteAiModelConfigById(id);
    }

    /**
     * 批量删除AI模型配置
     * 
     * @param ids 需要删除的配置ID
     * @return 结果
     */
    @Override
    public int deleteAiModelConfigByIds(Long[] ids) {
        return aiModelConfigMapper.deleteAiModelConfigByIds(ids);
    }
}
