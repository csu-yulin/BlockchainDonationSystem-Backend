package csu.yulin.config;

import cn.dev33.satoken.stp.StpInterface;
import csu.yulin.enums.RoleEnum;
import csu.yulin.model.entity.User;
import csu.yulin.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限验证扩展
 *
 * @author lp
 * @create 2025-01-04
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final IUserService userService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> permissions = new ArrayList<>();

        // 获取用户信息
        User user = userService.getById((String) loginId);

        // 判断角色
        if (RoleEnum.ADMIN.getCode().equals(user.getRole())) {
            // 添加基础管理员权限
            permissions.add("admin:basic");

            // 如果是高级管理员，添加额外权限
            if (user.getAdminLevel() != null && user.getAdminLevel() >= 2) {
                permissions.add("admin:approve");
            }
        }

        return permissions;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roles = new ArrayList<>();

        // 获取用户信息
        User user = userService.getById((String) loginId);

        // 添加角色到列表
        roles.add(user.getRole());

        return roles;
    }
}
