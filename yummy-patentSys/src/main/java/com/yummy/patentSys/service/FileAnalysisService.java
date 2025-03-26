package com.yummy.patentSys.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import com.yummy.patentSys.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * 文件分析服务
 */
@Service
public class FileAnalysisService {
    
    private static final Logger log = LoggerFactory.getLogger(FileAnalysisService.class);
    
    @Autowired
    private FileStorageService fileStorageService;

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;
    
    private static final int MAX_TEXT_LENGTH = 3000;
    
    /**
     * 解析上传的文件
     */
    public String analyzeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "无法处理空文件";
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "文件名无效";
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        
        try {
            // 保存到临时文件
            File tempFile = createTempFile(file, extension);
            
            // 根据文件类型解析
            String content = extractFileContent(tempFile, extension);
            
            // 删除临时文件
            tempFile.delete();
            
            return content;
        } catch (Exception e) {
            log.error("解析文件时出错", e);
            return "解析文件时出错: " + e.getMessage();
        }
    }
    
    /**
     * 解析已保存的文件
     */
    public String analyzeUploadedFile(String fileName) {
        try {
            // 从FileStorageService获取正确的文件路径
            String filePath = fileStorageService.getAbsoluteUploadPath() + File.separator + fileName;
            File file = new File(filePath);
            
            if (!file.exists() || !file.isFile()) {
                return "文件不存在: " + fileName;
            }
            
            String extension = getFileExtension(fileName).toLowerCase();
            return extractFileContent(file, extension);
            
        } catch (Exception e) {
            log.error("解析文件时出错", e);
            return "解析文件时出错: " + e.getMessage();
        }
    }
    
    /**
     * 创建临时文件
     */
    private File createTempFile(MultipartFile file, String extension) throws IOException {
        // 确保目录存在
        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // 创建临时文件
        File tempFile = File.createTempFile("upload_", "." + extension, dir);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        
        return tempFile;
    }
    
    /**
     * 提取文件内容
     */
    private String extractFileContent(File file, String extension) throws IOException {
        log.info("开始提取文件内容，类型: {}, 文件路径: {}", extension, file.getAbsolutePath());
        
        try {
            switch (extension) {
                case "pdf":
                    return extractPdfContent(file);
                case "doc":
                    return extractDocContent(file);
                case "docx":
                    return extractDocxContent(file);
                case "xls":
                    return extractXlsContent(file);
                case "xlsx":
                    return extractXlsxContent(file);
                case "txt":
                    return extractTxtContent(file);
                default:
                    return "不支持的文件类型: " + extension;
            }
        } catch (Exception e) {
            log.error("提取文件内容时出现未处理的异常", e);
            return "处理文件时出错: " + e.getMessage();
        }
    }
    
    /**
     * 提取PDF文件内容
     */
    private String extractPdfContent(File file) throws IOException {
        StringBuilder result = new StringBuilder();
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            result.append("PDF文档内容 (").append(document.getNumberOfPages()).append("页):\n\n");
            result.append(truncateText(text, MAX_TEXT_LENGTH));
        }
        return result.toString();
    }
    
    /**
     * 提取DOC文件内容
     */
    private String extractDocContent(File file) throws IOException {
        StringBuilder result = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             HWPFDocument document = new HWPFDocument(fis);
             WordExtractor extractor = new WordExtractor(document)) {
            
            String text = extractor.getText();
            result.append("Word文档内容:\n\n");
            result.append(truncateText(text, MAX_TEXT_LENGTH));
        }
        return result.toString();
    }
    
    /**
     * 提取DOCX文件内容
     */
    private String extractDocxContent(File file) throws IOException {
        StringBuilder result = new StringBuilder();
        
        try (FileInputStream fis = new FileInputStream(file)) {
            log.info("开始解析DOCX文件: {}", file.getName());
            
            // 添加更多日志来追踪过程
            log.info("文件大小: {} bytes", file.length());
            
            try {
                XWPFDocument document = new XWPFDocument(fis);
                try (XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                    String text = extractor.getText();
                    result.append("Word文档内容:\n\n");
                    result.append(truncateText(text, MAX_TEXT_LENGTH));
                }
            } catch (NoClassDefFoundError e) {
                log.error("解析DOCX文件时遇到错误", e);
                result.append("无法解析文档内容: ").append(e.getMessage());
                result.append("\n可能是缺少必要的依赖项，请检查项目配置。");
            } catch (Exception e) {
                log.error("解析DOCX文件时遇到错误", e);
                result.append("无法解析文档内容: ").append(e.getMessage());
            }
        }
        
        return result.toString();
    }
    
    /**
     * 提取XLS文件内容
     */
    private String extractXlsContent(File file) throws IOException {
        StringBuilder result = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new HSSFWorkbook(fis)) {
            
            result.append("Excel电子表格内容 (").append(workbook.getNumberOfSheets()).append("个工作表):\n\n");
            
            for (int i = 0; i < Math.min(3, workbook.getNumberOfSheets()); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                result.append("工作表: ").append(sheet.getSheetName()).append("\n");
                
                extractSheetContent(sheet, result);
                result.append("\n");
            }
        }
        return result.toString();
    }
    
    /**
     * 提取XLSX文件内容
     */
    private String extractXlsxContent(File file) throws IOException {
        StringBuilder result = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            result.append("Excel电子表格内容 (").append(workbook.getNumberOfSheets()).append("个工作表):\n\n");
            
            for (int i = 0; i < Math.min(3, workbook.getNumberOfSheets()); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                result.append("工作表: ").append(sheet.getSheetName()).append("\n");
                
                extractSheetContent(sheet, result);
                result.append("\n");
            }
        }
        return result.toString();
    }
    
    /**
     * 提取Excel工作表内容
     */
    private void extractSheetContent(Sheet sheet, StringBuilder result) {
        int rows = 0;
        Iterator<Row> rowIterator = sheet.iterator();
        
        while (rowIterator.hasNext() && rows < 15) {
            Row row = rowIterator.next();
            rows++;
            
            for (int i = 0; i < 10 && i < row.getLastCellNum(); i++) {
                Cell cell = row.getCell(i);
                result.append(getCellValue(cell)).append("\t");
            }
            
            if (row.getLastCellNum() > 10) {
                result.append("...");
            }
            
            result.append("\n");
        }
        
        if (sheet.getLastRowNum() > 15) {
            result.append("...(更多行省略)...\n");
        }
    }
    
    /**
     * 获取单元格值
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        CellType cellType = cell.getCellType();
        if (cellType == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cellType == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toString();
            } else {
                return String.valueOf(cell.getNumericCellValue());
            }
        } else if (cellType == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cellType == CellType.FORMULA) {
            return cell.getCellFormula();
        } else {
            return "";
        }
    }
    
    /**
     * 提取TXT文件内容
     */
    private String extractTxtContent(File file) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append("文本文件内容:\n\n");
        
        // 读取文本文件
        String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        result.append(truncateText(content, MAX_TEXT_LENGTH));
        
        return result.toString();
    }
    
    /**
     * 截断过长的文本
     */
    private String truncateText(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...\n\n[内容过长，已截断]";
    }
    
    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
}
