-- 更新 Officers 表结构以匹配新的实体类
-- 将 Officers 表改为包含所有人员信息字段，不再依赖 Persons 表

USE crime_platform_db;

-- 首先删除外键约束
ALTER TABLE Officers DROP FOREIGN KEY officers_ibfk_1;
ALTER TABLE UserAccounts DROP FOREIGN KEY useraccounts_ibfk_1;

-- 删除旧的 person_id 列
ALTER TABLE Officers DROP COLUMN person_id;

-- 添加新的列
ALTER TABLE Officers 
ADD COLUMN name VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名' AFTER officer_id,
ADD COLUMN id_number VARCHAR(18) NOT NULL COMMENT '身份证号' AFTER name,
ADD COLUMN gender ENUM('男', '女', '未知') NOT NULL DEFAULT '未知' COMMENT '性别' AFTER id_number,
ADD COLUMN birth_date DATE COMMENT '出生日期' AFTER gender,
ADD COLUMN address VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '住址' AFTER birth_date,
ADD COLUMN contact_info VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '联系方式' AFTER address;

-- 更新 UserAccounts 表，将 officer_id 改为可为空
ALTER TABLE UserAccounts MODIFY COLUMN officer_id VARCHAR(20) NULL;

-- 重新添加外键约束（如果需要的话）
-- ALTER TABLE UserAccounts ADD FOREIGN KEY (officer_id) REFERENCES Officers(officer_id) ON DELETE CASCADE ON UPDATE CASCADE;
