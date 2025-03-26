package com.yummy.patentSys.service;

import com.yummy.patentSys.domain.ChatMessage;

import java.util.List;
import java.util.Map;

/**
 * AI通信服务接口
 */
public interface IAiCommunicationService {
    
    /**
     * 调用OpenAI兼容API获取AI回复，支持函数调用
     * @param contextMessages 上下文消息列表
     * @param systemPrompt 系统提示消息
     * @param requiredTools 需要使用的工具列表
     * @return 响应结果
     */
    Map<String, Object> callAiApiWithFunctions(List<ChatMessage> contextMessages, String systemPrompt, List<String> requiredTools);
    
    /**
     * 调用OpenAI兼容API获取AI回复
     * @param contextMessages 上下文消息列表
     * @return 返回的文本内容
     */
    String callAiApi(List<ChatMessage> contextMessages);
}
