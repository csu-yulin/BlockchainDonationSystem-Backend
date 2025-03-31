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

-- 3. 创建 `project` 表
CREATE TABLE IF NOT EXISTS project
(
    -- 基本信息
    project_id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '项目ID，主键',
    project_name        VARCHAR(255)               NOT NULL COMMENT '项目名称',
    description         TEXT COMMENT '项目详细描述',
    cover_image         VARCHAR(255) COMMENT '项目封面图片的URL',

    -- 项目发起者相关
    creator_id          BIGINT                     NOT NULL COMMENT '项目创建者的用户ID（关联user表）',
    creator_role        ENUM ('INDIVIDUAL', 'ORG') NOT NULL COMMENT '项目创建者角色：个人或公益组织',
    org_name            VARCHAR(255) COMMENT '若由公益组织发起，记录组织名称',
    contact_person_name VARCHAR(255) COMMENT '项目联系人姓名',

    -- 项目状态及审批相关
    approval_status     ENUM ('PENDING', 'APPROVED', 'REJECTED')            DEFAULT 'PENDING' COMMENT '项目审批状态',
    approval_notes      TEXT COMMENT '审批备注或原因',
    verifier_id         BIGINT COMMENT '最后审核该项目的管理员ID（关联user表）',
    status              ENUM ('ACTIVE', 'COMPLETED', 'CANCELLED','EXPIRED') DEFAULT 'ACTIVE'
        COMMENT '项目状态：进行中、已完成、已取消、已过期',

    -- 项目资金管理
    target_amount       DECIMAL(15, 2)             NOT NULL COMMENT '目标募集金额',
    raised_amount       DECIMAL(15, 2)                                      DEFAULT 0 COMMENT '已募集金额',
    bank_account        VARCHAR(255) COMMENT '用于接收捐款的银行账户',

    -- 项目时间管理
    start_date          DATE                       NOT NULL COMMENT '项目开始日期',
    end_date            DATE COMMENT '项目结束日期（可为空，表示长期项目）',

    -- 历史记录
    donation_records    JSON COMMENT '项目的捐赠记录（JSON格式）',
    activity_records    JSON COMMENT '项目相关的活动记录（如更新、公告等，JSON格式）',

    -- 通用控制字段
    is_deleted          BOOLEAN                                             DEFAULT FALSE COMMENT '逻辑删除标志：TRUE 表示已删除',
    created_at          TIMESTAMP                                           DEFAULT CURRENT_TIMESTAMP COMMENT '项目创建时间',
    updated_at          TIMESTAMP                                           DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '项目最后更新时间'
) COMMENT '项目表';

-- 4. 插入测试数据
INSERT INTO project (project_name, description, cover_image, creator_id, creator_role,
                     org_name, contact_person_name, approval_status, approval_notes, verifier_id,
                     status, target_amount, raised_amount, bank_account, start_date, end_date,
                     donation_records, activity_records, is_deleted, created_at, updated_at)
VALUES ('助学计划', '帮助贫困地区学生完成学业', 'https://example.com/images/project1.jpg', 101, 'ORG',
        '阳光公益', '李明', 'APPROVED', '项目内容符合资助标准', 201,
        'ACTIVE', 100000.00, 50000.00, '6222020000001', '2024-01-01', '2024-12-31',
        NULL, NULL, FALSE, NOW(), NOW()),

       ('环保植树行动', '在荒漠化地区开展植树活动', 'https://example.com/images/project2.jpg', 102, 'INDIVIDUAL',
        NULL, NULL, 'APPROVED', '审核通过', 202,
        'ACTIVE', 50000.00, 20000.00, '6222020000002', '2024-03-01', NULL,
        NULL, NULL, FALSE, NOW(), NOW()),

       ('赈灾募捐', '为受灾地区提供紧急物资援助', 'https://example.com/images/project3.jpg', 103, 'ORG',
        '希望之家', '张红', 'APPROVED', '资助方案明确', 201,
        'ACTIVE', 200000.00, 150000.00, '6222020000003', '2024-01-15', '2024-06-30',
        NULL, NULL, FALSE, NOW(), NOW()),

       ('义诊活动', '组织医生为山区居民提供义诊服务', 'https://example.com/images/project4.jpg', 104, 'ORG',
        '健康公益', '赵敏', 'PENDING', NULL, NULL,
        'ACTIVE', 30000.00, 5000.00, '6222020000004', '2024-04-01', '2024-04-15',
        NULL, NULL, FALSE, NOW(), NOW()),

       ('社区图书馆建设', '为偏远社区建设小型图书馆', 'https://example.com/images/project5.jpg', 105, 'ORG',
        '书香传递', '刘强', 'REJECTED', '不符合资助标准', 203,
        'CANCELLED', 80000.00, 0.00, '6222020000005', '2024-02-01', NULL,
        NULL, NULL, FALSE, NOW(), NOW()),

       ('贫困户生活补助', '为农村贫困家庭提供生活补助', 'https://example.com/images/project6.jpg', 106, 'INDIVIDUAL',
        NULL, NULL, 'APPROVED', '审核通过', 201,
        'ACTIVE', 100000.00, 75000.00, '6222020000006', '2024-01-10', '2024-12-10',
        NULL, NULL, FALSE, NOW(), NOW()),

       ('无障碍设施改造', '为残疾人家庭改造无障碍设施', 'https://example.com/images/project7.jpg', 107, 'ORG',
        '无障爱心', '孙杰', 'APPROVED', '支持方案完整', 202,
        'COMPLETED', 40000.00, 40000.00, '6222020000007', '2023-11-01', '2023-12-31',
        NULL, NULL, FALSE, NOW(), NOW()),

       ('儿童营养餐计划', '为贫困儿童提供免费午餐', 'https://example.com/images/project8.jpg', 108, 'ORG',
        '阳光公益', '李明', 'PENDING', NULL, NULL,
        'ACTIVE', 120000.00, 30000.00, '6222020000008', '2024-05-01', NULL,
        NULL, NULL, FALSE, NOW(), NOW()),

       ('流浪动物收容', '建立流浪动物临时收容所', 'https://example.com/images/project9.jpg', 109, 'INDIVIDUAL',
        NULL, NULL, 'APPROVED', '资助有效', 202,
        'ACTIVE', 50000.00, 10000.00, '6222020000009', '2024-03-01', '2024-12-31',
        NULL, NULL, FALSE, NOW(), NOW()),

       ('乡村医疗设备升级', '帮助乡村医院购置医疗设备', 'https://example.com/images/project10.jpg', 110, 'ORG',
        '健康公益', '赵敏', 'APPROVED', '资助充分合理', 203,
        'ACTIVE', 150000.00, 60000.00, '6222020000010', '2024-02-01', '2024-10-31',
        NULL, NULL, FALSE, NOW(), NOW());

-- 5. 创建 `donation` 表
CREATE TABLE IF NOT EXISTS donation
(
    donation_id      BIGINT PRIMARY KEY COMMENT '捐款ID，与链上 donationId 一致',
    user_id          BIGINT         NOT NULL COMMENT '捐款用户ID（关联 user 表）',
    project_id       BIGINT         NOT NULL COMMENT '关联项目ID（关联 project 表）',
    amount           DECIMAL(15, 2) NOT NULL COMMENT '捐款金额',
    timestamp        TIMESTAMP      NOT NULL COMMENT '捐款时间戳，与链上一致',
    transaction_hash VARCHAR(66) COMMENT '区块链交易哈希，用于追溯链上记录',
    is_deleted       BOOLEAN   DEFAULT FALSE COMMENT '逻辑删除标志',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
    FOREIGN KEY (user_id) REFERENCES user (user_id),
    FOREIGN KEY (project_id) REFERENCES project (project_id)
) COMMENT '捐款记录表';

-- 6. 插入测试数据
INSERT INTO donation (donation_id, user_id, project_id, amount, timestamp, transaction_hash, is_deleted, created_at,
                      updated_at)
VALUES (1, 1, 1, 100.00, '2025-03-20 10:00:00', '0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef',
        FALSE, '2025-03-20 10:00:00', '2025-03-20 10:00:00'),
       (2, 2, 1, 50.50, '2025-03-20 14:30:00', '0xabcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890',
        FALSE, '2025-03-20 14:30:00', '2025-03-20 14:30:00'),
       (3, 3, 2, 200.75, '2025-03-21 09:15:00', '0x7890abcdef1234567890abcdef1234567890abcdef1234567890abcdef123456',
        FALSE, '2025-03-21 09:15:00', '2025-03-21 09:15:00'),
       (4, 4, 2, 300.00, '2025-03-22 16:45:00', '0x4567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef123',
        FALSE, '2025-03-22 16:45:00', '2025-03-22 16:45:00'),
       (5, 5, 3, 75.25, '2025-03-23 11:20:00', '0xdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abc',
        FALSE, '2025-03-23 11:20:00', '2025-03-23 11:20:00'),
       (6, 1, 3, 150.00, '2025-03-24 08:30:00', '0xabc1234567890defabc1234567890defabc1234567890defabc1234567890def',
        FALSE, '2025-03-24 08:30:00', '2025-03-24 08:30:00'),
       (7, 2, 1, 250.80, '2025-03-24 15:10:00', '0x7890defabc1234567890defabc1234567890defabc1234567890defabc123456',
        FALSE, '2025-03-24 15:10:00', '2025-03-24 15:10:00'),
       (8, 3, 2, 80.00, '2025-03-25 13:25:00', '0x4567890defabc1234567890defabc1234567890defabc1234567890defabc123',
        FALSE, '2025-03-25 13:25:00', '2025-03-25 13:25:00'),
       (9, 4, 3, 500.00, '2025-03-25 17:50:00', '0x1234567890defabc1234567890defabc1234567890defabc1234567890defabc',
        FALSE, '2025-03-25 17:50:00', '2025-03-25 17:50:00'),
       (10, 5, 1, 30.90, '2025-03-26 09:00:00', '0xdefabc1234567890defabc1234567890defabc1234567890defabc1234567890',
        FALSE, '2025-03-26 09:00:00', '2025-03-26 09:00:00');

-- 7. 创建 `voucher` 表
CREATE TABLE IF NOT EXISTS voucher
(
    voucher_id       BIGINT PRIMARY KEY COMMENT '凭证ID，与链上 voucherId 一致',
    project_id       BIGINT       NOT NULL COMMENT '关联项目ID（关联 project 表）',
    org_id           BIGINT       NOT NULL COMMENT '公益组织ID（关联 user 表，role=ORG）',
    ipfs_hash        VARCHAR(255) NOT NULL COMMENT '凭证文件的IPFS哈希，与链上 bytes32 一致',
    timestamp        TIMESTAMP    NOT NULL COMMENT '上传时间戳，与链上一致',
    transaction_hash VARCHAR(66) COMMENT '区块链交易哈希，用于追溯链上记录',
    file_url         VARCHAR(255) COMMENT '凭证文件的实际URL（可选，链下存储）',
    is_deleted       BOOLEAN   DEFAULT FALSE COMMENT '逻辑删除标志',
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
    FOREIGN KEY (project_id) REFERENCES project (project_id),
    FOREIGN KEY (org_id) REFERENCES user (user_id)
) COMMENT '凭证记录表';

-- 8. 插入测试数据
INSERT INTO voucher (voucher_id, project_id, org_id, ipfs_hash, timestamp, transaction_hash, file_url, is_deleted,
                     created_at, updated_at)
VALUES (1, 1, 6, '0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef', '2025-03-20 09:00:00',
        '0xabcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890', 'https://ipfs.io/ipfs/QmA1', FALSE,
        '2025-03-20 09:00:00', '2025-03-20 09:00:00'),
       (2, 1, 7, '0xabcdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890', '2025-03-20 14:15:00',
        '0x7890abcdef1234567890abcdef1234567890abcdef1234567890abcdef123456', 'https://ipfs.io/ipfs/QmA2', FALSE,
        '2025-03-20 14:15:00', '2025-03-20 14:15:00'),
       (3, 2, 8, '0x7890abcdef1234567890abcdef1234567890abcdef1234567890abcdef123456', '2025-03-21 10:30:00',
        '0x4567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef123', 'https://ipfs.io/ipfs/QmA3', FALSE,
        '2025-03-21 10:30:00', '2025-03-21 10:30:00'),
       (4, 2, 6, '0x4567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef123', '2025-03-22 13:45:00',
        '0xdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abc', 'https://ipfs.io/ipfs/QmA4', FALSE,
        '2025-03-22 13:45:00', '2025-03-22 13:45:00'),
       (5, 3, 7, '0xdef1234567890abcdef1234567890abcdef1234567890abcdef1234567890abc', '2025-03-23 08:20:00',
        '0xabc1234567890defabc1234567890defabc1234567890defabc1234567890def', 'https://ipfs.io/ipfs/QmA5', FALSE,
        '2025-03-23 08:20:00', '2025-03-23 08:20:00'),
       (6, 3, 8, '0xabc1234567890defabc1234567890defabc1234567890defabc1234567890def', '2025-03-23 16:10:00',
        '0x7890defabc1234567890defabc1234567890defabc1234567890defabc123456', 'https://ipfs.io/ipfs/QmA6', FALSE,
        '2025-03-23 16:10:00', '2025-03-23 16:10:00'),
       (7, 1, 6, '0x7890defabc1234567890defabc1234567890defabc1234567890defabc123456', '2025-03-24 11:25:00',
        '0x4567890defabc1234567890defabc1234567890defabc1234567890defabc123', 'https://ipfs.io/ipfs/QmA7', FALSE,
        '2025-03-24 11:25:00', '2025-03-24 11:25:00'),
       (8, 2, 7, '0x4567890defabc1234567890defabc1234567890defabc1234567890defabc123', '2025-03-25 09:50:00',
        '0x1234567890defabc1234567890defabc1234567890defabc1234567890defabc', 'https://ipfs.io/ipfs/QmA8', FALSE,
        '2025-03-25 09:50:00', '2025-03-25 09:50:00'),
       (9, 3, 8, '0x1234567890defabc1234567890defabc1234567890defabc1234567890defabc', '2025-03-25 15:30:00',
        '0xdefabc1234567890defabc1234567890defabc1234567890defabc1234567890', 'https://ipfs.io/ipfs/QmA9', FALSE,
        '2025-03-25 15:30:00', '2025-03-25 15:30:00'),
       (10, 1, 6, '0xdefabc1234567890defabc1234567890defabc1234567890defabc1234567890', '2025-03-26 10:00:00',
        '0xabcdef7890123456abcdef7890123456abcdef7890123456abcdef7890123456', 'https://ipfs.io/ipfs/QmA10', FALSE,
        '2025-03-26 10:00:00', '2025-03-26 10:00:00');