package com.yummy.patentSys.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天消息实体类
 */
@Data
public class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 消息ID */
    private Long id;
    
    /** 所属会话ID */
    private Long chatId;
    
    /** 用户ID */
    private Long userId;
    
    /** 角色（user/assistant/system/tool） */
    private String role;
    
    /** 消息内容 */
    private String content;
    
    /** 函数调用对象（JSON存储，不持久化到数据库） */
    @JsonIgnore
    private Object functionCall;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 认证令牌（仅传输用，不持久化） */
    @JsonIgnore
    private String token;
}
