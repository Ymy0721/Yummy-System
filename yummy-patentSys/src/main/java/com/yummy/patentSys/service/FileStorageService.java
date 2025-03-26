package com.yummy.patentSys.service;

import com.yummy.common.utils.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件存储服务
 */
@Service
public class FileStorageService {
    
    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    
    @Value("${file.upload.path:}")
    private String uploadPath;
    
    // 文件映射: fileId -> fileName
    private static final Map<String, String> UPLOADED_FILES = new HashMap<>();
    
    /**
     * 保存上传的文件
     * @param file 上传的文件
     * @return 文件ID和文件名
     * @throws Exception 上传失败时抛出异常
     */
    public Map<String, String> saveFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("无效的文件名");
        }
        
        // 获取文件扩展名
        String extension = getFileExtension(originalFilename);
        
        // 生成唯一文件ID
        String fileId = UUID.fastUUID().toString(true);
        
        // 确保使用绝对路径
        String absoluteUploadPath = getAbsoluteUploadPath();
        log.info("文件将保存到绝对路径: {}", absoluteUploadPath);
        
        // 创建所有必要的目录
        Path uploadDir = Paths.get(absoluteUploadPath);
        Files.createDirectories(uploadDir);
        
        // 保存文件
        String fileName = fileId + "." + extension;
        Path filePath = uploadDir.resolve(fileName);
        log.info("完整文件路径: {}", filePath.toAbsolutePath());
        file.transferTo(filePath.toFile());
        
        // 保存文件映射
        UPLOADED_FILES.put(fileId, fileName);
        
        // 返回结果
        Map<String, String> result = new HashMap<>();
        result.put("fileId", fileId);
        result.put("fileName", fileName);
        result.put("originalFileName", originalFilename);
        
        return result;
    }
    
    /**
     * 获取绝对上传路径
     * @return 绝对路径字符串
     */
    public String getAbsoluteUploadPath() {
        // 如果配置的是绝对路径，则直接使用
        if (uploadPath != null && !uploadPath.isEmpty() && 
            (uploadPath.startsWith("/") || uploadPath.contains(":"))) {
            return uploadPath;
        }
        
        // 否则在当前用户目录下创建uploads目录
        String userHome = System.getProperty("user.home");
        return userHome + File.separator + "yummy-uploads";
    }
    
    /**
     * 根据文件ID获取文件名
     * @param fileId 文件ID
     * @return 文件名，不存在时返回null
     */
    public String getFileName(String fileId) {
        return UPLOADED_FILES.get(fileId);
    }
    
    /**
     * 获取文件的完整路径
     * @param fileId 文件ID
     * @return 完整文件路径
     */
    public String getFilePath(String fileId) {
        String fileName = getFileName(fileId);
        if (fileName == null) {
            return null;
        }
        return getAbsoluteUploadPath() + File.separator + fileName;
    }
    
    /**
     * 检查扩展名是否允许
     * @param extension 文件扩展名
     * @return 是否允许
     */
    public boolean isAllowedExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return false;
        }
        
        String[] allowedTypes = {"pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"};
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名
     */
    public String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1).toLowerCase();
    }
}
