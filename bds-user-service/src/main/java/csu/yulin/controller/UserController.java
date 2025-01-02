package csu.yulin.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import csu.yulin.common.CommonResponse;
import csu.yulin.constants.RedisKeyConstants;
import csu.yulin.enums.ResultCode;
import csu.yulin.enums.RoleEnum;
import csu.yulin.model.convert.UserConverter;
import csu.yulin.model.dto.UserDTO;
import csu.yulin.model.entity.User;
import csu.yulin.model.vo.UserVO;
import csu.yulin.service.IUserService;
import csu.yulin.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

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

    private final RedisUtil redisUtil;

    private final SmsUtil smsUtil;

    private final AvatarUtil avatarUtil;

    @GetMapping("/test")
    public String test() {
        smsUtil.sendSms("15696389220");
        return "Hello, World!";
    }

    /**
     * 个体用户注册
     */
    @PostMapping("/register/individual")
    public CommonResponse<UserVO> registerByIndividual(@RequestBody UserDTO userDTO) {
        AssertUtil.hasText(userDTO.getPassword(), "密码不能为空");
        AssertUtil.hasText(userDTO.getPhoneNumber(), "电话号码不能为空");
        AssertUtil.hasText(userDTO.getCode(), "验证码不能为空");

        // 校验验证码是否则是否正确
        String smsRedisKey = RedisKeyConstants.SMS_CODE_PREFIX + userDTO.getPhoneNumber();
        String storedSmsCode = (String) redisUtil.get(smsRedisKey);
        AssertUtil.isTrue(StringUtils.hasText(storedSmsCode) && storedSmsCode.equals(userDTO.getCode()),
                ResultCode.BAD_REQUEST, "验证码错误或无效");
        redisUtil.delete(smsRedisKey);

        // 检查该个体用户是否已存在
        boolean isUserExist = userService.isUserExist(userDTO.getPhoneNumber());
        AssertUtil.isFalse(isUserExist, "用户已存在");

        // 保存用户
        User user = UserConverter.toEntity(userDTO);
        user.setPassword(MD5Util.encrypt(user.getPassword()));
        user.setAvatar(avatarUtil.getRandomAvatar());
        user.setRole(RoleEnum.INDIVIDUAL.getCode());
        boolean success = userService.save(user);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "用户创建失败");

        log.info("User registered successfully: phoneNumber={}, role={}", userDTO.getPhoneNumber(),
                RoleEnum.INDIVIDUAL.getCode());
        StpUtil.login(user.getUserId());
        UserVO vo = UserConverter.toVO(userService.getById(user.getUserId()));
        return CommonResponse.success(vo);
    }

    /**
     * 获取图形验证码
     */
    @GetMapping("/captcha")
    public CommonResponse<String> generateCaptcha() {
        // 生成验证码，宽度160，高度60，验证码字符长度4，干扰线数20
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(160, 60, 4, 20);
        String code = captcha.getCode();
        log.info("Generated Captcha: {}", code);

        // 将验证码存入 Redis，有效期5分钟
        String captchaKey = RedisKeyConstants.CAPTCHA_PREFIX + SnowflakeUtil.generateId();
        redisUtil.set(captchaKey, code, 5, TimeUnit.MINUTES);

        // 构造响应数据
        String base64Image = captcha.getImageBase64();
        String response = "{\"captchaKey\": \"" + captchaKey + "\", \"image\": \"" + base64Image + "\"}";

        return CommonResponse.success("验证码生成成功", response);
    }

    /**
     * 校验图形验证码
     */
    @PostMapping("/validateCaptcha")
    public CommonResponse<Void> validateCaptcha(@RequestParam("captchaKey") String captchaKey,
                                                @RequestParam("captcha") String captcha) {
        // 校验输入参数不为空
        AssertUtil.hasText(captchaKey, "验证码键不能为空");
        AssertUtil.hasText(captcha, "验证码不能为空");

        // 从 Redis 获取验证码
        String storedCaptcha = (String) redisUtil.get(captchaKey);

        // 校验 Redis 中是否存在对应验证码
        AssertUtil.notNull(storedCaptcha, ResultCode.NOT_FOUND, "验证码已过期或无效");

        // 校验验证码是否匹配
        AssertUtil.isTrue(storedCaptcha.equalsIgnoreCase(captcha), ResultCode.BAD_REQUEST, "验证码错误");

        // 验证成功后删除 Redis 中的验证码
        redisUtil.delete(captchaKey);

        // 返回成功响应
        return CommonResponse.success("验证码校验成功", null);
    }

    /**
     * 获取手机验证码
     */
    @GetMapping("/smsCode")
    public CommonResponse<Void> generateSmsCode(@RequestParam String phoneNumber) {
        // 校验手机号是否有效
        AssertUtil.hasText(phoneNumber, "手机号不能为空");

        // 生成短信验证码并发送
        smsUtil.sendSms(phoneNumber);

        // 返回成功响应
        return CommonResponse.success("短信验证码已发送", null);
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