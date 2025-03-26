-- ----------------------------
-- 聊天会话表
-- ----------------------------
DROP TABLE IF EXISTS `chat_session`;
CREATE TABLE `chat_session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `title` varchar(255) DEFAULT NULL COMMENT '会话标题',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_update_time` (`update_time`),
  CONSTRAINT `fk_chat_session_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='AI聊天会话表';

-- ----------------------------
-- 聊天消息表
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `chat_id` bigint(20) NOT NULL COMMENT '会话ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role` varchar(20) NOT NULL COMMENT '角色(user/assistant)',
  `content` text NOT NULL COMMENT '消息内容',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_chat_id` (`chat_id`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_chat_message_session` FOREIGN KEY (`chat_id`) REFERENCES `chat_session` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_chat_message_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 COMMENT='AI聊天消息表';
