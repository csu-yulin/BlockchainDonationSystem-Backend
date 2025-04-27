package csu.yulin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import csu.yulin.common.PageDTO;
import csu.yulin.enums.ApprovalStatusEnum;
import csu.yulin.enums.ProjectStatusEnum;
import csu.yulin.mapper.ProjectMapper;
import csu.yulin.model.entity.Project;
import csu.yulin.model.vo.ProjectStatsVO;
import csu.yulin.service.IProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 项目表 服务实现类
 *
 * @author lp
 * @create 2025-01-07
 */
@Service
@Slf4j
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

    /**
     * 获取所有状态为 ACTIVE 且结束日期小于今天的项目
     *
     * @param today 今天的日期
     * @return 项目列表
     */
    @Override
    public List<Project> getActiveProjectsWithEndDateBefore(LocalDate today) {
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getStatus, ProjectStatusEnum.ACTIVE.getCode())
                .eq(Project::getApprovalStatus, ApprovalStatusEnum.APPROVED.getCode())
                .lt(Project::getEndDate, today);
        return list(queryWrapper);
    }

    /**
     * 根据条件查询项目列表
     *
     * @param pageDTO 分页查询 DTO
     * @return 项目列表（包含高亮结果）
     */
    @Override
    public Page<Project> getProjectsByConditions(PageDTO pageDTO) {
        // 打印查询条件
        log.info("查询条件: {}", pageDTO);

        // 创建分页对象
        Page<Project> page = new Page<>(pageDTO.getPage(), pageDTO.getSize());

        // 创建查询条件
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();

        // 按状态筛选
        if (StringUtils.hasText(pageDTO.getStatus())) {
            queryWrapper.eq(Project::getStatus, pageDTO.getStatus());
        }

        // 按审批状态筛选
        if (StringUtils.hasText(pageDTO.getApprovalStatus())) {
            queryWrapper.eq(Project::getApprovalStatus, pageDTO.getApprovalStatus());
        }

        // 按审批状态筛选
        if (pageDTO.getCreatorId() != null) {
            queryWrapper.eq(Project::getCreatorId, pageDTO.getCreatorId());
        }

        // 按创建者角色筛选
        if (StringUtils.hasText(pageDTO.getCreatorRole())) {
            queryWrapper.eq(Project::getCreatorRole, pageDTO.getCreatorRole());
        }

        // 按时间范围筛选
        if (pageDTO.getStartDate() != null) {
            queryWrapper.ge(Project::getStartDate, pageDTO.getStartDate());
        }
        if (pageDTO.getEndDate() != null) {
            queryWrapper.le(Project::getEndDate, pageDTO.getEndDate());
        }

        // 全局模糊搜索
        if (StringUtils.hasText(pageDTO.getKeyword())) {
            queryWrapper.and(wrapper ->
                    wrapper.like(Project::getProjectName, pageDTO.getKeyword())
                            .or()
                            .like(Project::getDescription, pageDTO.getKeyword())
                            .or()
                            .like(Project::getOrgName, pageDTO.getKeyword())
                            .or()
                            .like(Project::getContactPersonName, pageDTO.getKeyword())
            );
        }

        // 执行分页查询
        Page<Project> resultPage = this.page(page, queryWrapper);
        log.info("分页查询结果: total={}, pages={}", resultPage.getTotal(), resultPage.getPages());

        // 手动统计总数（临时方案）
        if (resultPage.getTotal() == 0) {
            long manualTotal = this.count(queryWrapper);
            resultPage.setTotal(manualTotal);
            resultPage.setPages((manualTotal + pageDTO.getSize() - 1) / pageDTO.getSize());
            System.out.println("修正后: total=" + resultPage.getTotal() + ", pages=" + resultPage.getPages());
        }

        // 处理高亮（手动添加高亮字段）
        List<Project> highlightedProjects = resultPage.getRecords().stream().peek(project -> {
            String keyword = pageDTO.getKeyword();
            if (StringUtils.hasText(keyword)) {
                if (project.getProjectName().contains(keyword)) {
                    project.setProjectName(project.getProjectName().replace(keyword, "<em>" + keyword + "</em>"));
                }
                if (project.getDescription() != null && project.getDescription().contains(keyword)) {
                    project.setDescription(project.getDescription().replace(keyword, "<em>" + keyword + "</em>"));
                }
                if (project.getOrgName() != null && project.getOrgName().contains(keyword)) {
                    project.setOrgName(project.getOrgName().replace(keyword, "<em>" + keyword + "</em>"));
                }
                if (project.getContactPersonName() != null && project.getContactPersonName().contains(keyword)) {
                    project.setContactPersonName(project.getContactPersonName().replace(keyword, "<em>" + keyword + "</em>"));
                }
            }
        }).collect(Collectors.toList());

        // 将高亮后的结果设置回分页对象
        resultPage.setRecords(highlightedProjects);

        return resultPage;
    }

    @Override
    public ProjectStatsVO getProjectStatsByOrgId(Long orgId) {
        ProjectStatsVO stats = new ProjectStatsVO();

        // 查询项目总数
        LambdaQueryWrapper<Project> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(Project::getCreatorId, orgId);
        Long projectCount = this.count(countWrapper);
        stats.setProjectCount(projectCount);

        // 查询目标总金额和已筹集总金额
        LambdaQueryWrapper<Project> sumWrapper = new LambdaQueryWrapper<>();
        sumWrapper.eq(Project::getCreatorId, orgId);
        List<Project> projects = this.list(sumWrapper);

        BigDecimal totalTargetAmount = BigDecimal.ZERO;
        BigDecimal totalRaisedAmount = BigDecimal.ZERO;

        for (Project project : projects) {
            totalTargetAmount = totalTargetAmount.add(
                    Optional.ofNullable(project.getTargetAmount()).orElse(BigDecimal.ZERO)
            );
            totalRaisedAmount = totalRaisedAmount.add(
                    Optional.ofNullable(project.getRaisedAmount()).orElse(BigDecimal.ZERO)
            );
        }

        stats.setTotalTargetAmount(totalTargetAmount);
        stats.setTotalRaisedAmount(totalRaisedAmount);

        return stats;
    }

}
