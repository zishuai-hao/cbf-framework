-- 用户表
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像',
  `status` int(1) DEFAULT '1' COMMENT '状态（0：禁用，1：启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int(1) DEFAULT '0' COMMENT '逻辑删除标记（0：未删除，1：已删除）',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_phone` (`phone`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入测试数据
INSERT INTO `sys_user` (`id`, `username`, `password`, `email`, `phone`, `nickname`, `avatar`, `status`, `create_time`, `update_time`, `deleted`, `create_by`, `update_by`) VALUES
(1, 'admin', '$2a$10$7JB720yubVSOfvVWzKz8UeKz8UeKz8UeKz8UeKz8UeKz8UeKz8UeK', 'admin@company.com', '13800138000', '管理员', 'https://example.com/avatar/admin.jpg', 1, NOW(), NOW(), 0, 1, 1),
(2, 'user1', '$2a$10$7JB720yubVSOfvVWzKz8UeKz8UeKz8UeKz8UeKz8UeKz8UeKz8UeK', 'user1@company.com', '13800138001', '用户1', 'https://example.com/avatar/user1.jpg', 1, NOW(), NOW(), 0, 1, 1),
(3, 'user2', '$2a$10$7JB720yubVSOfvVWzKz8UeKz8UeKz8UeKz8UeKz8UeKz8UeKz8UeK', 'user2@company.com', '13800138002', '用户2', 'https://example.com/avatar/user2.jpg', 1, NOW(), NOW(), 0, 1, 1);
