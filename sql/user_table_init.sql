-- =============================================
-- 用户表初始化脚本
-- =============================================

USE seckill;

-- =============================================
-- 用户表
-- =============================================
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密存储)',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0-普通用户, 1-管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE INDEX uk_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 插入默认管理员用户 (密码: admin123)
-- =============================================
INSERT INTO user (username, password, email, phone, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVM7Fy', 'admin@seckill.com', '13800138000', 1, 1);

-- 插入测试普通用户 (密码: user123)
INSERT INTO user (username, password, email, phone, role, status) VALUES
('user', '$2a$10$HcE6eF6XKf7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVM7Fy', 'user@seckill.com', '13800138001', 0, 1);

SELECT '用户表初始化完成!' AS message;
SELECT * FROM user;
