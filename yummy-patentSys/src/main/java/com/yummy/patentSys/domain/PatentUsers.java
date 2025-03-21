package com.yummy.patentSys.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yummy.common.annotation.Excel;
import com.yummy.common.core.domain.BaseEntity;

/**
 * 专利用户对象 patent_users
 *
 * @author yummy
 * @date 2024-11-06
 */
public class PatentUsers extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 专利用户唯一标识符 */
    private Long userId;

    /** 用户名 */
    @Excel(name = "用户名")
    private String username;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 用户类型 */
    @Excel(name = "用户类型")
    private String usertype;

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }
    public void setUsertype(String usertype)
    {
        this.usertype = usertype;
    }

    public String getUsertype()
    {
        return usertype;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("username", getUsername())
            .append("password", getPassword())
            .append("usertype", getUsertype())
            .toString();
    }
}
