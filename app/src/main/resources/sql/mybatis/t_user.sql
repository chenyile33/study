-- 用户 Demo 初始化脚本。
-- 这张表替代早期的内存用户仓库。

CREATE DATABASE IF NOT EXISTS `study_demo`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `study_demo`;

CREATE TABLE IF NOT EXISTS `demo_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `nickname` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_demo_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 保留早期内存版本中的三条示例用户，便于接口行为前后对比。
INSERT INTO `demo_user` (`id`, `username`, `nickname`, `email`, `created_at`) VALUES
    (1001, 'alice', 'Alice', 'alice@example.com', NOW()),
    (1002, 'bob', 'Bob', 'bob@example.com', NOW()),
    (1003, 'carol', 'Carol', 'carol@example.com', NOW())
ON DUPLICATE KEY UPDATE
    `username` = VALUES(`username`),
    `nickname` = VALUES(`nickname`),
    `email` = VALUES(`email`);
