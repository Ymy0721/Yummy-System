package com.yummy.controller;

import com.yummy.common.core.controller.BaseController;
import com.yummy.common.core.domain.AjaxResult;
import com.yummy.patentSys.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/patentSys/chat")
public class FileUploadController extends BaseController {
    
    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * 上传聊天文件
     */
    @PostMapping("/upload")
    public AjaxResult uploadFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return AjaxResult.error("请选择文件");
            }
            
            // 检查文件大小
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB限制
                return AjaxResult.error("文件大小不能超过10MB");
            }
            
            // 检查文件类型
            String extension = fileStorageService.getFileExtension(file.getOriginalFilename());
            if (!fileStorageService.isAllowedExtension(extension)) {
                return AjaxResult.error("不支持的文件类型");
            }
            
            // 保存文件
            Map<String, String> fileInfo = fileStorageService.saveFile(file);
            
            return AjaxResult.success("文件上传成功", fileInfo);
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return AjaxResult.error("文件上传失败: " + e.getMessage());
        }
    }
}
