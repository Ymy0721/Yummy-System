package com.yummy.patentSys.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 网络搜索服务 - 使用Bing Search API
 */
@Service
public class WebSearchService {
    
    private static final Logger log = LoggerFactory.getLogger(WebSearchService.class);
    
    @Value("${search.api.key:}")
    private String apiKey;
    
    @Value("${search.api.endpoint:https://api.bing.microsoft.com/v7.0/search}")
    private String apiEndpoint;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 执行网络搜索
     * @param query 搜索查询词
     * @return 搜索结果
     */
    public String search(String query) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("搜索API密钥未配置，使用备用搜索方法");
            return fallbackSearch(query);
        }
        
        try {
            // 构建请求URL
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            String url = apiEndpoint + "?q=" + encodedQuery + "&count=5&mkt=zh-CN";
            
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Ocp-Apim-Subscription-Key", apiKey);
            headers.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
            
            // 发送请求
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                String.class
            );
            
            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("搜索API请求失败: {}", response.getStatusCodeValue());
                return "搜索请求失败，HTTP状态码: " + response.getStatusCodeValue();
            }
            
            // 处理响应
            return formatBingSearchResults(response.getBody());
            
        } catch (Exception e) {
            log.error("执行搜索请求时出错", e);
            return "执行网络搜索时出错: " + e.getMessage();
        }
    }
    
    /**
     * 格式化Bing搜索结果
     */
    private String formatBingSearchResults(String jsonResponse) throws IOException {
        StringBuilder formatted = new StringBuilder();
        formatted.append("网络搜索结果:\n\n");
        
        JsonNode root = objectMapper.readTree(jsonResponse);
        
        // 处理网页结果
        if (root.has("webPages") && root.get("webPages").has("value")) {
            JsonNode webPages = root.get("webPages").get("value");
            for (int i = 0; i < webPages.size(); i++) {
                JsonNode page = webPages.get(i);
                formatted.append(i + 1).append(". ");
                formatted.append("标题: ").append(page.get("name").asText()).append("\n");
                formatted.append("   摘要: ").append(page.get("snippet").asText()).append("\n");
                formatted.append("   链接: ").append(page.get("url").asText()).append("\n\n");
            }
        }
        
        // 也可以添加新闻结果
        if (root.has("news") && root.get("news").has("value")) {
            JsonNode news = root.get("news").get("value");
            formatted.append("最新新闻:\n");
            for (int i = 0; i < news.size() && i < 3; i++) {
                JsonNode article = news.get(i);
                formatted.append("- ").append(article.get("name").asText());
                if (article.has("datePublished")) {
                    formatted.append(" (").append(article.get("datePublished").asText()).append(")");
                }
                formatted.append("\n");
            }
            formatted.append("\n");
        }
        
        return formatted.toString();
    }
    
    /**
     * 备用搜索方法 - 当API密钥未配置时使用
     */
    private String fallbackSearch(String query) {
        return "网络搜索功能当前不可用。请配置搜索API密钥。\n\n" +
               "您的搜索查询是: " + query + "\n\n" +
               "请联系管理员开通此功能。";
    }
}
