package csu.yulin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import csu.yulin.common.PageDTO;
import csu.yulin.model.entity.Project;
import csu.yulin.model.vo.ProjectStatsVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 项目表 服务类
 *
 * @author lp
 * @create 2025-01-07
 */
public interface IProjectService extends IService<Project> {

    /**
     * 获取所有状态为 ACTIVE 且结束日期小于今天的项目
     *
     * @param today 今天的日期
     * @return 项目列表
     */
    List<Project> getActiveProjectsWithEndDateBefore(LocalDate today);

    /**
     * 根据条件查询项目列表
     *
     * @param pageDTO 分页查询 DTO
     * @return 项目列表
     */
    Page<Project> getProjectsByConditions(PageDTO pageDTO);

    ProjectStatsVO getProjectStatsByOrgId(Long orgId);
}
