-- 创建数据库 bds
CREATE DATABASE IF NOT EXISTS bds;

-- 使用数据库 bds
USE bds;

-- 1. 创建 `user` 表
CREATE TABLE IF NOT EXISTS user
(
    -- 基本信息
    user_id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID，主键',
    username             VARCHAR(255) COMMENT '用户昵称，非唯一',
    password             VARCHAR(255)                        NOT NULL COMMENT '用户密码，存储加密值',
    email                VARCHAR(255) COMMENT '用户电子邮件地址',
    phone_number         VARCHAR(20) COMMENT '用户电话号码',
    avatar               VARCHAR(255) COMMENT '用户个人头像的URL | 公益组织LOGO',

    -- 角色相关字段
    role                 ENUM ('INDIVIDUAL', 'ADMIN', 'ORG') NOT NULL COMMENT '用户角色：个体(捐赠者和受助者)、管理员、公益组织',
    admin_level          TINYINT UNSIGNED COMMENT '管理员等级：1-基础，2-审核组织、项目资质',
    status               ENUM ('ACTIVE', 'INACTIVE')              DEFAULT 'ACTIVE' COMMENT '用户状态：ACTIVE 表示启用，INACTIVE 表示禁用',

    -- 个体用户相关字段
    user_real_name       VARCHAR(255) COMMENT '用户真实姓名',
    id_card_number       VARCHAR(18) COMMENT '用户身份证号',
    user_bio             TEXT COMMENT '用户个人简介',
    user_bank_account    VARCHAR(255) COMMENT '用户银行账户信息',

    -- 公益组织相关字段
    org_name             VARCHAR(255) COMMENT '公益组织名称',
    org_bio              TEXT COMMENT '公益组织简介或宗旨说明',
    org_license_number   VARCHAR(255) COMMENT '公益组织注册号或营业执照编号',
    org_bank_account     VARCHAR(255) COMMENT '公益组织银行账户信息',
    certification_status ENUM ('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING' COMMENT '公益组织认证状态',
    certification_notes  TEXT COMMENT '认证状态备注或原因',
    verifier_id          BIGINT COMMENT '最后审核该组织信息的管理员ID',
    contact_person_name  VARCHAR(255) COMMENT '公益组织联系人姓名',

    -- 历史记录及附加信息
    donation_history     JSON COMMENT '用户的捐赠历史记录（JSON格式）',
    assistance_history   JSON COMMENT '用户的受助历史记录（JSON格式）',

    -- 通用控制字段
    is_deleted           BOOLEAN                                  DEFAULT FALSE COMMENT '逻辑删除标志：TRUE 表示已删除',
    created_at           TIMESTAMP                                DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间',
    updated_at           TIMESTAMP                                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户信息最后更新时间'
) COMMENT '用户表';

-- 2. 插入测试数据
INSERT INTO user (username, password, email, phone_number, avatar, role, admin_level, status,
                  user_real_name, id_card_number, user_bio, user_bank_account,
                  org_name, org_bio, org_license_number, org_bank_account,
                  certification_status, certification_notes, verifier_id, contact_person_name,
                  donation_history, assistance_history)
VALUES ('john_doe', 'hashed_password_1', 'john.doe@example.com', '1234567890', 'https://example.com/avatar1.jpg',
        'INDIVIDUAL', NULL, 'ACTIVE',
        'John Doe', '123456789012345678', 'Donor bio', '1234567890123456789',
        NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        '{
          "donated_to": "Project A",
          "amount": 100
        }', NULL),

       ('jane_doe', 'hashed_password_2', 'jane.doe@example.com', '0987654321', 'https://example.com/avatar2.jpg',
        'INDIVIDUAL', NULL, 'ACTIVE',
        'Jane Doe', '234567890123456789', 'Beneficiary bio', '9876543210987654321',
        NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        NULL, '{
         "assisted_by": "Organization B",
         "amount": 50
       }'),

       ('admin_1', 'hashed_password_3', 'admin1@example.com', '1231231234', 'https://example.com/avatar3.jpg', 'ADMIN',
        1, 'ACTIVE',
        NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        NULL, NULL),

       ('org_1', 'hashed_password_4', 'org1@example.com', '5432143210', 'https://example.com/avatar4.jpg', 'ORG', NULL,
        'ACTIVE',
        NULL, NULL, NULL, NULL,
        'Organization A', 'Purpose of Organization A', 'ORG12345', '1234567890',
        'PENDING', 'Pending approval from admin', NULL, 'Alice Johnson',
        NULL, NULL),

       ('org_2', 'hashed_password_5', 'org2@example.com', '9876543210', 'https://example.com/avatar5.jpg', 'ORG', NULL,
        'ACTIVE',
        NULL, NULL, NULL, NULL,
        'Organization B', 'Purpose of Organization B', 'ORG67890', '9876543210',
        'APPROVED', 'Approved by admin', 1, 'Bob Smith',
        NULL, NULL),

       ('john_smith', 'hashed_password_6', 'john.smith@example.com', '1231231234', 'https://example.com/avatar6.jpg',
        'INDIVIDUAL', NULL, 'ACTIVE',
        'John Smith', '345678901234567890', 'Donor bio 2', '111222333444555666',
        NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        '{
          "donated_to": "Project C",
          "amount": 200
        }', NULL),

       ('jane_smith', 'hashed_password_7', 'jane.smith@example.com', '2342342345', 'https://example.com/avatar7.jpg',
        'INDIVIDUAL', NULL, 'ACTIVE',
        'Jane Smith', '456789012345678901', 'Beneficiary bio 2', '555666777888999000',
        NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        NULL, '{
         "assisted_by": "Organization C",
         "amount": 150
       }'),

       ('admin_2', 'hashed_password_8', 'admin2@example.com', '3213213210', 'https://example.com/avatar8.jpg', 'ADMIN',
        2, 'ACTIVE',
        NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL,
        NULL, NULL),

       ('org_3', 'hashed_password_9', 'org3@example.com', '9879879876', 'https://example.com/avatar9.jpg', 'ORG', NULL,
        'ACTIVE',
        NULL, NULL, NULL, NULL,
        'Organization C', 'Purpose of Organization C', 'ORG11223', '6789012345',
        'REJECTED', 'Rejected by admin due to issues', NULL, 'Charlie Green',
        NULL, NULL),

       ('org_4', 'hashed_password_10', 'org4@example.com', '6546546543', 'https://example.com/avatar10.jpg', 'ORG',
        NULL, 'ACTIVE',
        NULL, NULL, NULL, NULL,
        'Organization D', 'Purpose of Organization D', 'ORG99887', '1234987654',
        'PENDING', 'Waiting for admin approval', NULL, 'David Brown',
        NULL, NULL);
