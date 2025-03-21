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
import com.yummy.patentSys.domain.PatentUsers;
import com.yummy.patentSys.service.IPatentUsersService;
import com.yummy.common.utils.poi.ExcelUtil;
import com.yummy.common.core.page.TableDataInfo;

/**
 * 专利用户Controller
 *
 * @author yummy
 * @date 2024-11-06
 */
@RestController
@RequestMapping("/patentSys/users")
public class PatentUsersController extends BaseController
{
    @Autowired
    private IPatentUsersService patentUsersService;

    /**
     * 查询专利用户列表
     */
    @PreAuthorize("@ss.hasPermi('patentSys:users:list')")
    @GetMapping("/list")
    public TableDataInfo list(PatentUsers patentUsers)
    {
        startPage();
        List<PatentUsers> list = patentUsersService.selectPatentUsersList(patentUsers);
        return getDataTable(list);
    }

    /**
     * 导出专利用户列表
     */
    @PreAuthorize("@ss.hasPermi('patentSys:users:export')")
    @Log(title = "专利用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PatentUsers patentUsers)
    {
        List<PatentUsers> list = patentUsersService.selectPatentUsersList(patentUsers);
        ExcelUtil<PatentUsers> util = new ExcelUtil<PatentUsers>(PatentUsers.class);
        util.exportExcel(response, list, "专利用户数据");
    }

    /**
     * 获取专利用户详细信息
     */
    @PreAuthorize("@ss.hasPermi('patentSys:users:query')")
    @GetMapping(value = "/{userId}")
    public AjaxResult getInfo(@PathVariable("userId") Long userId)
    {
        return success(patentUsersService.selectPatentUsersByUserId(userId));
    }

    /**
     * 新增专利用户
     */
    @PreAuthorize("@ss.hasPermi('patentSys:users:add')")
    @Log(title = "专利用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PatentUsers patentUsers)
    {
        return toAjax(patentUsersService.insertPatentUsers(patentUsers));
    }

    /**
     * 修改专利用户
     */
    @PreAuthorize("@ss.hasPermi('patentSys:users:edit')")
    @Log(title = "专利用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PatentUsers patentUsers)
    {
        return toAjax(patentUsersService.updatePatentUsers(patentUsers));
    }

    /**
     * 删除专利用户
     */
    @PreAuthorize("@ss.hasPermi('patentSys:users:remove')")
    @Log(title = "专利用户", businessType = BusinessType.DELETE)
	@DeleteMapping("/{userIds}")
    public AjaxResult remove(@PathVariable Long[] userIds)
    {
        return toAjax(patentUsersService.deletePatentUsersByUserIds(userIds));
    }
}
