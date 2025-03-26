package com.yummy.patentSys.domain;

import com.yummy.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 聊天会话实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatSession extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /** 会话ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 会话标题 */
    private String title;
}
