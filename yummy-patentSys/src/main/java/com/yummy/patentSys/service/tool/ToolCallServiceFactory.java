package com.yummy.patentSys.service.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具调用服务工厂
 */
@Component
public class ToolCallServiceFactory {
    
    private static final Logger log = LoggerFactory.getLogger(ToolCallServiceFactory.class);
    
    @Autowired
    private List<IToolCallService> toolServices;
    
    private final Map<String, IToolCallService> toolServiceMap = new HashMap<>();
    
    @PostConstruct
    public void init() {
        for (IToolCallService service : toolServices) {
            toolServiceMap.put(service.getToolName(), service);
            log.info("注册工具服务: {}", service.getToolName());
        }
    }
    
    /**
     * 获取工具服务
     * @param toolName 工具名称
     * @return 工具服务
     */
    public IToolCallService getToolService(String toolName) {
        return toolServiceMap.get(toolName);
    }
    
    /**
     * 获取所有工具定义
     * @return 工具定义列表
     */
    public List<Map<String, Object>> getAllToolDefinitions() {
        List<Map<String, Object>> tools = new ArrayList<>();
        for (IToolCallService service : toolServices) {
            tools.add(service.getToolDefinition());
        }
        return tools;
    }
    
    /**
     * 获取指定的工具定义
     * @param toolNames 工具名称列表
     * @return 工具定义列表
     */
    public List<Map<String, Object>> getToolDefinitions(List<String> toolNames) {
        List<Map<String, Object>> tools = new ArrayList<>();
        for (String toolName : toolNames) {
            IToolCallService service = toolServiceMap.get(toolName);
            if (service != null) {
                tools.add(service.getToolDefinition());
            }
        }
        return tools;
    }
    
    /**
     * 检测消息中是否需要使用特定工具
     * @param messageContent 消息内容
     * @return 需要使用的工具名称列表
     */
    public List<String> detectRequiredTools(String messageContent) {
        List<String> requiredTools = new ArrayList<>();
        
        // 只有在消息内容明确指示需要特定工具时才添加
        if (messageContent != null) {
            // 检测是否需要文档分析 - 只有消息中包含文件标记时
            if (messageContent.contains("[文件:") && messageContent.contains("fileId:")) {
                requiredTools.add("analyze_document");
                log.info("检测到需要文档分析工具");
            }
            
            // 检测是否需要网络搜索 - 只有明确包含搜索标记时
            if (messageContent.contains("[请使用网络搜索获取最新信息]")) {
                requiredTools.add("web_search");
                log.info("检测到需要网络搜索工具");
            }
        }
        
        return requiredTools;
    }
}
