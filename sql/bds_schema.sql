-- 创建数据库 bds
CREATE DATABASE IF NOT EXISTS bds;

-- 使用数据库 bds
USE bds;

-- 1. 创建 `user` 表
CREATE TABLE IF NOT EXISTS user
(
    user_id      BIGINT AUTO_INCREMENT PRIMARY KEY,                                                -- 用户ID，主键
    nickname     VARCHAR(255)                                  NOT NULL,                           -- 昵称，非唯一
    password     VARCHAR(255)                                  NOT NULL,                           -- 密码
    email        VARCHAR(255),                                                                     -- 电子邮件
    phone_number VARCHAR(20),                                                                      -- 电话号码
    role         ENUM ('DONOR', 'BENEFICIARY', 'ADMIN', 'ORG') NOT NULL,                           -- 用户角色，四种角色
    status       ENUM ('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',                                     -- 用户状态
    created_at   TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP,                            -- 注册时间
    updated_at   TIMESTAMP                   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新时间
);

-- 2. 创建 `user_profile` 表
CREATE TABLE IF NOT EXISTS user_profile
(
    user_id             BIGINT PRIMARY KEY, -- 用户ID，唯一
    full_name           VARCHAR(255),       -- 用户真实姓名
    bio                 TEXT,               -- 用户个人简介
    profile_picture_url VARCHAR(255),       -- 头像URL
    donation_history    JSON,               -- 捐赠历史（JSON格式）
    assistance_history  JSON                -- 受助历史（JSON格式）
);

-- 3. 创建 `role` 表
CREATE TABLE IF NOT EXISTS role
(
    role_id     INT AUTO_INCREMENT PRIMARY KEY,                         -- 角色ID，主键
    role_name   ENUM ('DONOR', 'BENEFICIARY', 'ADMIN', 'ORG') NOT NULL, -- 角色名称
    description TEXT                                                    -- 角色描述
);

-- 4. 创建 `user_role` 表（没有外键约束）
CREATE TABLE IF NOT EXISTS user_role
(
    user_id BIGINT,                 -- 用户ID
    role_id INT,                    -- 角色ID
    PRIMARY KEY (user_id, role_id), -- 联合主键
    INDEX idx_user_id (user_id),    -- 为 user_id 添加索引，提高查询效率
    INDEX idx_role_id (role_id)     -- 为 role_id 添加索引，提高查询效率
);

-- 5. 创建 `permission` 表
CREATE TABLE IF NOT EXISTS permission
(
    permission_id   INT AUTO_INCREMENT PRIMARY KEY, -- 权限ID
    permission_name VARCHAR(255) NOT NULL,          -- 权限名称
    description     TEXT                            -- 权限描述
);

-- 6. 创建 `role_permission` 表（没有外键约束）
CREATE TABLE IF NOT EXISTS role_permission
(
    role_id       INT,                      -- 角色ID
    permission_id INT,                      -- 权限ID
    PRIMARY KEY (role_id, permission_id),   -- 联合主键
    INDEX idx_role_id (role_id),            -- 为 role_id 添加索引，提高查询效率
    INDEX idx_permission_id (permission_id) -- 为 permission_id 添加索引，提高查询效率
);