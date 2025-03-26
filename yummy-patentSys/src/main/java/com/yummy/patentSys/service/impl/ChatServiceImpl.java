package com.yummy.patentSys.service.impl;

import com.yummy.common.exception.ServiceException;
import com.yummy.common.utils.DateUtils;
import com.yummy.patentSys.domain.ChatSession;
import com.yummy.patentSys.domain.ChatMessage;
import com.yummy.patentSys.mapper.ChatMapper;
import com.yummy.patentSys.service.*;
import com.yummy.patentSys.service.tool.IToolCallService;
import com.yummy.patentSys.service.tool.ToolCallServiceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;

/**
 * AI聊天服务实现
 */
@Service
public class ChatServiceImpl implements IChatService {
    
    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);
    
    @Autowired
    private ChatMapper chatMapper;
    
    @Autowired
    private ToolCallServiceFactory toolCallServiceFactory;
    
    @Autowired
    private IChatSessionService chatSessionService;
    
    @Autowired
    private IAiCommunicationService aiCommunicationService;
    
    /**
     * 获取用户的所有聊天会话
     */
    @Override
    public List<ChatSession> getChatSessionsByUserId(Long userId) {
        return chatSessionService.getChatSessionsByUserId(userId);
    }
    
    /**
     * 获取聊天会话中的消息
     */
    @Override
    public List<ChatMessage> getChatMessages(Long chatId, Long userId) {
        // 验证会话归属
        chatSessionService.getChatSessionById(chatId, userId);
        return chatMapper.selectChatMessagesByChatId(chatId);
    }
    
    /**
     * 创建新的聊天会话
     */
    @Override
    @Transactional
    public ChatSession createChatSession(Long userId) {
        return chatSessionService.createChatSession(userId);
    }
    
    /**
     * 更新聊天会话信息
     */
    @Override
    public void updateChatSession(ChatSession chatSession) {
        chatSessionService.updateChatSession(chatSession);
    }
    
    /**
     * 删除聊天会话
     */
    @Override
    @Transactional
    public void deleteChatSession(Long chatId, Long userId) {
        chatSessionService.deleteChatSession(chatId, userId);
    }
    
    /**
     * 处理用户消息并获取AI回复
     */
    @Override
    @Transactional
    public ChatMessage processAndReply(ChatMessage message) {
        // 验证会话归属
        ChatSession session = chatSessionService.getChatSessionById(message.getChatId(), message.getUserId());
        
        // 设置消息属性
        message.setRole("user");
        message.setCreateTime(DateUtils.getNowDate());
        
        // 保存用户消息
        chatMapper.insertChatMessage(message);
        
        // 获取上下文消息（最近10条）
        List<ChatMessage> contextMessages = chatMapper.selectRecentChatMessages(message.getChatId(), 10);

        // 构建系统提示消息
        String systemPrompt = "你是一个助手，请简洁、准确地回答问题。你可以使用工具来获取额外信息。";
        
        // 检测消息中需要使用的工具
        List<String> requiredTools = toolCallServiceFactory.detectRequiredTools(message.getContent());

        // 调用AI API获取回复（可能包含函数调用）
        String aiReply = "";
        try {
            boolean functionCallCompleted = false;
            int maxFunctionCalls = 3; // 防止无限循环
            int functionCallCount = 0;
            
            // 初始化响应消息
            Map<String, Object> responseData = null;
            
            while (!functionCallCompleted && functionCallCount < maxFunctionCalls) {
                functionCallCount++;
                
                // 调用API，并检查是否需要执行函数
                responseData = aiCommunicationService.callAiApiWithFunctions(contextMessages, systemPrompt, requiredTools);
                
                // 检查是否有函数调用 - 添加安全检查避免空指针异常
                if (responseData != null && responseData.containsKey("function_call") && responseData.get("function_call") != null) {
                    try {
                        Map<String, Object> functionCall = (Map<String, Object>) responseData.get("function_call");
                        if (functionCall != null && functionCall.containsKey("name") && functionCall.containsKey("arguments")) {
                            String functionName = (String) functionCall.get("name");
                            String functionArgs = (String) functionCall.get("arguments");
                            
                            // 执行工具调用
                            String functionResult = executeToolCall(functionName, functionArgs, message.getContent());
                            
                            // 将函数调用和结果添加到上下文中
                            ChatMessage functionCallMessage = new ChatMessage();
                            functionCallMessage.setRole("assistant");
                            functionCallMessage.setContent("");
                            functionCallMessage.setFunctionCall(functionCall);
                            
                            ChatMessage functionResultMessage = new ChatMessage();
                            functionResultMessage.setRole("tool");
                            functionResultMessage.setContent(functionResult);
                            
                            // 添加到上下文
                            contextMessages.add(0, functionCallMessage);
                            contextMessages.add(0, functionResultMessage);
                            
                            // 继续对话，将函数结果传递给模型
                            continue;
                        } else {
                            log.warn("函数调用数据结构不完整，跳过函数调用");
                            functionCallCompleted = true;
                            aiReply = (String) responseData.get("content");
                        }
                    } catch (Exception e) {
                        log.error("处理函数调用时出错", e);
                        functionCallCompleted = true;
                        aiReply = "在处理您的请求时遇到了问题，请稍后再试。";
                    }
                } else {
                    // 没有函数调用，对话结束
                    functionCallCompleted = true;
                    if (responseData != null && responseData.containsKey("content")) {
                        aiReply = (String) responseData.get("content");
                    } else {
                        aiReply = "抱歉，AI未能生成有效回复。";
                    }
                }
            }
            
            // 如果达到最大函数调用次数但未完成，使用最后一次响应
            if (!functionCallCompleted) {
                aiReply = "抱歉，处理您的请求时遇到了问题。请尝试重新表述您的问题。";
                if (responseData != null && responseData.containsKey("content")) {
                    aiReply = (String) responseData.get("content");
                }
            }
        } catch (Exception e) {
            log.error("调用AI API出错", e);
            aiReply = "与AI服务通信时发生错误，请稍后再试。错误详情: " + e.getMessage();
        }
        
        // 构建AI回复消息
        ChatMessage replyMessage = new ChatMessage();
        replyMessage.setChatId(message.getChatId());
        replyMessage.setUserId(message.getUserId());
        replyMessage.setRole("assistant");
        replyMessage.setContent(aiReply);
        replyMessage.setCreateTime(DateUtils.getNowDate());
        
        // 保存AI回复
        chatMapper.insertChatMessage(replyMessage);
        
        // 更新会话最后活动时间
        chatSessionService.updateSessionLastActivity(message.getChatId());
        
        return replyMessage;
    }
    
    /**
     * 执行工具调用
     */
    private String executeToolCall(String toolName, String arguments, String messageContent) {
        try {
            // 获取对应的工具服务
            IToolCallService toolService = toolCallServiceFactory.getToolService(toolName);
            if (toolService == null) {
                return "未找到工具: " + toolName;
            }
            
            // 准备上下文信息
            Map<String, Object> context = new HashMap<>();
            context.put("messageContent", messageContent);
            
            // 执行工具调用
            return toolService.execute(arguments, context);
        } catch (Exception e) {
            log.error("执行工具调用时出错: {}", e.getMessage(), e);
            return "执行工具调用时出错: " + e.getMessage();
        }
    }
}
