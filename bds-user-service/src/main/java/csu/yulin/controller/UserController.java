package csu.yulin.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import csu.yulin.common.CommonResponse;
import csu.yulin.enums.ResultCode;
import csu.yulin.model.entity.User;
import csu.yulin.service.IUserService;
import csu.yulin.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author lp
 * @create 2024-12-30
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping("/test")
    public SaResult test() {
        StpUtil.login(10001);
        return SaResult.ok("登录成功");
    }

    /**
     * 用户注册
     */
    @PostMapping
    public CommonResponse<User> register(@RequestBody User user) {
        // 参数校验
        AssertUtil.hasText(user.getUsername(), "用户名不能为空");
        AssertUtil.hasText(user.getPassword(), "密码不能为空");

        // 检查用户名是否已存在
        long count = userService.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, user.getUsername()));
        AssertUtil.isTrue(count == 0, "用户名已存在");

        // 保存用户
        boolean success = userService.save(user);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "用户创建失败");

        return CommonResponse.success(user);
    }

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详细信息
     */
    @GetMapping("/{userId}")
    public CommonResponse<User> getUserById(@PathVariable Long userId) {
        User user = userService.getById(userId);
        AssertUtil.notNull(user, ResultCode.NOT_FOUND, "用户不存在");
        return CommonResponse.success(user);
    }

    /**
     * 分页查询用户列表
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页用户列表
     */
    @GetMapping
    public CommonResponse<Page<User>> listUsers(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size) {
        Page<User> userPage = userService.page(new Page<>(page, size));
        return CommonResponse.success(userPage);
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param user   更新的用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/{userId}")
    public CommonResponse<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        // 检查用户是否存在
        User existingUser = userService.getById(userId);
        AssertUtil.notNull(existingUser, ResultCode.NOT_FOUND, "用户不存在");

        // 设置用户ID
        user.setUserId(userId);

        // 更新用户信息
        boolean success = userService.updateById(user);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "用户更新失败");

        return CommonResponse.success(user);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{userId}")
    public CommonResponse<Void> deleteUser(@PathVariable Long userId) {
        // 检查用户是否存在
        User existingUser = userService.getById(userId);
        AssertUtil.notNull(existingUser, ResultCode.NOT_FOUND, "用户不存在");

        // 删除用户
        boolean success = userService.removeById(userId);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "用户删除失败");

        return CommonResponse.success(null);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/search")
    public CommonResponse<User> getUserByUsername(@RequestParam String username) {
        AssertUtil.hasText(username, "用户名不能为空");

        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        AssertUtil.notNull(user, ResultCode.NOT_FOUND, "用户不存在");

        return CommonResponse.success(user);
    }
}