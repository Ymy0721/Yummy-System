package com.yummy.patentSys.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

/**
 * 重试功能配置类
 */
@Configuration
@EnableRetry
public class RetryConfig {
    // 配置内容...
}
