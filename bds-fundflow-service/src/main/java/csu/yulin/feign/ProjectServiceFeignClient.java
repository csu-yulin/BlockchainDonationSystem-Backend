package csu.yulin.feign;

import csu.yulin.common.CommonResponse;
import csu.yulin.model.dto.ProjectDTO;
import csu.yulin.model.entity.Project;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 用户服务feign客户端
 *
 * @author lp
 * @create 2025-01-08
 */
@FeignClient(name = "bds-project-service")
public interface ProjectServiceFeignClient {

    /**
     * 查询单个项目详情
     */
    @GetMapping("/project/{id}")
    CommonResponse<Project> getProjectById(@PathVariable Long id);

    @PutMapping("/project/update")
    CommonResponse<String> updateProject(@RequestBody ProjectDTO projectDTO);

    @GetMapping("/project/creator/{creatorId}")
    CommonResponse<List<Project>> getProjectsByCreatorId(@PathVariable("creatorId") Long creatorId);
}
