package com.yummy.patentSys.service;

import java.util.List;
import java.util.Map;

import com.yummy.patentSys.domain.Patents;
import com.yummy.patentSys.stat.BusinessStat;
import com.yummy.patentSys.stat.EntityRelationship;

/**
 * 专利数据Service接口
 *
 * @author yummy
 * @date 2024-11-06
 */
public interface IPatentsService
{

    List<BusinessStat> getPatentCountByTime();

    List<BusinessStat> getPatentCountByRegion();

    List<BusinessStat> getPatentCountByAgency();

    List<BusinessStat> getPatentCountByApplicant();

    List<BusinessStat> getPatentCountByInventor();

    List<BusinessStat> getPatentCountByType();

    List<BusinessStat> getPatentTitleWordCount();

    /**
     * 查询关系大于10的实体及其关系
     *
     * @return 实体关系集合
     */
    public List<EntityRelationship> getEntitiesWithRelationship();

    /**
     * 查询专利数据
     *
     * @param patentId 专利数据主键
     * @return 专利数据
     */
    public Patents selectPatentsByPatentId(Long patentId);

    /**
     * 查询专利数据列表
     *
     * @param patents 专利数据
     * @return 专利数据集合
     */
    public List<Patents> selectPatentsList(Patents patents);

    /**
     * 新增专利数据
     *
     * @param patents 专利数据
     * @return 结果
     */
    public int insertPatents(Patents patents);

    /**
     * 修改专利数据
     *
     * @param patents 专利数据
     * @return 结果
     */
    public int updatePatents(Patents patents);

    /**
     * 批量删除专利数据
     *
     * @param patentIds 需要删除的专利数据主键集合
     * @return 结果
     */
    public int deletePatentsByPatentIds(Long[] patentIds);

    /**
     * 删除专利数据信息
     *
     * @param patentId 专利数据主键
     * @return 结果
     */
    public int deletePatentsByPatentId(Long patentId);
}
