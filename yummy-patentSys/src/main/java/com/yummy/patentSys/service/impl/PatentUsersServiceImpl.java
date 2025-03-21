package com.yummy.patentSys.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yummy.patentSys.mapper.PatentUsersMapper;
import com.yummy.patentSys.domain.PatentUsers;
import com.yummy.patentSys.service.IPatentUsersService;

/**
 * 专利用户Service业务层处理
 *
 * @author yummy
 * @date 2024-11-06
 */
@Service
public class PatentUsersServiceImpl implements IPatentUsersService
{
    @Autowired
    private PatentUsersMapper patentUsersMapper;

    /**
     * 查询专利用户
     *
     * @param userId 专利用户主键
     * @return 专利用户
     */
    @Override
    public PatentUsers selectPatentUsersByUserId(Long userId)
    {
        return patentUsersMapper.selectPatentUsersByUserId(userId);
    }

    /**
     * 查询专利用户列表
     *
     * @param patentUsers 专利用户
     * @return 专利用户
     */
    @Override
    public List<PatentUsers> selectPatentUsersList(PatentUsers patentUsers)
    {
        return patentUsersMapper.selectPatentUsersList(patentUsers);
    }

    /**
     * 新增专利用户
     *
     * @param patentUsers 专利用户
     * @return 结果
     */
    @Override
    public int insertPatentUsers(PatentUsers patentUsers)
    {
        return patentUsersMapper.insertPatentUsers(patentUsers);
    }

    /**
     * 修改专利用户
     *
     * @param patentUsers 专利用户
     * @return 结果
     */
    @Override
    public int updatePatentUsers(PatentUsers patentUsers)
    {
        return patentUsersMapper.updatePatentUsers(patentUsers);
    }

    /**
     * 批量删除专利用户
     *
     * @param userIds 需要删除的专利用户主键
     * @return 结果
     */
    @Override
    public int deletePatentUsersByUserIds(Long[] userIds)
    {
        return patentUsersMapper.deletePatentUsersByUserIds(userIds);
    }

    /**
     * 删除专利用户信息
     *
     * @param userId 专利用户主键
     * @return 结果
     */
    @Override
    public int deletePatentUsersByUserId(Long userId)
    {
        return patentUsersMapper.deletePatentUsersByUserId(userId);
    }
}
