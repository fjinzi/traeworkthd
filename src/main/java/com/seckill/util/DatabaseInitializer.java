package com.seckill.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        initUserTable();
    }

    private void initUserTable() {
        try {
            // 先删除用户表（用于测试环境）
            String dropTableSql = "DROP TABLE IF EXISTS user";
            jdbcTemplate.update(dropTableSql);
            System.out.println("已删除旧用户表");
            
            // 创建用户表
            String createTableSql = "CREATE TABLE user (" +
                    "    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID'," +
                    "    username VARCHAR(50) NOT NULL COMMENT '用户名'," +
                    "    password VARCHAR(255) NOT NULL COMMENT '密码(加密存储)'," +
                    "    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱'," +
                    "    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号'," +
                    "    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0-普通用户, 1-管理员'," +
                    "    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用'," +
                    "    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                    "    PRIMARY KEY (id)," +
                    "    UNIQUE INDEX uk_username (username)," +
                    "    INDEX idx_email (email)," +
                    "    INDEX idx_phone (phone)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表'";
            
            jdbcTemplate.update(createTableSql);
            System.out.println("用户表创建成功");

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String adminPassword = encoder.encode("admin123");
            String userPassword = encoder.encode("user123");
            
            System.out.println("管理员密码加密后: " + adminPassword);
            System.out.println("普通用户密码加密后: " + userPassword);

            // 插入默认管理员用户
            String insertAdminSql = "INSERT INTO user (username, password, email, phone, role, status) VALUES " +
                    "('admin', ?, 'admin@seckill.com', '13800138000', 1, 1)";
            jdbcTemplate.update(insertAdminSql, adminPassword);
            System.out.println("管理员用户创建成功 (admin/admin123)");

            // 插入测试普通用户
            String insertUserSql = "INSERT INTO user (username, password, email, phone, role, status) VALUES " +
                    "('user', ?, 'user@seckill.com', '13800138001', 0, 1)";
            jdbcTemplate.update(insertUserSql, userPassword);
            System.out.println("测试用户创建成功 (user/user123)");
            
        } catch (Exception e) {
            System.err.println("初始化用户表失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
