-- MyBatis blog Demo 初始化脚本。
-- 运行应用前先执行本目录下的 t_auth.sql、t_blog.sql。

CREATE DATABASE IF NOT EXISTS `study_demo`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `study_demo`;

CREATE TABLE IF NOT EXISTS `t_blog` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `appreciation` tinyint(1) NOT NULL DEFAULT 0,
  `commentabled` tinyint(1) NOT NULL DEFAULT 1,
  `content` longtext,
  `create_time` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `first_picture` varchar(255) DEFAULT NULL,
  `flag` varchar(255) DEFAULT NULL,
  `published` tinyint(1) NOT NULL DEFAULT 1,
  `recommend` tinyint(1) NOT NULL DEFAULT 0,
  `share_statement` tinyint(1) NOT NULL DEFAULT 1,
  `title` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `views` int DEFAULT 0,
  `type_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_t_blog_type_id` (`type_id`),
  KEY `idx_t_blog_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 示例数据用于验证 blog 读写接口和权限控制。
INSERT INTO `t_blog` (
    `id`, `appreciation`, `commentabled`, `content`, `create_time`, `description`,
    `first_picture`, `flag`, `published`, `recommend`, `share_statement`, `title`,
    `update_time`, `views`, `type_id`, `user_id`
) VALUES
    (1, 1, 1, '这是一篇用于 MyBatis-Plus CRUD 学习的示例内容。', NOW(), 'MyBatis-Plus 示例数据',
     '', '原创', 1, 1, 1, 'MyBatis-Plus 入门示例', NOW(), 12, 1, 1),
    (2, 0, 1, '这篇文章用于验证 blog:read、blog:create、blog:update、blog:delete 权限。', NOW(), '认证授权联动示例',
     '', '原创', 1, 0, 1, '认证授权和博客接口联动', NOW(), 3, 1, 2)
ON DUPLICATE KEY UPDATE
    `appreciation` = VALUES(`appreciation`),
    `commentabled` = VALUES(`commentabled`),
    `content` = VALUES(`content`),
    `description` = VALUES(`description`),
    `first_picture` = VALUES(`first_picture`),
    `flag` = VALUES(`flag`),
    `published` = VALUES(`published`),
    `recommend` = VALUES(`recommend`),
    `share_statement` = VALUES(`share_statement`),
    `title` = VALUES(`title`),
    `views` = VALUES(`views`),
    `type_id` = VALUES(`type_id`),
    `user_id` = VALUES(`user_id`);
