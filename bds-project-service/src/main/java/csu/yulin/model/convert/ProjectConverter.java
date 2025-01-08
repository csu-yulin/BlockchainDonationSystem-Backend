package csu.yulin.model.convert;

import csu.yulin.model.dto.ProjectDTO;
import csu.yulin.model.entity.Project;
import csu.yulin.model.vo.ProjectVO;
import org.springframework.beans.BeanUtils;

/**
 * 项目实体与 DTO、VO 的转换工具类
 *
 * @author lp
 * @create 2025-01-08
 */
public class ProjectConverter {

    /**
     * 将 Project 实体转换为 ProjectDTO
     *
     * @param project 项目实体
     * @return 项目 DTO
     */
    public static ProjectDTO toDTO(Project project) {
        if (project == null) {
            return null;
        }
        ProjectDTO dto = new ProjectDTO();
        BeanUtils.copyProperties(project, dto);
        return dto;
    }

    /**
     * 将 ProjectDTO 转换为 Project 实体
     *
     * @param dto 项目 DTO
     * @return 项目实体
     */
    public static Project toEntity(ProjectDTO dto) {
        if (dto == null) {
            return null;
        }
        Project project = new Project();
        BeanUtils.copyProperties(dto, project);
        return project;
    }

    /**
     * 将 Project 实体转换为 ProjectVO
     *
     * @param project 项目实体
     * @return 项目 VO
     */
    public static ProjectVO toVO(Project project) {
        if (project == null) {
            return null;
        }
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(project, vo);
        return vo;
    }

    /**
     * 将 ProjectDTO 转换为 ProjectVO
     *
     * @param dto 项目 DTO
     * @return 项目 VO
     */
    public static ProjectVO toVO(ProjectDTO dto) {
        if (dto == null) {
            return null;
        }
        ProjectVO vo = new ProjectVO();
        BeanUtils.copyProperties(dto, vo);
        return vo;
    }
}
