-- 认证 Demo 初始化脚本。
-- 当前仍然是 opaque token 方案，但账号、角色、权限和 token 状态都落到 MySQL。

CREATE DATABASE IF NOT EXISTS `study_demo`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `study_demo`;

CREATE TABLE IF NOT EXISTS `demo_auth_account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL,
  `password` varchar(100) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_demo_auth_account_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 角色表达一类身份分组，例如 ADMIN、USER。
CREATE TABLE IF NOT EXISTS `demo_auth_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) NOT NULL,
  `role_name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_demo_auth_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 权限码表达具体操作，例如 blog:delete。
CREATE TABLE IF NOT EXISTS `demo_auth_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `permission_code` varchar(128) NOT NULL,
  `permission_name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_demo_auth_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 账号和角色多对多关系。
CREATE TABLE IF NOT EXISTS `demo_auth_account_role` (
  `account_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`account_id`, `role_id`),
  KEY `idx_demo_auth_account_role_role_id` (`role_id`),
  CONSTRAINT `fk_demo_auth_account_role_account`
    FOREIGN KEY (`account_id`) REFERENCES `demo_auth_account` (`id`),
  CONSTRAINT `fk_demo_auth_account_role_role`
    FOREIGN KEY (`role_id`) REFERENCES `demo_auth_role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 角色和权限码多对多关系。
CREATE TABLE IF NOT EXISTS `demo_auth_role_permission` (
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`, `permission_id`),
  KEY `idx_demo_auth_role_permission_permission_id` (`permission_id`),
  CONSTRAINT `fk_demo_auth_role_permission_role`
    FOREIGN KEY (`role_id`) REFERENCES `demo_auth_role` (`id`),
  CONSTRAINT `fk_demo_auth_role_permission_permission`
    FOREIGN KEY (`permission_id`) REFERENCES `demo_auth_permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- opaque token 只有随机串，真正的登录主体需要回表恢复。
CREATE TABLE IF NOT EXISTS `demo_auth_token` (
  `token` varchar(64) NOT NULL,
  `account_id` bigint NOT NULL,
  `expires_at` datetime NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`token`),
  KEY `idx_demo_auth_token_account_id` (`account_id`),
  KEY `idx_demo_auth_token_expires_at` (`expires_at`),
  CONSTRAINT `fk_demo_auth_token_account`
    FOREIGN KEY (`account_id`) REFERENCES `demo_auth_account` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 明文密码只服务学习 Demo，真实项目应保存加盐哈希后的密码。
INSERT INTO `demo_auth_account` (`id`, `username`, `password`, `enabled`) VALUES
    (1, 'admin', 'admin123', 1),
    (2, 'alice', 'alice123', 1)
ON DUPLICATE KEY UPDATE
    `username` = VALUES(`username`),
    `password` = VALUES(`password`),
    `enabled` = VALUES(`enabled`);

INSERT INTO `demo_auth_role` (`id`, `role_code`, `role_name`) VALUES
    (1, 'ADMIN', '管理员'),
    (2, 'USER', '普通用户')
ON DUPLICATE KEY UPDATE
    `role_code` = VALUES(`role_code`),
    `role_name` = VALUES(`role_name`);

INSERT INTO `demo_auth_permission` (`id`, `permission_code`, `permission_name`) VALUES
    (1, 'secure:read', '访问安全示例读接口'),
    (2, 'secure:admin', '访问安全示例管理接口'),
    (3, 'blog:read', '读取博客'),
    (4, 'blog:create', '创建博客'),
    (5, 'blog:update', '更新博客'),
    (6, 'blog:delete', '删除博客')
ON DUPLICATE KEY UPDATE
    `permission_code` = VALUES(`permission_code`),
    `permission_name` = VALUES(`permission_name`);

INSERT IGNORE INTO `demo_auth_account_role` (`account_id`, `role_id`) VALUES
    (1, 1),
    (1, 2),
    (2, 2);

INSERT IGNORE INTO `demo_auth_role_permission` (`role_id`, `permission_id`) VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    (1, 5),
    (1, 6),
    (2, 1),
    (2, 3);
