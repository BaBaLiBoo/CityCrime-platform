-- =================================================================
-- 城市犯罪事件管理平台 (Urban Crime Incident Management Platform)
-- 数据库初始化脚本 v2 (修复版 for MySQL 5.7)
-- =================================================================

-- 强制会话使用 utf8mb4 编码，解决中文乱码和错误
SET NAMES 'utf8mb4';
SET FOREIGN_KEY_CHECKS=0; -- 临时禁用外键检查，防止因表创建顺序问题报错

-- 步骤 1: 创建数据库
DROP DATABASE IF EXISTS `crime_platform_db`;
CREATE DATABASE `crime_platform_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `crime_platform_db`;

-- 步骤 2: 创建表
-- 表: Locations (案件地点信息表)
CREATE TABLE `Locations` (
    `location_id` INT AUTO_INCREMENT PRIMARY KEY,
    `address` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '详细地址',
    `district` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '行政区',
    `longitude` DECIMAL(11, 8) COMMENT '经度',
    `latitude` DECIMAL(10, 8) COMMENT '纬度'
) ENGINE=InnoDB COMMENT='案件地点信息表';

-- 表: Cases (核心案件信息表)
CREATE TABLE `Cases` (
    `case_id` INT AUTO_INCREMENT PRIMARY KEY,
    `case_title` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '案件标题',
    `case_type` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '案件类型',
    `status` ENUM('已接报', '立案侦查', '已告破', '已归档') NOT NULL DEFAULT '已接报' COMMENT '案件状态',
    `report_time` DATETIME NOT NULL COMMENT '报案时间',
    `filing_time` DATETIME NULL COMMENT '立案时间',
    `solve_time` DATETIME NULL COMMENT '侦破时间',
    `archive_time` DATETIME NULL COMMENT '归档时间',
    `description` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '案件详细描述',
    `location_id` INT,
    FOREIGN KEY (`location_id`) REFERENCES `Locations`(`location_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='核心案件信息表';

-- 表: Persons (所有相关人员的基础信息表)
CREATE TABLE `Persons` (
    `person_id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
    `id_number` VARCHAR(18) UNIQUE NOT NULL COMMENT '身份证号',
    `gender` ENUM('男', '女', '未知') NOT NULL DEFAULT '未知' COMMENT '性别',
    `birth_date` DATE COMMENT '出生日期',
    `address` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '住址',
    `contact_info` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '联系方式'
) ENGINE=InnoDB COMMENT='所有相关人员（嫌疑人、受害人、办案人员）的基础信息表';

-- 表: Officers (办案人员（警员）的特定信息表)
CREATE TABLE `Officers` (
    `officer_id` VARCHAR(20) PRIMARY KEY COMMENT '警员编号作为主键',
    `department` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '所属部门',
    `position` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '职务',
    `name` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
    `id_number` VARCHAR(18) NOT NULL COMMENT '身份证号',
    `gender` ENUM('男', '女', '未知') NOT NULL DEFAULT '未知' COMMENT '性别',
    `birth_date` DATE COMMENT '出生日期',
    `address` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '住址',
    `contact_info` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '联系方式'
) ENGINE=InnoDB COMMENT='办案人员（警员）的特定信息表';

-- 表: UserAccounts (系统登录账户表)
CREATE TABLE `UserAccounts` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY,
    `officer_id` VARCHAR(20) NULL,
    `username` VARCHAR(50) UNIQUE NOT NULL COMMENT '登录用户名',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '哈希后的密码',
    `is_active` TINYINT(1) DEFAULT 1 COMMENT '账户是否激活 (1=是, 0=否)',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`officer_id`) REFERENCES `Officers`(`officer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='系统登录账户表';

-- 表: Roles (用户角色表)
CREATE TABLE `Roles` (
    `role_id` INT AUTO_INCREMENT PRIMARY KEY,
    `role_name` VARCHAR(50) UNIQUE NOT NULL COMMENT '角色名称',
    `description` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
) ENGINE=InnoDB COMMENT='用户角色表';

-- 表: Permissions (系统功能权限表)
CREATE TABLE `Permissions` (
    `permission_id` INT AUTO_INCREMENT PRIMARY KEY,
    `permission_name` VARCHAR(100) UNIQUE NOT NULL COMMENT '权限标识，如 case:create',
    `description` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
) ENGINE=InnoDB COMMENT='系统功能权限表';

-- 步骤 3: 创建关联表和索引
CREATE TABLE `Case_Persons` (
    `case_id` INT,
    `id_number` VARCHAR(18),
    `role_in_case` ENUM('嫌疑人', '受害人', '证人') NOT NULL,
    PRIMARY KEY (`case_id`, `id_number`, `role_in_case`),
    FOREIGN KEY (`case_id`) REFERENCES `Cases`(`case_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`id_number`) REFERENCES `Persons`(`id_number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='连接案件与涉案人员的多对多关系表';

CREATE TABLE `Case_Officers` (
    `case_id` INT,
    `officer_id` VARCHAR(20),
    PRIMARY KEY (`case_id`, `officer_id`),
    FOREIGN KEY (`case_id`) REFERENCES `Cases`(`case_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`officer_id`) REFERENCES `Officers`(`officer_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='连接案件与办案人员的多对多关系表';

CREATE TABLE `User_Roles` (
    `user_id` INT,
    `role_id` INT,
    PRIMARY KEY (`user_id`, `role_id`),
    FOREIGN KEY (`user_id`) REFERENCES `UserAccounts`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES `Roles`(`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='连接用户与角色的多对多关系表';

CREATE TABLE `Role_Permissions` (
    `role_id` INT,
    `permission_id` INT,
    PRIMARY KEY (`role_id`, `permission_id`),
    FOREIGN KEY (`role_id`) REFERENCES `Roles`(`role_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`permission_id`) REFERENCES `Permissions`(`permission_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='连接角色与权限的多对多关系表';

CREATE INDEX `idx_case_type` ON `Cases`(`case_type`);
CREATE INDEX `idx_case_status` ON `Cases`(`status`);
CREATE INDEX `idx_case_report_time` ON `Cases`(`report_time`);
CREATE INDEX `idx_location_district` ON `Locations`(`district`);
CREATE INDEX `idx_user_username` ON `UserAccounts`(`username`);

-- 步骤 4: 插入初始数据
INSERT INTO `Roles` (`role_name`, `description`) VALUES
('系统管理员', '拥有系统的最高管理权限，负责用户、角色和系统配置的管理。对应技术维护人员。'),
('领导决策人员', '拥有查看所有案件信息和统计报表的权限，用于宏观决策。'),
('数据分析人员', '拥有对案件数据进行查询、统计和分析的权限，不能修改案件数据。'),
('警务人员', '系统的主要使用者，拥有案件的录入、修改、查询等日常操作权限。');

INSERT INTO `Permissions` (`permission_name`, `description`) VALUES
('case:create', '创建新案件'), ('case:view:self', '查看自己负责的案件'), ('case:view:all', '查看所有案件'),
('case:update:self', '更新自己负责的案件信息'), ('case:update:all', '更新所有案件信息'), ('case:delete', '删除案件（高危权限）'),
('person:link', '将人员关联到案件'), ('report:view', '查看统计分析报表'),
('system:manage:users', '管理系统用户账户'), ('system:manage:roles', '管理角色和权限');

INSERT INTO `Role_Permissions` (`role_id`, `permission_id`) SELECT r.role_id, p.permission_id FROM `Roles` r, `Permissions` p WHERE r.role_name = '系统管理员';
INSERT INTO `Role_Permissions` (`role_id`, `permission_id`) SELECT r.role_id, p.permission_id FROM `Roles` r, `Permissions` p WHERE r.role_name = '领导决策人员' AND p.permission_name IN ('case:view:all', 'report:view');
INSERT INTO `Role_Permissions` (`role_id`, `permission_id`) SELECT r.role_id, p.permission_id FROM `Roles` r, `Permissions` p WHERE r.role_name = '数据分析人员' AND p.permission_name IN ('case:view:all', 'report:view');
INSERT INTO `Role_Permissions` (`role_id`, `permission_id`) SELECT r.role_id, p.permission_id FROM `Roles` r, `Permissions` p WHERE r.role_name = '警务人员' AND p.permission_name IN ('case:create', 'case:view:self', 'case:update:self', 'person:link');

SET FOREIGN_KEY_CHECKS=1; -- 重新启用外键检查

SELECT '数据库 crime_platform_db 在 MySQL 中创建并初始化成功！' AS 'Status';