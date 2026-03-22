package com.seckill.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            initUserTable();
        } catch (Exception e) {
            log.error("数据初始化失败", e);
        }
    }

    private void initUserTable() {
        try {
            // 检查表是否存在
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'sys_user'",
                Integer.class
            );
            if (count != null && count > 0) {
                log.info("用户表已存在");
                return;
            }
        } catch (Exception e) {
            log.warn("检查表存在性失败，尝试创建表: {}", e.getMessage());
        }
        
        // 表不存在，创建表
        log.info("创建用户表...");
        
        try {
            String createTableSql = """
                CREATE TABLE IF NOT EXISTS sys_user (
                    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    username VARCHAR(50) NOT NULL COMMENT '用户名',
                    password VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
                    role TINYINT NOT NULL DEFAULT 0 COMMENT '角色: 0-普通用户, 1-管理员',
                    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
                    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                    PRIMARY KEY (id),
                    UNIQUE INDEX uk_username (username),
                    INDEX idx_role (role),
                    INDEX idx_status (status)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表'
                """;
            
            jdbcTemplate.execute(createTableSql);
            
            // 插入管理员账号（密码: admin123）
            String insertAdminSql = """
                INSERT INTO sys_user (username, password, role, status) 
                VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EO', 1, 1)
                """;
            
            jdbcTemplate.execute(insertAdminSql);
            log.info("用户表创建成功，管理员账号已初始化");
        } catch (Exception e) {
            log.error("创建用户表失败: {}", e.getMessage());
            throw e;
        }
    }
}
