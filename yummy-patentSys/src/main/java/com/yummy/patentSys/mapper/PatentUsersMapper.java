package com.yummy.patentSys.mapper;

import java.util.List;
import com.yummy.patentSys.domain.PatentUsers;

/**
 * 专利用户Mapper接口
 *
 * @author yummy
 * @date 2024-11-06
 */
public interface PatentUsersMapper
{
    /**
     * 查询专利用户
     *
     * @param userId 专利用户主键
     * @return 专利用户
     */
    public PatentUsers selectPatentUsersByUserId(Long userId);

    /**
     * 查询专利用户列表
     *
     * @param patentUsers 专利用户
     * @return 专利用户集合
     */
    public List<PatentUsers> selectPatentUsersList(PatentUsers patentUsers);

    /**
     * 新增专利用户
     *
     * @param patentUsers 专利用户
     * @return 结果
     */
    public int insertPatentUsers(PatentUsers patentUsers);

    /**
     * 修改专利用户
     *
     * @param patentUsers 专利用户
     * @return 结果
     */
    public int updatePatentUsers(PatentUsers patentUsers);

    /**
     * 删除专利用户
     *
     * @param userId 专利用户主键
     * @return 结果
     */
    public int deletePatentUsersByUserId(Long userId);

    /**
     * 批量删除专利用户
     *
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePatentUsersByUserIds(Long[] userIds);
}
