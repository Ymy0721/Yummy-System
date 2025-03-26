package com.yummy.patentSys.service.tool.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yummy.patentSys.service.WebSearchService;
import com.yummy.patentSys.service.tool.IToolCallService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络搜索工具服务实现
 */
@Service
public class WebSearchToolService implements IToolCallService {
    
    private static final Logger log = LoggerFactory.getLogger(WebSearchToolService.class);
    
    @Autowired
    private WebSearchService webSearchService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public String getToolName() {
        return "web_search";
    }
    
    @Override
    public String getToolDescription() {
        return "搜索互联网获取最新信息";
    }
    
    @Override
    public Map<String, Object> getParametersDefinition() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> query = new HashMap<>();
        query.put("type", "string");
        query.put("description", "搜索查询词");
        properties.put("query", query);
        
        parameters.put("properties", properties);
        
        List<String> required = new ArrayList<>();
        required.add("query");
        parameters.put("required", required);
        
        return parameters;
    }
    
    @Override
    public String execute(String arguments, Map<String, Object> context) {
        try {
            Map<String, Object> args = objectMapper.readValue(arguments, Map.class);
            String query = (String) args.get("query");
            
            if (query == null || query.trim().isEmpty()) {
                return "搜索查询不能为空";
            }
            
            return webSearchService.search(query);
        } catch (Exception e) {
            log.error("执行网络搜索时出错: {}", e.getMessage(), e);
            return "网络搜索失败: " + e.getMessage();
        }
    }
}
