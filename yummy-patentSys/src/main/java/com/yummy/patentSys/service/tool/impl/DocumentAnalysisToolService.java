package com.yummy.patentSys.service.tool.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yummy.patentSys.service.FileAnalysisService;
import com.yummy.patentSys.service.FileStorageService;
import com.yummy.patentSys.service.tool.IToolCallService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 文档分析工具服务实现
 */
@Service
public class DocumentAnalysisToolService implements IToolCallService {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentAnalysisToolService.class);
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private FileAnalysisService fileAnalysisService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public String getToolName() {
        return "analyze_document";
    }
    
    @Override
    public String getToolDescription() {
        return "分析上传的文档并提取内容";
    }
    
    @Override
    public Map<String, Object> getParametersDefinition() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        parameters.put("properties", new HashMap<>());
        parameters.put("required", new ArrayList<>());
        return parameters;
    }
    
    @Override
    public String execute(String arguments, Map<String, Object> context) {
        try {
            // 从上下文中获取消息内容
            String messageContent = (String) context.get("messageContent");
            if (messageContent == null) {
                return "未找到消息内容，无法分析文档";
            }
            
            // 从消息中提取文件ID
            String fileId = extractFileIdFromMessage(messageContent);
            if (fileId == null) {
                return "未找到有效的文件ID";
            }
            
            // 获取文件名
            String fileName = fileStorageService.getFileName(fileId);
            if (fileName == null) {
                return "找不到指定的文件";
            }
            
            // 分析文件内容
            return fileAnalysisService.analyzeUploadedFile(fileName);
        } catch (Exception e) {
            log.error("执行文档分析时出错: {}", e.getMessage(), e);
            return "文档分析失败: " + e.getMessage();
        }
    }
    
    /**
     * 从消息中提取文件ID
     */
    private String extractFileIdFromMessage(String content) {
        int fileStart = content.indexOf("[文件:");
        if (fileStart != -1) {
            int fileEnd = content.indexOf("]", fileStart);
            if (fileEnd != -1) {
                String fileInfo = content.substring(fileStart + 4, fileEnd).trim();
                
                if (fileInfo.contains("fileId:")) {
                    return fileInfo.substring(fileInfo.indexOf("fileId:") + 7).trim();
                }
            }
        }
        return null;
    }
}
