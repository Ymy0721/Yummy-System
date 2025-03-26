-- 更新默认模型配置，确保URL和认证格式正确
UPDATE `ai_model_config` 
SET `base_url` = 'https://llmapi.paratera.com/v1/chat/completions',
    `model` = 'DeepSeek-R1',
    `is_default` = '1'
WHERE `name` = 'DeepSeek-R1';

-- 如果没有DeepSeek模型配置，则插入
INSERT INTO `ai_model_config` 
    (`name`, `model`, `base_url`, `api_key`, `max_tokens`, `temperature`, `is_default`, `status`, `remark`, `create_time`)
SELECT 'DeepSeek-R1', 'DeepSeek-R1', 'https://llmapi.paratera.com/v1/chat/completions', 
       'sk-wwBgsZowEfofm5y2AU0SHQ', 4000, 0.7, '1', '0', 'DeepSeek专业模型', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `ai_model_config` WHERE `name` = 'DeepSeek-R1'
);
