package com.yummy.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.yummy.common.annotation.Log;
import com.yummy.common.core.controller.BaseController;
import com.yummy.common.core.domain.AjaxResult;
import com.yummy.common.enums.BusinessType;
import com.yummy.patentSys.domain.AiModelConfig;
import com.yummy.patentSys.service.IAiModelConfigService;
import com.yummy.common.utils.poi.ExcelUtil;
import com.yummy.common.core.page.TableDataInfo;

/**
 * AI模型配置Controller
 */
@RestController
@RequestMapping("/patentSys/aimodel")
public class AiModelConfigController extends BaseController {
    @Autowired
    private IAiModelConfigService aiModelConfigService;

    /**
     * 查询AI模型配置列表
     */
    @PreAuthorize("@ss.hasPermi('patentSys:aimodel:list')")
    @GetMapping("/list")
    public TableDataInfo list(AiModelConfig aiModelConfig) {
        startPage();
        List<AiModelConfig> list = aiModelConfigService.selectAiModelConfigList(aiModelConfig);
        return getDataTable(list);
    }

    /**
     * 导出AI模型配置列表
     */
    @PreAuthorize("@ss.hasPermi('patentSys:aimodel:export')")
    @Log(title = "AI模型配置", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(AiModelConfig aiModelConfig) {
        List<AiModelConfig> list = aiModelConfigService.selectAiModelConfigList(aiModelConfig);
        ExcelUtil<AiModelConfig> util = new ExcelUtil<>(AiModelConfig.class);
        return util.exportExcel(list, "AI模型配置");
    }

    /**
     * 获取AI模型配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('patentSys:aimodel:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(aiModelConfigService.selectAiModelConfigById(id));
    }

    /**
     * 获取所有可用的AI模型
     */
    @GetMapping("/options")
    public AjaxResult getModelOptions() {
        AiModelConfig query = new AiModelConfig();
        query.setStatus("0"); // 只查询启用状态的模型
        List<AiModelConfig> list = aiModelConfigService.selectAiModelConfigList(query);
        return AjaxResult.success(list);
    }

    /**
     * 新增AI模型配置
     */
    @PreAuthorize("@ss.hasPermi('patentSys:aimodel:add')")
    @Log(title = "AI模型配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AiModelConfig aiModelConfig) {
        return toAjax(aiModelConfigService.insertAiModelConfig(aiModelConfig));
    }

    /**
     * 修改AI模型配置
     */
    @PreAuthorize("@ss.hasPermi('patentSys:aimodel:edit')")
    @Log(title = "AI模型配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AiModelConfig aiModelConfig) {
        return toAjax(aiModelConfigService.updateAiModelConfig(aiModelConfig));
    }

    /**
     * 删除AI模型配置
     */
    @PreAuthorize("@ss.hasPermi('patentSys:aimodel:remove')")
    @Log(title = "AI模型配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(aiModelConfigService.deleteAiModelConfigByIds(ids));
    }
}
