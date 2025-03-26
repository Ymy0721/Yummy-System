package com.yummy.patentSys.service.impl;

import com.yummy.common.exception.ServiceException;
import com.yummy.patentSys.domain.AiModelConfig;
import com.yummy.patentSys.domain.ChatMessage;
import com.yummy.patentSys.service.IAiCommunicationService;
import com.yummy.patentSys.service.IAiModelConfigService;
import com.yummy.patentSys.service.tool.ToolCallServiceFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.util.*;
import java.util.function.Consumer;

/**
 * AI通信服务实现
 */
@Service
public class AiCommunicationServiceImpl implements IAiCommunicationService {
    
    private static final Logger log = LoggerFactory.getLogger(AiCommunicationServiceImpl.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private IAiModelConfigService aiModelConfigService;
    
    @Autowired
    private ToolCallServiceFactory toolCallServiceFactory;
    
    /**
     * 调用OpenAI兼容API获取AI回复，支持函数调用
     */
    @Override
    public Map<String, Object> callAiApiWithFunctions(List<ChatMessage> contextMessages, String systemPrompt, List<String> requiredTools) {
        // 获取默认模型配置
        AiModelConfig modelConfig = aiModelConfigService.getDefaultModelConfig();
        
        if (modelConfig == null) {
            throw new ServiceException("未找到有效的AI模型配置，请先配置模型");
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 设置Authorization头部
            String apiKey = modelConfig.getApiKey();
            if (!apiKey.startsWith("Bearer ")) {
                headers.set("Authorization", "Bearer " + apiKey);
            } else {
                headers.set("Authorization", apiKey);
            }
            
            // 构建API请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelConfig.getModel());
            requestBody.put("stream", false);
            
            // 设置参数
            if (modelConfig.getMaxTokens() != null) {
                requestBody.put("max_tokens", modelConfig.getMaxTokens());
            } else {
                requestBody.put("max_tokens", 2000);
            }
            
            if (modelConfig.getTemperature() != null) {
                requestBody.put("temperature", modelConfig.getTemperature());
            } else {
                requestBody.put("temperature", 0.7);
            }
            
            // 加入消息上下文
            List<Map<String, Object>> messages = new ArrayList<>();
            
            // 添加系统消息
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
            
            // 逆序处理上下文消息
            Collections.reverse(contextMessages);
            
            // 添加历史消息，包括函数调用结果
            for (ChatMessage msg : contextMessages) {
                Map<String, Object> messageMap = new HashMap<>();
                
                // 适配角色名称 - 将function角色转换为tool角色
                String role = msg.getRole();
                if ("function".equals(role)) {
                    role = "tool"; // 将function替换为tool以兼容DeepSeek API
                    log.info("将角色function转换为tool以兼容API");
                }
                messageMap.put("role", role);
                
                // 设置消息内容
                if (msg.getContent() != null) {
                    messageMap.put("content", msg.getContent());
                } else {
                    messageMap.put("content", "");
                }
                
                // 工具角色需要设置函数名称
                if ("tool".equals(role)) {
                    // 根据消息内容判断是哪个函数
                    if (msg.getContent() != null && msg.getContent().startsWith("PDF文档内容") || 
                        msg.getContent() != null && msg.getContent().startsWith("Word文档内容") ||
                        msg.getContent() != null && msg.getContent().startsWith("Excel电子表格内容") ||
                        msg.getContent() != null && msg.getContent().startsWith("文本文件内容")) {
                        messageMap.put("name", "analyze_document");
                    } else {
                        // 默认为网络搜索
                        messageMap.put("name", "web_search");
                    }
                }
                
                // 设置函数调用（如果有）
                if (msg.getFunctionCall() != null) {
                    messageMap.put("function_call", msg.getFunctionCall());
                }
                
                messages.add(messageMap);
            }
            
            requestBody.put("messages", messages);
            
            // 添加工具定义 - 确保只有在明确需要工具时才添加
            if (requiredTools != null && !requiredTools.isEmpty()) {
                List<Map<String, Object>> tools = toolCallServiceFactory.getToolDefinitions(requiredTools);
                if (tools != null && !tools.isEmpty()) {
                    requestBody.put("tools", tools);
                    log.info("添加工具到请求: {}", requiredTools);
                }
            }
            
            // 确保API URL格式正确
            String apiUrl = modelConfig.getBaseUrl();
            if (!apiUrl.endsWith("/v1/chat/completions")) {
                if (!apiUrl.endsWith("/")) {
                    apiUrl += "/";
                }
                apiUrl += "v1/chat/completions";
            }
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            log.info("调用AI API - URL: {}, 消息数: {}, 工具数: {}", apiUrl, messages.size(), requiredTools.size());
            
            // 发送请求到AI API
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            
            // 解析回复 - 确保安全处理可能为null的情况
            Map<String, Object> result = new HashMap<>();
            if (response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    
                    if (message != null) {
                        // 提取内容
                        if (message.containsKey("content") && message.get("content") != null) {
                            result.put("content", message.get("content"));
                        } else {
                            result.put("content", "");
                        }
                        
                        // 提取函数调用
                        if (message.containsKey("function_call") && message.get("function_call") != null) {
                            result.put("function_call", message.get("function_call"));
                        }
                        
                        // 提取工具调用 - 改用tool_calls而不是function_call
                        if (message.containsKey("tool_calls")) {
                            Object toolCallsObj = message.get("tool_calls");
                            if (toolCallsObj != null && toolCallsObj instanceof List) {
                                List<Map<String, Object>> toolCalls = (List<Map<String, Object>>) toolCallsObj;
                                if (toolCalls != null && !toolCalls.isEmpty()) {
                                    Map<String, Object> toolCall = toolCalls.get(0);
                                    if (toolCall != null && toolCall.containsKey("function")) {
                                        Map<String, Object> function = (Map<String, Object>) toolCall.get("function");
                                        if (function != null) {
                                            Map<String, Object> functionCall = new HashMap<>();
                                            functionCall.put("name", function.get("name"));
                                            functionCall.put("arguments", function.get("arguments"));
                                            
                                            result.put("function_call", functionCall);
                                            log.info("从tool_calls中提取了function_call信息");
                                        }
                                    }
                                }
                            }
                        }
                        
                        return result;
                    }
                }
            }
            
            result.put("content", "AI助手暂时无法回答您的问题，请稍后再试。");
            return result;
        } catch (Exception e) {
            log.error("调用AI API出错: {}", e.getMessage(), e);
            
            Map<String, Object> result = new HashMap<>();
            result.put("content", "与AI服务通信时发生错误: " + e.getMessage());
            return result;
        }
    }
    
    /**
     * 调用OpenAI兼容API获取AI回复
     */
    @Override
    public String callAiApi(List<ChatMessage> contextMessages) {
        // 获取默认模型配置
        AiModelConfig modelConfig = aiModelConfigService.getDefaultModelConfig();
        
        if (modelConfig == null) {
            throw new ServiceException("未找到有效的AI模型配置，请先配置模型");
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 设置Authorization头部 - 以Bearer开头
            String apiKey = modelConfig.getApiKey();
            if (!apiKey.startsWith("Bearer ")) {
                headers.set("Authorization", "Bearer " + apiKey);
                log.info("使用Bearer Token认证");
            } else {
                headers.set("Authorization", apiKey);
                log.info("使用完整Authorization头");
            }
            
            // 构建API请求体 - 按照OpenAI格式
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelConfig.getModel());
            requestBody.put("stream", false); // 不使用流式响应
            
            // 设置参数
            if (modelConfig.getMaxTokens() != null) {
                requestBody.put("max_tokens", modelConfig.getMaxTokens());
            } else {
                requestBody.put("max_tokens", 2000); // 默认值
            }
            
            if (modelConfig.getTemperature() != null) {
                requestBody.put("temperature", modelConfig.getTemperature());
            } else {
                requestBody.put("temperature", 0.7); // 默认值
            }
            
            // 加入消息上下文
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加一个系统消息
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "你是一个助手，请简洁、准确地回答问题。");
            messages.add(systemMsg);
            
            // 逆序处理上下文消息，因为selectRecentChatMessages返回的是按时间降序排列的
            Collections.reverse(contextMessages);
            
            // 添加历史消息
            for (ChatMessage msg : contextMessages) {
                Map<String, String> messageMap = new HashMap<>();
                messageMap.put("role", msg.getRole());
                messageMap.put("content", msg.getContent());
                messages.add(messageMap);
            }
            
            requestBody.put("messages", messages);
            
            // 确保API URL格式正确
            String apiUrl = modelConfig.getBaseUrl();
            if (!apiUrl.endsWith("/v1/chat/completions")) {
                if (!apiUrl.endsWith("/")) {
                    apiUrl += "/";
                }
                apiUrl += "v1/chat/completions";
                log.info("调整API URL为标准路径: {}", apiUrl);
            }
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            log.info("调用AI API - URL: {}, 消息数: {}", apiUrl, messages.size());
            
            // 发送请求到AI API
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            
            log.info("API响应状态: {}", response.getStatusCode());
            
            // 解析回复
            if (response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                log.info("API响应体结构: {}", responseBody.keySet());
                
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, String> message = (Map<String, String>) firstChoice.get("message");
                    if (message != null) {
                        return message.get("content");
                    }
                }
            }
            
            return "AI助手暂时无法回答您的问题，请稍后再试。";
        } catch (Exception e) {
            log.error("调用AI API出错: {}", e.getMessage(), e);
            
            if (e.getCause() instanceof SocketTimeoutException) {
                return "AI服务响应超时，这可能是因为当前查询较为复杂或服务负载较高。您可以尝试简化问题或稍后再试。";
            }
            
            return "与AI服务通信时发生错误: " + e.getMessage();
        }
    }
}