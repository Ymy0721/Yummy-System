package com.yummy.controller;

import com.yummy.common.core.controller.BaseController;
import com.yummy.common.core.domain.AjaxResult;
import com.yummy.patentSys.domain.AiModelConfig;
import com.yummy.patentSys.service.IAiModelConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * API测试控制器 - 仅用于开发调试
 */
@RestController
@RequestMapping("/dev/apitest")
public class ApiTestController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(ApiTestController.class);
    
    @Autowired
    private IAiModelConfigService aiModelConfigService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @GetMapping("/testapi")
    public AjaxResult testApiConnection() {
        AiModelConfig modelConfig = aiModelConfigService.getDefaultModelConfig();
        if (modelConfig == null) {
            return AjaxResult.error("未找到有效的AI模型配置");
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
            
            // 构建简单消息测试请求
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelConfig.getModel());
            requestBody.put("stream", false);
            requestBody.put("max_tokens", modelConfig.getMaxTokens() != null ? modelConfig.getMaxTokens() : 100);
            requestBody.put("temperature", modelConfig.getTemperature() != null ? modelConfig.getTemperature() : 0.7);
            
            List<Map<String, String>> messages = new ArrayList<>();
            
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", "You are a helpful assistant.");
            messages.add(systemMsg);
            
            Map<String, String> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", "Hello, this is a test message.");
            messages.add(userMsg);
            
            requestBody.put("messages", messages);
            
            // 确保API URL格式正确
            String apiUrl = modelConfig.getBaseUrl();
            if (!apiUrl.endsWith("/v1/chat/completions")) {
                if (!apiUrl.endsWith("/")) {
                    apiUrl += "/";
                }
                apiUrl += "v1/chat/completions";
            }
            
            log.info("测试API连接: {}", apiUrl);
            log.info("请求头: {}", headers);
            log.info("请求体: {}", requestBody);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // 发送请求并获取完整响应
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    String.class);
            
            // 返回详细信息用于调试
            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", response.getStatusCodeValue());
            result.put("headers", response.getHeaders());
            result.put("body", response.getBody());
            
            return AjaxResult.success("API测试成功", result);
        } catch (Exception e) {
            log.error("API测试失败: {}", e.getMessage(), e);
            return AjaxResult.error("API测试失败: " + e.getMessage());
        }
    }
}
