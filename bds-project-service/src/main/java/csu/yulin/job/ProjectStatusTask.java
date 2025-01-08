package csu.yulin.job;

import csu.yulin.enums.ProjectStatusEnum;
import csu.yulin.model.entity.Project;
import csu.yulin.service.IProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * 项目状态检查定时任务
 *
 * @author lp
 * @create 2025-01-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectStatusTask {

    private final IProjectService projectService;

    /**
     * 每天凌晨 0 点执行定时任务
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndUpdateExpiredProjects() {
        log.info("开始检查过期项目状态...");

        // 获取所有状态为 ACTIVE 且结束日期小于今天的项目
        LocalDate today = LocalDate.now();
        List<Project> expiredProjects = projectService.getActiveProjectsWithEndDateBefore(today);

        if (expiredProjects.isEmpty()) {
            log.info("没有需要更新状态的过期项目。");
            return;
        }

        // 更新项目状态为 EXPIRED
        for (Project project : expiredProjects) {
            project.setStatus(ProjectStatusEnum.EXPIRED.getCode());
            projectService.updateById(project);
            log.info("项目 ID：{} 状态已更新为 EXPIRED", project.getProjectId());
        }

        log.info("过期项目状态检查完成，总计更新 {} 个项目。", expiredProjects.size());
    }
}
