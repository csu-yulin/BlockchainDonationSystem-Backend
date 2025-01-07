package csu.yulin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import csu.yulin.mapper.ProjectMapper;
import csu.yulin.model.entity.Project;
import csu.yulin.service.IProjectService;
import org.springframework.stereotype.Service;

/**
 * 项目表 服务实现类
 *
 * @author lp
 * @create 2025-01-07
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements IProjectService {

}
