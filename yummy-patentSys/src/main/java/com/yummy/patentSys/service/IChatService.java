package com.yummy.patentSys.service;

import com.yummy.patentSys.domain.ChatSession;
import com.yummy.patentSys.domain.ChatMessage;

import java.util.List;

/**
 * AI聊天服务接口
 */
public interface IChatService {
    
    /**
     * 获取用户的所有聊天会话
     */
    List<ChatSession> getChatSessionsByUserId(Long userId);
    
    /**
     * 获取聊天会话中的消息
     */
    List<ChatMessage> getChatMessages(Long chatId, Long userId);
    
    /**
     * 创建新的聊天会话
     */
    ChatSession createChatSession(Long userId);
    
    /**
     * 更新聊天会话信息
     */
    void updateChatSession(ChatSession chatSession);
    
    /**
     * 删除聊天会话
     */
    void deleteChatSession(Long chatId, Long userId);
    
    /**
     * 处理用户消息并获取AI回复
     */
    ChatMessage processAndReply(ChatMessage message);
}
