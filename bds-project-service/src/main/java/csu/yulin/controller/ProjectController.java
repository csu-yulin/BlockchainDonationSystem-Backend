package csu.yulin.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import csu.yulin.common.CommonResponse;
import csu.yulin.common.PageDTO;
import csu.yulin.enums.ResultCode;
import csu.yulin.enums.RoleEnum;
import csu.yulin.feign.UserServiceFeignClient;
import csu.yulin.model.convert.ProjectConverter;
import csu.yulin.model.dto.ProjectDTO;
import csu.yulin.model.entity.Project;
import csu.yulin.model.vo.ProjectVO;
import csu.yulin.service.IProjectService;
import csu.yulin.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    /**
     * 新建项目
     */
    @PostMapping("/create")
    public CommonResponse<ProjectVO> createProject(@RequestBody ProjectDTO projectDTO) {
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
        Project project = ProjectConverter.toEntity(projectDTO);
        project.setCreatorId(userId);
        project.setCreatorRole(role);

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
        return CommonResponse.success("创建项目成功", projectVO);
    }

    /**
     * 更新项目
     */
    @PutMapping("/update")
    public CommonResponse<String> updateProject(@RequestBody ProjectDTO projectDTO) {
        Long userId = StpUtil.getLoginIdAsLong();
        String role = StpUtil.getSession().get("role").toString();

        // 检查项目是否存在
        Project existingProject = projectService.getById(projectDTO.getProjectId());
        AssertUtil.notNull(existingProject, ResultCode.NOT_FOUND, "项目不存在");

        // 检查是否有权限操作
        boolean isOwner = existingProject.getCreatorId().equals(userId);
        boolean isAdmin = RoleEnum.ADMIN.getCode().equals(role);
        AssertUtil.isTrue(isOwner || isAdmin, ResultCode.FORBIDDEN, "您无权操作该项目");

        // 更新项目信息
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
    public CommonResponse<ProjectVO> getProjectById(@PathVariable Long id) {
        // 查询项目详情
        Project project = projectService.getById(id);
        AssertUtil.notNull(project, ResultCode.NOT_FOUND, "项目不存在");

        // 转换为 VO 并返回
        ProjectVO projectVO = ProjectConverter.toVO(project);
        return CommonResponse.success("查询成功", projectVO);
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
}
