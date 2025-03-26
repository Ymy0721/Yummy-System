-- ----------------------------
-- AI模型配置表
-- ----------------------------
DROP TABLE IF EXISTS `ai_model_config`;
CREATE TABLE `ai_model_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `name` varchar(100) NOT NULL COMMENT '模型名称',
  `model` varchar(100) NOT NULL COMMENT '模型代码',
  `base_url` varchar(255) NOT NULL COMMENT 'API base URL',
  `api_key` varchar(255) NOT NULL COMMENT 'API key',
  `max_tokens` int(11) DEFAULT '2000' COMMENT '最大token数量',
  `temperature` float DEFAULT '0.7' COMMENT '温度参数',
  `is_default` char(1) DEFAULT '0' COMMENT '是否为默认模型(0否 1是)',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_model_name` (`name`) 
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='AI模型配置表';

-- ----------------------------
-- 初始化默认模型配置数据
-- ----------------------------
INSERT INTO `ai_model_config` 
  (`name`, `model`, `base_url`, `api_key`, `max_tokens`, `temperature`, `is_default`, `status`, `remark`, `create_time`) 
VALUES 
  ('DeepSeek-R1', 'DeepSeek-R1', 'https://llmapi.paratera.com', 'sk-wwBgsZowEfofm5y2AU0SHQ', 4000, 0.7, '1', '0', 'DeepSeek专业模型', NOW()),
  ('OpenAI GPT-3.5', 'gpt-3.5-turbo', 'https://api.openai.com/v1/chat/completions', 'sk-your-openai-api-key', 2000, 0.7, '0', '0', 'OpenAI官方GPT-3.5模型', NOW());
