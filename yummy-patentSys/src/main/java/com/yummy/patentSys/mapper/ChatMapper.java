package com.yummy.patentSys.mapper;

import com.yummy.patentSys.domain.ChatSession;
import com.yummy.patentSys.domain.ChatMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI聊天数据访问接口
 */
public interface ChatMapper {
    
    /**
     * 查询用户的聊天会话列表
     */
    List<ChatSession> selectChatSessionsByUserId(Long userId);
    
    /**
     * 根据ID查询聊天会话
     */
    ChatSession selectChatSessionById(Long chatId);
    
    /**
     * 新增聊天会话
     */
    int insertChatSession(ChatSession chatSession);
    
    /**
     * 更新聊天会话
     */
    int updateChatSession(ChatSession chatSession);
    
    /**
     * 删除聊天会话
     */
    int deleteChatSessionById(Long chatId);
    
    /**
     * 查询会话的所有消息
     */
    List<ChatMessage> selectChatMessagesByChatId(Long chatId);
    
    /**
     * 查询会话的最近消息（用于上下文）
     */
    List<ChatMessage> selectRecentChatMessages(@Param("chatId") Long chatId, @Param("limit") int limit);
    
    /**
     * 新增聊天消息
     */
    int insertChatMessage(ChatMessage chatMessage);
    
    /**
     * 删除会话相关的所有消息
     */
    int deleteChatMessagesByChatId(Long chatId);
}
