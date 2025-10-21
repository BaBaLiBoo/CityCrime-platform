-- 修复 Case_Persons 表结构
USE crime_platform_db;

-- 禁用外键检查
SET FOREIGN_KEY_CHECKS=0;

-- 删除现有的 Case_Persons 表
DROP TABLE IF EXISTS `Case_Persons`;

-- 重新创建 Case_Persons 表，使用正确的结构
CREATE TABLE `Case_Persons` (
    `case_id` INT,
    `id_number` VARCHAR(18),
    `role_in_case` ENUM('嫌疑人', '受害人', '证人') NOT NULL,
    PRIMARY KEY (`case_id`, `id_number`, `role_in_case`),
    FOREIGN KEY (`case_id`) REFERENCES `Cases`(`case_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (`id_number`) REFERENCES `Persons`(`id_number`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB COMMENT='连接案件与涉案人员的多对多关系表';

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS=1;

SELECT 'Case_Persons 表结构修复完成！' AS 'Status';
