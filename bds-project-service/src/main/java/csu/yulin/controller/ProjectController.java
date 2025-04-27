package csu.yulin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import csu.yulin.common.CommonResponse;
import csu.yulin.common.PageDTO;
import csu.yulin.enums.ProjectStatusEnum;
import csu.yulin.enums.ResultCode;
import csu.yulin.enums.RoleEnum;
import csu.yulin.exception.BusinessException;
import csu.yulin.feign.BlockchainServiceFeignClient;
import csu.yulin.feign.UserServiceFeignClient;
import csu.yulin.model.convert.ProjectConverter;
import csu.yulin.model.dto.CharityDonationCreateProjectInputDTO;
import csu.yulin.model.dto.ProjectDTO;
import csu.yulin.model.entity.DonationRecord;
import csu.yulin.model.entity.Project;
import csu.yulin.model.vo.ProjectStatsVO;
import csu.yulin.model.vo.ProjectVO;
import csu.yulin.service.IProjectService;
import csu.yulin.util.AssertUtil;
import csu.yulin.util.OSSUtil;
import csu.yulin.util.SnowflakeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * 项目表 前端控制器
 *
 * @author lp
 * @create 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final IProjectService projectService;

    private final UserServiceFeignClient userServiceFeignClient;

    private final BlockchainServiceFeignClient blockchainServiceFeignClient;

    private final ObjectMapper objectMapper;

    private final OSSUtil ossUtil;

    /**
     * 新建项目
     */
    @PostMapping("/create")
    public CommonResponse<Project> createProject(@RequestBody ProjectDTO projectDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        String role = StpUtil.getSession().get("role").toString();

        // 调用用户服务检查用户是否有资格创建项目
        CommonResponse<Map<String, Object>> userCheckResponse = userServiceFeignClient.canCreateProject(userId);

        // 检查 Feign 调用是否成功
        AssertUtil.isTrue(userCheckResponse.getCode() == ResultCode.SUCCESS.getCode(),
                ResultCode.INTERNAL_SERVER_ERROR, "创建项目失败，请稍后重试");

        // 获取用户检查结果
        Map<String, Object> responseData = userCheckResponse.getData();
        AssertUtil.notNull(responseData, "用户服务返回数据为空");

        Boolean canCreate = (Boolean) responseData.get("canCreate");
        String reason = (String) responseData.get("reason");

        // 检查用户是否有资格创建项目
        AssertUtil.isTrue(Boolean.TRUE.equals(canCreate), ResultCode.FORBIDDEN, reason);

        // 创建并保存项目
        CharityDonationCreateProjectInputDTO charityDonationCreateProjectInputDTO = new CharityDonationCreateProjectInputDTO();
        charityDonationCreateProjectInputDTO.setOrgId(BigInteger.valueOf(userId));
        charityDonationCreateProjectInputDTO.setTargetAmount(projectDTO.getTargetAmount().toBigInteger());
        Map<String, Object> transactionResponse = blockchainServiceFeignClient.createProject(charityDonationCreateProjectInputDTO).getData();
        AssertUtil.notNull(transactionResponse, ResultCode.INTERNAL_SERVER_ERROR, "区块链服务响应为空");

        Project project = ProjectConverter.toEntity(projectDTO);
        project.setCreatorId(userId);
        project.setCreatorRole(role);
        project.setTransactionHash(transactionResponse.get("transactionHash").toString());
        Integer projectIdInteger = (Integer) transactionResponse.get("projectId");
        project.setProjectId(projectIdInteger.longValue());


        // 设置组织或个人的额外信息
        String orgName = (String) responseData.get("orgName");
        String contactPersonName = (String) responseData.get("contactPersonName");
        if (RoleEnum.ORGANIZATION.getCode().equals(role)) {
            project.setOrgName(orgName);
        }
        project.setContactPersonName(contactPersonName);

        boolean saved = projectService.save(project);
        AssertUtil.isTrue(saved, ResultCode.INTERNAL_SERVER_ERROR, "创建项目失败，请稍后重试");

        // 返回成功信息
        project = projectService.getById(project.getProjectId());
        ProjectVO projectVO = ProjectConverter.toVO(project);
        log.info("项目创建成功，项目ID：{}", project.getProjectId());
        return CommonResponse.success("创建项目成功", project);
    }

    /**
     * 更新项目
     */
    @PutMapping("/update")
    public CommonResponse<String> updateProject(@RequestBody ProjectDTO projectDTO) throws JsonProcessingException {
        Long userId = StpUtil.getLoginIdAsLong();
        String role = StpUtil.getSession().get("role").toString();

        // 检查项目是否存在
        Project existingProject = projectService.getById(projectDTO.getProjectId());
        AssertUtil.notNull(existingProject, ResultCode.NOT_FOUND, "项目不存在");

        // 检查是否有权限操作
        boolean isOwner = existingProject.getCreatorId().equals(userId);
        boolean isAdmin = RoleEnum.ADMIN.getCode().equals(role);
//        AssertUtil.isTrue(isOwner || isAdmin, ResultCode.FORBIDDEN, "您无权操作该项目");

        // 更新项目信息
        if (StringUtils.isNotBlank(projectDTO.getDonationRecords())) {
            // 原 donation_history（可能为 null）
            String oldHistoryJson = existingProject.getDonationRecords();
            List<DonationRecord> historyList = new ArrayList<>();
            if (StringUtils.isNotBlank(oldHistoryJson)) {
                historyList = objectMapper.readValue(oldHistoryJson, new TypeReference<>() {
                });
            }

            // 追加新记录（反序列化 DTO 中的 donationHistory）
            DonationRecord newRecord = objectMapper.readValue(projectDTO.getDonationRecords(), DonationRecord.class);
            historyList.add(newRecord);

            // 重新序列化并设置
            projectDTO.setDonationRecords(objectMapper.writeValueAsString(historyList));

            BigDecimal oldRaisedAmount = Optional.ofNullable(existingProject.getRaisedAmount()).orElse(BigDecimal.ZERO);
            BigDecimal newAmount = Optional.ofNullable(projectDTO.getRaisedAmount()).orElse(BigDecimal.ZERO);

            BigDecimal totalAmount = oldRaisedAmount.add(newAmount);
            projectDTO.setRaisedAmount(totalAmount);

            BigDecimal targetAmount = Optional.ofNullable(existingProject.getTargetAmount()).orElse(BigDecimal.ZERO);

            // 如果达到目标金额，则设置状态为已完成
            if (totalAmount.compareTo(targetAmount) >= 0 && targetAmount.compareTo(BigDecimal.ZERO) > 0) {
                projectDTO.setStatus(ProjectStatusEnum.COMPLETED.getCode());
            }
        }

        Project updatedProject = ProjectConverter.toEntity(projectDTO);
        // 如果是管理员，修改的只能是项目状态，即审批
        if (updatedProject.getApprovalStatus() != null) {
            updatedProject.setVerifierId(userId);
        }
        boolean updated = projectService.updateById(updatedProject);
        AssertUtil.isTrue(updated, ResultCode.INTERNAL_SERVER_ERROR, "更新项目失败，请稍后重试");

        log.info("项目更新成功，项目ID：{}", updatedProject.getProjectId());
        return CommonResponse.success("项目更新成功");
    }

    /**
     * 查询单个项目详情
     */
    @GetMapping("/{id}")
    public CommonResponse<Project> getProjectById(@PathVariable Long id) {
        // 查询项目详情
        Project project = projectService.getById(id);
        AssertUtil.notNull(project, ResultCode.NOT_FOUND, "项目不存在");

        // 转换为 VO 并返回
        ProjectVO projectVO = ProjectConverter.toVO(project);
        return CommonResponse.success("查询成功", project);
    }


    /**
     * 分页查询项目列表
     */
    @PostMapping("/list")
    public CommonResponse<Page<Project>> listProjects(@RequestBody PageDTO pageDTO) {
        log.info("Querying project by conditions: {}", pageDTO);
        Page<Project> projectPage = projectService.getProjectsByConditions(pageDTO);
        return CommonResponse.success(projectPage);
    }

    /**
     * 统计项目总数
     */
    @GetMapping("/count")
    public CommonResponse<Long> getProjectCount() {
        Long count = projectService.count();
        return CommonResponse.success("项目总数查询成功", count);
    }

    /**
     * 根据组织ID统计项目信息
     * 返回项目总数、目标总金额、已筹集总金额
     */
    @GetMapping("/stats/{orgId}")
    public CommonResponse<ProjectStatsVO> getProjectStatsByOrgId(@PathVariable Long orgId) {
        // 验证 orgId 是否有效
        AssertUtil.isTrue(orgId != null && orgId > 0, ResultCode.BAD_REQUEST, "无效的组织ID");

        // 查询统计数据
        ProjectStatsVO stats = projectService.getProjectStatsByOrgId(orgId);

        log.info("组织项目统计查询成功，orgId：{}", orgId);
        return CommonResponse.success("组织项目统计查询成功", stats);
    }

    /**
     * 上传项目封面图片
     */
    @PostMapping("/cover")
    public CommonResponse<String> uploadProjectCover(@RequestParam("cover") MultipartFile file) {
        // 参数校验
        Objects.requireNonNull(file, "上传文件不能为空");

        try {
            // 上传新封面
            String newFileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
            String newCoverUrl = ossUtil.uploadAvatar(newFileName, file.getInputStream());

            return CommonResponse.success("封面上传成功", newCoverUrl);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "封面上传失败：" + e.getMessage());
        }
    }


    /**
     * 生成唯一文件名
     *
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFileName(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return SnowflakeUtil.generateId() + fileExtension;
    }

    @GetMapping("/creator/{creatorId}")
    public CommonResponse<List<Project>> getProjectsByCreatorId(@PathVariable("creatorId") Long creatorId) {
        // 校验 creatorId 是否有效
        AssertUtil.notNull(creatorId, "创建者ID不能为空");
        AssertUtil.isTrue(creatorId > 0, ResultCode.BAD_REQUEST, "无效的创建者ID");

        // 查询该创建者创建的所有项目
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getCreatorId, creatorId);
        List<Project> projects = projectService.list(queryWrapper);

        // 日志记录
        log.info("查询创建者ID={} 的项目成功，项目数量：{}", creatorId, projects.size());

        // 返回项目列表
        return CommonResponse.success("查询项目成功", projects);
    }

    /**
     * 随机获取三个项目用于首页展示
     */
    @GetMapping("/random")
    public CommonResponse<List<Project>> getRandomProjects() {
        // 查询所有已批准且未完成的项目
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        // TODO:后面可优化
//        queryWrapper.eq(Project::getApprovalStatus, "APPROVED")
//                .ne(Project::getStatus, ProjectStatusEnum.COMPLETED.getCode());

        List<Project> allProjects = projectService.list(queryWrapper);

        // 如果项目少于3个，返回所有项目
        if (allProjects.size() <= 3) {
            log.info("随机项目查询成功，返回项目数量：{}", allProjects.size());
            return CommonResponse.success("随机项目查询成功", allProjects);
        }

        // 随机选择三个项目
        Collections.shuffle(allProjects);
        List<Project> randomProjects = allProjects.subList(0, 3);

        log.info("随机项目查询成功，返回项目数量：{}", randomProjects.size());
        return CommonResponse.success("随机项目查询成功", randomProjects);
    }
}
