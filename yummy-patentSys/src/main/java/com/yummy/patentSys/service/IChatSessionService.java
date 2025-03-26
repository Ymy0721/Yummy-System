package com.yummy.patentSys.service;

import com.yummy.patentSys.domain.ChatSession;
import java.util.List;

/**
 * 聊天会话管理服务接口
 */
public interface IChatSessionService {
    
    /**
     * 获取用户的所有聊天会话
     * @param userId 用户ID
     * @return 聊天会话列表
     */
    List<ChatSession> getChatSessionsByUserId(Long userId);
    
    /**
     * 获取指定ID的聊天会话
     * @param chatId 会话ID
     * @param userId 用户ID
     * @return 聊天会话
     */
    ChatSession getChatSessionById(Long chatId, Long userId);
    
    /**
     * 创建新的聊天会话
     * @param userId 用户ID
     * @return 新创建的会话
     */
    ChatSession createChatSession(Long userId);
    
    /**
     * 更新聊天会话信息
     * @param chatSession 会话信息
     */
    void updateChatSession(ChatSession chatSession);
    
    /**
     * 删除聊天会话
     * @param chatId 会话ID
     * @param userId 用户ID
     */
    void deleteChatSession(Long chatId, Long userId);
    
    /**
     * 更新会话最后活动时间
     * @param chatId 会话ID
     */
    void updateSessionLastActivity(Long chatId);
}
