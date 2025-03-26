package com.yummy.patentSys.service.tool;

import java.util.Map;

/**
 * 工具调用服务接口
 */
public interface IToolCallService {
    
    /**
     * 获取工具名称
     * @return 工具名称
     */
    String getToolName();
    
    /**
     * 获取工具描述
     * @return 工具描述
     */
    String getToolDescription();
    
    /**
     * 获取工具参数定义
     * @return 参数定义的Map
     */
    Map<String, Object> getParametersDefinition();
    
    /**
     * 执行工具调用
     * @param arguments 调用参数
     * @param context 上下文信息
     * @return 执行结果
     */
    String execute(String arguments, Map<String, Object> context);
    
    /**
     * 返回工具定义信息
     * @return 工具定义
     */
    default Map<String, Object> getToolDefinition() {
        Map<String, Object> tool = new java.util.HashMap<>();
        tool.put("type", "function");
        
        Map<String, Object> function = new java.util.HashMap<>();
        function.put("name", getToolName());
        function.put("description", getToolDescription());
        function.put("parameters", getParametersDefinition());
        
        tool.put("function", function);
        return tool;
    }
}
