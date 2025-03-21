package com.yummy.patentSys.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.yummy.patentSys.stat.BusinessStat;
import com.yummy.patentSys.stat.EntityRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yummy.patentSys.mapper.PatentsMapper;
import com.yummy.patentSys.domain.Patents;
import com.yummy.patentSys.service.IPatentsService;

/**
 * 专利数据Service业务层处理
 *
 * @author yummy
 * @date 2024-11-06
 */
@Service
public class PatentsServiceImpl implements IPatentsService
{
    @Autowired
    private PatentsMapper patentsMapper;


    @Override
    public List<BusinessStat> getPatentCountByTime() {
        return patentsMapper.getPatentCountByTime();
    }

    @Override
    public List<BusinessStat> getPatentCountByRegion() {
        return patentsMapper.getPatentCountByRegion();
    }

    @Override
    public List<BusinessStat> getPatentCountByAgency() {
        return patentsMapper.getPatentCountByAgency();
    }

    @Override
    public List<BusinessStat> getPatentCountByApplicant() {
        return patentsMapper.getPatentCountByApplicant();
    }

    @Override
    public List<BusinessStat> getPatentCountByInventor() {
        return patentsMapper.getPatentCountByInventor();
    }

    @Override
    public List<BusinessStat> getPatentCountByType() {
        return patentsMapper.getPatentCountByType();
    }

    @Override
    public List<BusinessStat> getPatentTitleWordCount() {
        return patentsMapper.getPatentTitleWordCount();
    }

    @Override
    public List<EntityRelationship> getEntitiesWithRelationship() {
        return patentsMapper.getEntitiesWithRelationship();
    }

    /**
     * 查询专利数据
     *
     * @param patentId 专利数据主键
     * @return 专利数据
     */
    @Override
    public Patents selectPatentsByPatentId(Long patentId)
    {
        return patentsMapper.selectPatentsByPatentId(patentId);
    }

    /**
     * 查询专利数据列表
     *
     * @param patents 专利数据
     * @return 专利数据
     */
    @Override
    public List<Patents> selectPatentsList(Patents patents)
    {
        return patentsMapper.selectPatentsList(patents);
    }

    /**
     * 新增专利数据
     *
     * @param patents 专利数据
     * @return 结果
     */
    @Override
    public int insertPatents(Patents patents)
    {
        return patentsMapper.insertPatents(patents);
    }

    /**
     * 修改专利数据
     *
     * @param patents 专利数据
     * @return 结果
     */
    @Override
    public int updatePatents(Patents patents)
    {
        return patentsMapper.updatePatents(patents);
    }

    /**
     * 批量删除专利数据
     *
     * @param patentIds 需要删除的专利数据主键
     * @return 结果
     */
    @Override
    public int deletePatentsByPatentIds(Long[] patentIds)
    {
        return patentsMapper.deletePatentsByPatentIds(patentIds);
    }

    /**
     * 删除专利数据信息
     *
     * @param patentId 专利数据主键
     * @return 结果
     */
    @Override
    public int deletePatentsByPatentId(Long patentId)
    {
        return patentsMapper.deletePatentsByPatentId(patentId);
    }
}
