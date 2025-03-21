package com.yummy.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yummy.common.annotation.Log;
import com.yummy.common.core.controller.BaseController;
import com.yummy.common.core.domain.AjaxResult;
import com.yummy.common.enums.BusinessType;
import com.yummy.patentSys.domain.Patents;
import com.yummy.patentSys.service.IPatentsService;
import com.yummy.common.utils.poi.ExcelUtil;
import com.yummy.common.core.page.TableDataInfo;

/**
 * 专利数据Controller
 *
 * @author yummy
 * @date 2024-11-06
 */
@RestController
@RequestMapping("/patentSys/patents")
public class PatentsController extends BaseController
{
    @Autowired
    private IPatentsService patentsService;

    /**
     * 查询专利数据列表
     */
    @PreAuthorize("@ss.hasPermi('patentSys:patents:list')")
    @GetMapping("/list")
    public TableDataInfo list(Patents patents)
    {
        startPage();
        List<Patents> list = patentsService.selectPatentsList(patents);
        return getDataTable(list);
    }

    /**
     * 导出专利数据列表
     */
    @PreAuthorize("@ss.hasPermi('patentSys:patents:export')")
    @Log(title = "专利数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Patents patents)
    {
        List<Patents> list = patentsService.selectPatentsList(patents);
        ExcelUtil<Patents> util = new ExcelUtil<Patents>(Patents.class);
        util.exportExcel(response, list, "专利数据数据");
    }

    /**
     * 获取专利数据详细信息
     */
    @PreAuthorize("@ss.hasPermi('patentSys:patents:query')")
    @GetMapping(value = "/{patentId}")
    public AjaxResult getInfo(@PathVariable("patentId") Long patentId)
    {
        return success(patentsService.selectPatentsByPatentId(patentId));
    }

    /**
     * 获取专利数据按时间统计信息
     */
//    @PreAuthorize("@ss.hasPermi('patentSys:patents:getPatentCountByTime')")
    @GetMapping(value = "/getPatentCountByTime")
    public AjaxResult getPatentCountByTime()
    {
        return success(patentsService.getPatentCountByTime());
    }

    /**
     * 获取专利数据按地区统计信息
     */
//    @PreAuthorize("@ss.hasPermi('patentSys:patents:getPatentCountByRegion')")
    @GetMapping(value = "/getPatentCountByRegion")
    public AjaxResult getPatentCountByRegion()
    {
        return success(patentsService.getPatentCountByRegion());
    }

    /**
     * 获取专利数据按代理机构统计信息
     */
//    @PreAuthorize("@ss.hasPermi('patentSys:patents:getPatentCountByAgency')")
    @GetMapping(value = "/getPatentCountByAgency")
    public AjaxResult getPatentCountByAgency()
    {
        return success(patentsService.getPatentCountByAgency());
    }

    /**
     * 获取专利数据按申请人统计信息
     */
//    @PreAuthorize("@ss.hasPermi('patentSys:patents:getPatentCountByApplicant')")
    @GetMapping(value = "/getPatentCountByApplicant")
    public AjaxResult getPatentCountByApplicant()
    {
        return success(patentsService.getPatentCountByApplicant());
    }

    /**
     * 获取专利数据按发明人统计信息
     */
//    @PreAuthorize("@ss.hasPermi('patentSys:patents:getPatentCountByInventor')")
    @GetMapping(value = "/getPatentCountByInventor")
    public AjaxResult getPatentCountByInventor()
    {
        return success(patentsService.getPatentCountByInventor());
    }

    /**
     * 获取专利数据按专利类型统计信息
     */
//    @PreAuthorize("@ss.hasPermi('patentSys:patents:getPatentCountByType')")
    @GetMapping(value = "/getPatentCountByType")
    public AjaxResult getPatentCountByType()
    {
        return success(patentsService.getPatentCountByType());
    }

    /**
     * 获取专利标题词频
     */
//    @PreAuthorize("@ss.hasPermi('patentSys:patents:getPatentTitleWordCount')")
    @GetMapping(value = "/getPatentTitleWordCount")
    public AjaxResult getPatentTitleWordCount()
    {
        return success(patentsService.getPatentTitleWordCount());
    }

//    @PreAuthorize("@ss.hasPermi('patentSys:patents:getEntitiesWithRelationship')")
    @GetMapping(value = "/getEntitiesWithRelationship")
    public AjaxResult getEntitiesWithRelationship()
    {
        return success(patentsService.getEntitiesWithRelationship());
    }

    /**
     * 新增专利数据
     */
    @PreAuthorize("@ss.hasPermi('patentSys:patents:add')")
    @Log(title = "专利数据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Patents patents)
    {
        return toAjax(patentsService.insertPatents(patents));
    }

    /**
     * 修改专利数据
     */
    @PreAuthorize("@ss.hasPermi('patentSys:patents:edit')")
    @Log(title = "专利数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Patents patents)
    {
        return toAjax(patentsService.updatePatents(patents));
    }

    /**
     * 删除专利数据
     */
    @PreAuthorize("@ss.hasPermi('patentSys:patents:remove')")
    @Log(title = "专利数据", businessType = BusinessType.DELETE)
	@DeleteMapping("/{patentIds}")
    public AjaxResult remove(@PathVariable Long[] patentIds)
    {
        return toAjax(patentsService.deletePatentsByPatentIds(patentIds));
    }
}
