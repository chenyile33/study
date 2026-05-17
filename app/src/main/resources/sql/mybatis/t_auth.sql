-- 认证 Demo 初始化脚本。
-- 当前同时演示 opaque token 和 JWT；账号、角色、权限和 opaque token 状态都落到 MySQL。

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

-- 账号资料表，登录凭证和展示资料分开保存。
CREATE TABLE IF NOT EXISTS `demo_auth_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint NOT NULL,
  `nickname` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_demo_auth_profile_account_id` (`account_id`),
  UNIQUE KEY `uk_demo_auth_profile_email` (`email`),
  CONSTRAINT `fk_demo_auth_profile_account`
    FOREIGN KEY (`account_id`) REFERENCES `demo_auth_account` (`id`)
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

-- password 保存 BCrypt 哈希值，不保存明文密码。
INSERT INTO `demo_auth_account` (`id`, `username`, `password`, `enabled`) VALUES
    (1, 'admin', '$2a$10$vIiYbRspd.Y0SWzlxFWJiOoRXLyoxEl7e9GZLn7dUDJREa6TgnwdW', 1),
    (2, 'alice', '$2a$10$5ExF4GmmVCAkAtPz9QDj9eQABioPn6GUiWO8ecI.AmzAgmUMo3aSa', 1)
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
    (6, 'blog:delete', '删除博客'),
    (7, 'auth:profile:read', '分页查询认证账号资料'),
    (8, 'auth:account:read', '查看认证账号管理详情'),
    (9, 'auth:account:write', '修改认证账号状态'),
    (10, 'auth:role:read', '查看认证角色'),
    (11, 'auth:role:write', '分配账号角色'),
    (12, 'auth:permission:read', '查看认证权限码'),
    (13, 'auth:permission:write', '分配角色权限码')
ON DUPLICATE KEY UPDATE
    `permission_code` = VALUES(`permission_code`),
    `permission_name` = VALUES(`permission_name`);

INSERT INTO `demo_auth_profile` (`account_id`, `nickname`, `email`) VALUES
    (1, 'Admin', 'admin@example.com'),
    (2, 'Alice', 'alice@example.com')
ON DUPLICATE KEY UPDATE
    `nickname` = VALUES(`nickname`),
    `email` = VALUES(`email`);

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
    (1, 7),
    (1, 8),
    (1, 9),
    (1, 10),
    (1, 11),
    (1, 12),
    (1, 13),
    (2, 1),
    (2, 3);
