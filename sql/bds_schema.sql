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

    -- 区块链相关
    transaction_hash    VARCHAR(1000) COMMENT '区块链交易哈希，用于追溯链上记录',

    -- 通用控制字段
    is_deleted          BOOLEAN                                             DEFAULT FALSE COMMENT '逻辑删除标志：TRUE 表示已删除',
    created_at          TIMESTAMP                                           DEFAULT CURRENT_TIMESTAMP COMMENT '项目创建时间',
    updated_at          TIMESTAMP                                           DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '项目最后更新时间'
) COMMENT '项目表';

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

CREATE TABLE IF NOT EXISTS fund_flow
(
    flow_id          BIGINT PRIMARY KEY COMMENT '资金流动ID，与链上 flowId 一致',
    project_id       BIGINT         NOT NULL COMMENT '关联项目ID（关联 project 表）',
    recipient_id     BIGINT         NOT NULL COMMENT '资金接收者ID（关联 user 表，role=INDIVIDUAL 或 ORG）',
    amount           DECIMAL(15, 2) NOT NULL COMMENT '流动金额',
    reason           VARCHAR(255)   NOT NULL COMMENT '挪用理由（如“用户求助”、“项目支出”）',
    proof_file_url   VARCHAR(255) COMMENT '用户求助证明资料的阿里云 OSS URL（用户申领时使用）',
    voucher_id       BIGINT COMMENT '关联凭证ID（关联 voucher 表，公益组织上传的凭证）',
    status           ENUM ('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING' COMMENT '审核状态：待审核、通过、拒绝',
    review_notes     TEXT COMMENT '审核备注或拒绝原因',
    reviewer_id      BIGINT COMMENT '审核者ID（管理员，关联 user 表，role=ADMIN）',
    timestamp        TIMESTAMP      NOT NULL COMMENT '链上时间戳',
    transaction_hash VARCHAR(66) COMMENT '区块链交易哈希，用于追溯链上记录',
    is_deleted       BOOLEAN                                  DEFAULT FALSE COMMENT '逻辑删除标志',
    created_at       TIMESTAMP                                DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    updated_at       TIMESTAMP                                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录最后更新时间',
    FOREIGN KEY (project_id) REFERENCES project (project_id),
    FOREIGN KEY (recipient_id) REFERENCES user (user_id),
    FOREIGN KEY (voucher_id) REFERENCES voucher (voucher_id),
    FOREIGN KEY (reviewer_id) REFERENCES user (user_id)
) COMMENT '资金流动记录表';