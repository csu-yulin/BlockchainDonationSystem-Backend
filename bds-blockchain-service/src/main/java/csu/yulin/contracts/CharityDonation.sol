// SPDX-License-Identifier: MIT
pragma solidity >=0.4.24 <0.6.11;

// 公益捐赠透明化管理智能合约
contract CharityDonation {
    // 项目结构体，存储公益项目的基本信息
    struct Project {
        uint256 projectId;       // 项目ID，唯一标识符
        uint256 orgId;           // 公益组织ID，与链下数据库关联
        uint256 targetAmount;    // 目标募集金额
        uint256 raisedAmount;    // 已募集金额
    }

    // 捐款结构体，记录每次捐款的详细信息
    struct Donation {
        uint256 donationId;      // 捐款ID，唯一标识符
        uint256 userId;          // 用户ID，与链下数据库关联
        uint256 projectId;       // 项目ID，关联的项目
        uint256 amount;          // 捐款金额
        uint256 timestamp;       // 捐款时间戳
    }

    // 凭证结构体，记录公益组织上传的凭证信息
    struct Voucher {
        uint256 voucherId;       // 凭证ID，唯一标识符
        uint256 projectId;       // 项目ID，关联的项目
        uint256 orgId;           // 公益组织ID，与链下数据库关联
        bytes32 ipfsHash;        // 凭证文件的IPFS哈希，用于链下文件索引
        uint256 timestamp;       // 上传时间戳
    }

    // 存储映射
    mapping(uint256 => Project) public projects;           // 项目ID到项目的映射
    mapping(uint256 => Donation) public donations;         // 捐款ID到捐款的映射
    mapping(uint256 => uint256[]) public projectDonations; // 项目ID到捐款ID列表的映射
    mapping(uint256 => Voucher) public vouchers;           // 凭证ID到凭证的映射
    mapping(uint256 => uint256[]) public projectVouchers;  // 项目ID到凭证ID列表的映射

    // 计数器，用于生成唯一ID
    uint256 public projectCount;   // 项目总数
    uint256 public donationCount;  // 捐款总数
    uint256 public voucherCount;   // 凭证总数

    // 事件，用于记录关键操作，便于链下监听
    event ProjectCreated(uint256 projectId, uint256 orgId, uint256 targetAmount); // 项目创建事件
    event Donated(uint256 donationId, uint256 userId, uint256 projectId, uint256 amount, uint256 timestamp); // 捐款记录事件
    event VoucherUploaded(uint256 voucherId, uint256 projectId, uint256 orgId, bytes32 ipfsHash, uint256 timestamp); // 凭证上传事件

    // 构造函数，初始化计数器
    constructor() public {
        projectCount = 0;
        donationCount = 0;
        voucherCount = 0;
    }

    // 创建新公益项目
    // 参数：
    // - orgId: 公益组织ID
    // - targetAmount: 目标募集金额
    function createProject(uint256 orgId, uint256 targetAmount) external {
        projectCount++; // 自增项目ID
        projects[projectCount] = Project(projectCount, orgId, targetAmount, 0); // 初始化项目信息
        emit ProjectCreated(projectCount, orgId, targetAmount); // 触发项目创建事件
    }

    // 记录用户捐款
    // 参数：
    // - userId: 用户ID
    // - projectId: 项目ID
    // - amount: 捐款金额
    function donate(uint256 userId, uint256 projectId, uint256 amount) external {
        donationCount++; // 自增捐款ID
        donations[donationCount] = Donation(donationCount, userId, projectId, amount, block.timestamp); // 记录捐款信息
        projects[projectId].raisedAmount += amount; // 更新项目已募集金额
        projectDonations[projectId].push(donationCount); // 将捐款ID加入项目捐款列表
        emit Donated(donationCount, userId, projectId, amount, block.timestamp); // 触发捐款事件
    }

    // 上传凭证文件信息
    // 参数：
    // - projectId: 项目ID
    // - orgId: 公益组织ID
    // - ipfsHash: 凭证文件的IPFS哈希
    function uploadVoucher(uint256 projectId, uint256 orgId, bytes32 ipfsHash) external {
        voucherCount++; // 自增凭证ID
        vouchers[voucherCount] = Voucher(voucherCount, projectId, orgId, ipfsHash, block.timestamp); // 记录凭证信息
        projectVouchers[projectId].push(voucherCount); // 将凭证ID加入项目凭证列表
        emit VoucherUploaded(voucherCount, projectId, orgId, ipfsHash, block.timestamp); // 触发凭证上传事件
    }

    // 查询项目信息
    // 参数：
    // - projectId: 项目ID
    // 返回：项目ID、组织ID、目标金额、已募集金额
    function getProject(uint256 projectId)
    external
    view
    returns (uint256, uint256, uint256, uint256)
    {
        Project memory project = projects[projectId];
        return (project.projectId, project.orgId, project.targetAmount, project.raisedAmount);
    }

    // 查询捐款信息
    // 参数：
    // - donationId: 捐款ID
    // 返回：捐款ID、用户ID、项目ID、金额、时间戳
    function getDonation(uint256 donationId)
    external
    view
    returns (uint256, uint256, uint256, uint256, uint256)
    {
        Donation memory donation = donations[donationId];
        return (donation.donationId, donation.userId, donation.projectId, donation.amount, donation.timestamp);
    }

    // 查询凭证信息
    // 参数：
    // - voucherId: 凭证ID
    // 返回：凭证ID、项目ID、组织ID、IPFS哈希、时间戳
    function getVoucher(uint256 voucherId)
    external
    view
    returns (uint256, uint256, uint256, bytes32, uint256)
    {
        Voucher memory voucher = vouchers[voucherId];
        return (voucher.voucherId, voucher.projectId, voucher.orgId, voucher.ipfsHash, voucher.timestamp);
    }

    // 查询项目的捐款ID列表
    // 参数：
    // - projectId: 项目ID
    // 返回：捐款ID数组
    function getProjectDonations(uint256 projectId) external view returns (uint256[]) {
        return projectDonations[projectId];
    }

    // 查询项目的凭证ID列表
    // 参数：
    // - projectId: 项目ID
    // 返回：凭证ID数组
    function getProjectVouchers(uint256 projectId) external view returns (uint256[]) {
        return projectVouchers[projectId];
    }
}