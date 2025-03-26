package com.yummy.patentSys.service.impl;

import com.yummy.common.exception.ServiceException;
import com.yummy.common.utils.DateUtils;
import com.yummy.patentSys.domain.ChatSession;
import com.yummy.patentSys.mapper.ChatMapper;
import com.yummy.patentSys.service.IChatSessionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 聊天会话管理服务实现
 */
@Service
public class ChatSessionServiceImpl implements IChatSessionService {
    
    private static final Logger log = LoggerFactory.getLogger(ChatSessionServiceImpl.class);
    
    @Autowired
    private ChatMapper chatMapper;
    
    /**
     * 获取用户的所有聊天会话
     */
    @Override
    public List<ChatSession> getChatSessionsByUserId(Long userId) {
        return chatMapper.selectChatSessionsByUserId(userId);
    }
    
    /**
     * 获取指定ID的聊天会话
     */
    @Override
    public ChatSession getChatSessionById(Long chatId, Long userId) {
        ChatSession session = chatMapper.selectChatSessionById(chatId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new ServiceException("无权访问该聊天会话");
        }
        return session;
    }
    
    /**
     * 创建新的聊天会话
     */
    @Override
    @Transactional
    public ChatSession createChatSession(Long userId) {
        ChatSession session = new ChatSession();
        session.setUserId(userId);
        session.setCreateTime(DateUtils.getNowDate());
        session.setUpdateTime(DateUtils.getNowDate());
        
        chatMapper.insertChatSession(session);
        return session;
    }
    
    /**
     * 更新聊天会话信息
     */
    @Override
    public void updateChatSession(ChatSession chatSession) {
        // 验证会话归属
        ChatSession session = chatMapper.selectChatSessionById(chatSession.getId());
        if (session == null || !session.getUserId().equals(chatSession.getUserId())) {
            throw new ServiceException("无权修改该聊天会话");
        }
        
        chatSession.setUpdateTime(DateUtils.getNowDate());
        chatMapper.updateChatSession(chatSession);
    }
    
    /**
     * 删除聊天会话
     */
    @Override
    @Transactional
    public void deleteChatSession(Long chatId, Long userId) {
        // 验证会话归属
        ChatSession session = chatMapper.selectChatSessionById(chatId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new ServiceException("无权删除该聊天会话");
        }
        
        // 删除会话及相关消息
        chatMapper.deleteChatMessagesByChatId(chatId);
        chatMapper.deleteChatSessionById(chatId);
    }
    
    /**
     * 更新会话最后活动时间
     */
    @Override
    public void updateSessionLastActivity(Long chatId) {
        ChatSession session = chatMapper.selectChatSessionById(chatId);
        if (session != null) {
            session.setUpdateTime(DateUtils.getNowDate());
            chatMapper.updateChatSession(session);
        }
    }
}
