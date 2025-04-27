package csu.yulin.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import csu.yulin.common.CommonResponse;
import csu.yulin.common.PageDTO;
import csu.yulin.constants.RedisKeyConstants;
import csu.yulin.enums.CertificationStatusEnum;
import csu.yulin.enums.ResultCode;
import csu.yulin.enums.RoleEnum;
import csu.yulin.enums.UserStatusEnum;
import csu.yulin.exception.BusinessException;
import csu.yulin.model.convert.UserConverter;
import csu.yulin.model.dto.OrganizationDTO;
import csu.yulin.model.dto.UserDTO;
import csu.yulin.model.entity.AssistanceHistoryRecord;
import csu.yulin.model.entity.DonationHistoryRecord;
import csu.yulin.model.entity.User;
import csu.yulin.model.vo.OrganizationVO;
import csu.yulin.model.vo.UserVO;
import csu.yulin.service.IUserService;
import csu.yulin.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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

    private final IdentityVerificationUtil identityVerificationUtil;

    private final OSSUtil ossUtil;

    private final ObjectMapper objectMapper;

    /**
     * 上传用户头像
     */
    @PostMapping("/{userId}/avatar")
    public CommonResponse<String> uploadAvatar(@PathVariable Long userId, @RequestParam("avatar") MultipartFile file) {
        // 检查用户是否存在
        User user = userService.getById(userId);
        AssertUtil.notNull(user, "用户不存在");

        // 获取当前头像信息
        String oldAvatarUrl = user.getAvatar();

        // 删除旧头像
//        if (StringUtils.hasText(oldAvatarUrl)) {
//            try {
//                String oldAvatarName = extractFileNameFromUrl(oldAvatarUrl);
//                ossUtil.deleteAvatar(oldAvatarName);
//            } catch (Exception e) {
//                // 记录日志但不阻止流程
//                log.warn("旧头像删除失败或不存在: {}", e.getMessage());
//            }
//        }

        // 上传新头像
        try {
            String newFileName = generateUniqueFileName(Objects.requireNonNull(file.getOriginalFilename()));
            String newAvatarUrl = ossUtil.uploadAvatar(newFileName, file.getInputStream());

            // 更新用户头像信息
            user.setAvatar(newAvatarUrl);
            boolean success = userService.updateById(user);
            AssertUtil.isTrue(success, "更新用户头像失败");

            return CommonResponse.success("头像上传成功", newAvatarUrl);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "头像上传失败");
        }
    }

    /**
     * 从头像 URL 中提取文件名
     *
     * @param url 文件 URL
     * @return 文件名
     */
    private String extractFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 生成唯一文件名
     *
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    private String generateUniqueFileName(String originalFileName) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return SnowflakeUtil.generateId() + fileExtension;
    }

    /**
     * 用户手机号和密码登录接口
     */
    @PostMapping("/login/phone")
    public CommonResponse<Object> loginByPhone(@RequestBody UserDTO userDTO) {
        // 校验手机号和密码是否为空
        AssertUtil.hasText(userDTO.getPhoneNumber(), "手机号不能为空");
        AssertUtil.hasText(userDTO.getPassword(), "密码不能为空");

        // 根据手机号查询用户信息
        User user = userService.getUserByPhoneNumber(userDTO.getPhoneNumber());
        AssertUtil.notNull(user, ResultCode.NOT_FOUND, "用户不存在");

        // 校验密码
        boolean isPasswordMatch = MD5Util.encrypt(userDTO.getPassword()).equals(user.getPassword());
        AssertUtil.isTrue(isPasswordMatch, ResultCode.UNAUTHORIZED, "密码错误");

        // 校验用户状态
        AssertUtil.isTrue("ACTIVE".equals(user.getStatus()), ResultCode.FORBIDDEN, "用户已被禁用，请联系管理员");

        // 登录成功，生成会话
        StpUtil.login(user.getUserId());

        StpUtil.getSession().set("role", user.getRole());
        log.info("User logged in successfully: phoneNumber={}", userDTO.getPhoneNumber());

        // 构造返回的用户信息
        Object result;
        if (RoleEnum.ORGANIZATION.getCode().equals(user.getRole())) {
            result = UserConverter.toOrganizationVO(user);
        } else {
            result = UserConverter.toUserVO(user);
        }
        return CommonResponse.success("登录成功", result);
    }

    /**
     * 公益组织邮箱和密码登录接口
     */
    @PostMapping("/login/email")
    public CommonResponse<Object> loginByEmail(@RequestBody UserDTO userDTO) {
        // 校验邮箱和密码是否为空
        AssertUtil.hasText(userDTO.getEmail(), "邮箱不能为空");
        AssertUtil.hasText(userDTO.getPassword(), "密码不能为空");

        // 根据邮箱查询公益组织信息
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, userDTO.getEmail()));
        AssertUtil.notNull(user, ResultCode.NOT_FOUND, "公益组织不存在");

        // 校验密码
        boolean isPasswordMatch = MD5Util.encrypt(userDTO.getPassword()).equals(user.getPassword());
        AssertUtil.isTrue(isPasswordMatch, ResultCode.UNAUTHORIZED, "密码错误");

        // 校验用户状态
        AssertUtil.isTrue("ACTIVE".equals(user.getStatus()), ResultCode.FORBIDDEN, "公益组织已被禁用，请联系管理员");

        // 登录成功，生成会话
        StpUtil.login(user.getUserId());

        StpUtil.getSession().set("role", user.getRole());
        log.info("User logged in successfully: phoneNumber={}", userDTO.getPhoneNumber());

        // 构造返回的用户信息
        Object result;
        if (RoleEnum.ORGANIZATION.getCode().equals(user.getRole())) {
            result = UserConverter.toOrganizationVO(user);
        } else {
            result = UserConverter.toUserVO(user);
        }
        return CommonResponse.success("登录成功", user);
    }

    /**
     * 个体用户注册
     */
    @PostMapping("/register/individual")
    public CommonResponse<User> registerByIndividual(@RequestBody UserDTO userDTO) {
        AssertUtil.hasText(userDTO.getPassword(), "密码不能为空");
        AssertUtil.hasText(userDTO.getPhoneNumber(), "电话号码不能为空");
        AssertUtil.hasText(userDTO.getCode(), "验证码不能为空");

        // 校验验证码是否则是否正确
        String smsRedisKey = RedisKeyConstants.SMS_CODE_PREFIX + userDTO.getPhoneNumber();
        String storedSmsCode = (String) redisUtil.get(smsRedisKey);
        AssertUtil.isTrue(StringUtils.isNotBlank(storedSmsCode) && storedSmsCode.equals(userDTO.getCode()),
                ResultCode.BAD_REQUEST, "验证码错误或无效");
        redisUtil.delete(smsRedisKey);

        // 检查该个体用户是否已存在
        boolean isUserExist = userService.isUserExistByPhoneNumber(userDTO.getPhoneNumber());
        AssertUtil.isFalse(isUserExist, "用户已存在");

        // 保存用户
        User user = UserConverter.toEntity(userDTO);
        user.setPassword(MD5Util.encrypt(user.getPassword()));
        user.setAvatar(avatarUtil.getRandomAvatar());
        user.setRole(RoleEnum.INDIVIDUAL.getCode());
        // 默认为未激活状态,因为需要进行身份认证
        user.setStatus(UserStatusEnum.INACTIVE.getCode());
        boolean success = userService.save(user);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "用户创建失败");

        log.info("User registered successfully: phoneNumber={}, role={}", userDTO.getPhoneNumber(),
                RoleEnum.INDIVIDUAL.getCode());
        StpUtil.login(user.getUserId());
        StpUtil.getSession().set("role", user.getRole());
        UserVO vo = UserConverter.toUserVO(userService.getById(user.getUserId()));
        return CommonResponse.success(userService.getById(user.getUserId()));
    }

    /**
     * 公益组织用户注册接口
     */
    @PostMapping("/register/organization")
    public CommonResponse<OrganizationVO> registerOrganization(@RequestBody OrganizationDTO orgDTO) {
        AssertUtil.hasText(orgDTO.getOrgName(), "组织名称不能为空");
        AssertUtil.hasText(orgDTO.getOrgLicenseNumber(), "组织注册号或营业执照编号不能为空");
        AssertUtil.hasText(orgDTO.getOrgBankAccount(), "组织银行账户信息不能为空");
        AssertUtil.hasText(orgDTO.getContactPersonName(), "联系人姓名不能为空");
        AssertUtil.hasText(orgDTO.getPhoneNumber(), "联系人手机号不能为空");
        AssertUtil.hasText(orgDTO.getEmail(), "组织邮箱地址不能为空");
        AssertUtil.hasText(orgDTO.getPassword(), "密码不能为空");

        // 检查手机号是否已被注册
        boolean isPhoneExist = userService.isUserExistByPhoneNumber(orgDTO.getPhoneNumber());
        AssertUtil.isFalse(isPhoneExist, "该手机号已被注册");

        // 保存组织用户信息
        User user = UserConverter.toEntity(orgDTO);
        user.setPassword(MD5Util.encrypt(user.getPassword()));
        user.setAvatar(avatarUtil.getRandomAvatar());
        user.setRole(RoleEnum.ORGANIZATION.getCode());
        // 默认待认证状态
        user.setCertificationStatus(CertificationStatusEnum.PENDING.getCode());

        boolean success = userService.save(user);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "组织用户注册失败");
        StpUtil.login(user.getUserId());
        StpUtil.getSession().set("role", user.getRole());
        log.info("Organization registered successfully: orgName={}, phoneNumber={}",
                orgDTO.getOrgName(), orgDTO.getPhoneNumber());

        // 返回用户 VO 数据
        OrganizationVO vo = UserConverter.toOrganizationVO(userService.getById(user.getUserId()));
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
     * 身份认证接口
     */
    @PostMapping("/individualVerify")
    public CommonResponse<String> verifyIdentity(@RequestBody UserDTO userDTO) throws IOException {
        AssertUtil.notNull(userDTO.getUserId(), "用户ID不能为空");
        AssertUtil.hasText(userDTO.getUserRealName(), "真实姓名不能为空");
        AssertUtil.hasText(userDTO.getIdCardNumber(), "身份证号不能为空");

        String response = identityVerificationUtil.verifyIdentity(userDTO.getUserRealName(),
                userDTO.getIdCardNumber());
        JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
        String respCode = jsonObject.get("respCode").getAsString();
        String respMessage = jsonObject.get("respMessage").getAsString();

        if ("0000".equals(respCode)) {
            AssertUtil.isTrue(userService.isUserExistById(userDTO.getUserId()), "用户不存在");
            User user = UserConverter.toEntity(userDTO);
            user.setStatus(UserStatusEnum.ACTIVE.getCode());
            userService.updateById(user);

            return CommonResponse.success("身份认证成功");
        } else {
            return CommonResponse.error(ResultCode.BAD_REQUEST, "身份认证失败：" + respMessage);
        }
    }

    /**
     * 用户登出接口
     */
    @PostMapping("/logout")
    public CommonResponse<Void> logout() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        log.info("User logging out: userId={}", userId);

        // 注销会话
        StpUtil.logout();

        // 返回登出成功响应
        return CommonResponse.success("登出成功", null);
    }

    // TODO: 不提供给管理员更新、增加、删除，也不可以删除用户，只提供给用户更新自己的信息
    // TODO: 管理员可以对用户的状态进行管理
    // TODO: 不提供注销账号功能
    // TODO: 后期在网关添加拦截器，对用户的请求进行拦截，要求用户登录后才能访问

    /**
     * 查询单个用户信息
     */
    @GetMapping("/{userId}")
    public CommonResponse<Object> getUserById(@PathVariable Long userId) {
        // 校验用户 ID 是否有效
        AssertUtil.notNull(userId, "用户 ID 不能为空");

        // 获取当前登录用户的 ID 和角色
        Long currentUserId = StpUtil.getLoginIdAsLong();
        User currentUser = userService.getById(currentUserId);
        AssertUtil.notNull(currentUser, "当前登录用户信息不存在");

        // 检查权限：只能是用户自己或管理员
        boolean isAdmin = RoleEnum.ADMIN.getCode().equals(currentUser.getRole());
        boolean isSelf = currentUserId.equals(userId);
//        AssertUtil.isTrue(isAdmin || isSelf, ResultCode.FORBIDDEN, "无权限查看该用户信息");

        // 查询用户信息
        User user = userService.getById(userId);
        AssertUtil.notNull(user, ResultCode.NOT_FOUND, "用户不存在");

        // 根据用户角色构造返回信息
        Object result;
        if (RoleEnum.ORGANIZATION.getCode().equals(user.getRole())) {
            result = user;
        } else {
//            result = UserConverter.toUserVO(user);
            result = user;
        }

        // 返回用户信息
        return CommonResponse.success("用户信息查询成功", result);
    }

    /**
     * 分页查询用户列表
     */
    @PostMapping("/users")
    public CommonResponse<Page<User>> listUsers(@RequestBody PageDTO pageDTO) {
        AssertUtil.isTrue(StpUtil.hasRole("ADMIN"), "无权限访问");
        log.info("Querying users by conditions: {}", pageDTO);
        Page<User> userPage = userService.getUsersByConditions(pageDTO);
        return CommonResponse.success(userPage);
    }

    /**
     * 更新个体用户信息
     */
    @PutMapping("/update/individual")
    public CommonResponse<User> updateIndividualUser(@RequestBody UserDTO userDTO) throws JsonProcessingException {
        // 获取当前登录用户 ID
        Long loggedInUserId = StpUtil.getLoginIdAsLong();

        // 检查待更新用户是否存在
        User targetUser = userService.getById(userDTO.getUserId());
        AssertUtil.notNull(targetUser, "用户不存在");

        // 确保待更新用户是个体用户
        AssertUtil.isTrue(RoleEnum.INDIVIDUAL.getCode().equals(targetUser.getRole()), "待更新用户不是个体用户");

        // 判断是否是用户自己或管理员
        boolean isAdmin = RoleEnum.ADMIN.getCode().equals(userService.getById(loggedInUserId).getRole());
        boolean isSelf = loggedInUserId.equals(userDTO.getUserId());
//        AssertUtil.isTrue(isAdmin || isSelf, "您无权限更新该用户的信息");

        // 保存更新后的数据
        if (StringUtils.isNotBlank(userDTO.getDonationHistory())) {
            // 原 donation_history（可能为 null）
            String oldHistoryJson = targetUser.getDonationHistory();
            List<DonationHistoryRecord> historyList = new ArrayList<>();
            if (StringUtils.isNotBlank(oldHistoryJson)) {
                historyList = objectMapper.readValue(oldHistoryJson, new TypeReference<>() {
                });
            }

            // 追加新记录（反序列化 DTO 中的 donationHistory）
            DonationHistoryRecord newRecord = objectMapper.readValue(userDTO.getDonationHistory(), DonationHistoryRecord.class);
            historyList.add(newRecord);

            // 重新序列化并设置
            userDTO.setDonationHistory(objectMapper.writeValueAsString(historyList));
        }

        // 保存更新后的数据
        if (StringUtils.isNotBlank(userDTO.getAssistanceHistory())) {
            // 原 donation_history（可能为 null）
            String oldHistoryJson = targetUser.getAssistanceHistory();
            List<AssistanceHistoryRecord> historyList = new ArrayList<>();
            if (StringUtils.isNotBlank(oldHistoryJson)) {
                historyList = objectMapper.readValue(oldHistoryJson, new TypeReference<>() {
                });
            }

            // 追加新记录（反序列化 DTO 中的 donationHistory）
            AssistanceHistoryRecord newRecord = objectMapper.readValue(userDTO.getAssistanceHistory(), AssistanceHistoryRecord.class);
            historyList.add(newRecord);

            // 重新序列化并设置
            userDTO.setAssistanceHistory(objectMapper.writeValueAsString(historyList));
        }

        User user = UserConverter.toEntity(userDTO);
        boolean success = userService.updateById(user);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "更新用户信息失败");
        User result = userService.getById(userDTO.getUserId());
        return CommonResponse.success("更新成功", result);
    }

    /**
     * 更新公益组织信息
     */
    @PutMapping("/update/organization")
    public CommonResponse<User> updateOrganization(@RequestBody OrganizationDTO organizationDTO) {
        // 获取当前登录用户 ID
        Long loggedInUserId = StpUtil.getLoginIdAsLong();

        // 检查待更新用户是否存在
        User targetUser = userService.getById(organizationDTO.getUserId());
        AssertUtil.notNull(targetUser, "用户不存在");

        // 确保待更新用户是公益组织
        AssertUtil.isTrue(RoleEnum.ORGANIZATION.getCode().equals(targetUser.getRole()), "待更新用户不是公益组织");

        // 判断是否是用户自己或管理员
        boolean isAdmin = RoleEnum.ADMIN.getCode().equals(userService.getById(loggedInUserId).getRole());
        boolean isSelf = loggedInUserId.equals(organizationDTO.getUserId());
        AssertUtil.isTrue(isAdmin || isSelf, "您无权限更新该用户的信息");

        // 保存更新后的数据
        User user = UserConverter.toEntity(organizationDTO);
        boolean success = userService.updateById(user);
        AssertUtil.isTrue(success, ResultCode.INTERNAL_SERVER_ERROR, "更新公益组织信息失败");

        User result = userService.getById(organizationDTO.getUserId());
        return CommonResponse.success("更新成功", result);
    }

    // TODO: 偷下懒，在这个接口顺便把用户姓名和组织名称查询也做了，嘿嘿(*^▽^*)

    /**
     * 检查用户是否有资格新建项目
     */
    @GetMapping("/{userId}/canCreateProject")
    public CommonResponse<Map<String, Object>> canCreateProject(@PathVariable Long userId) {
        // 校验用户是否存在
        User user = userService.getById(userId);
        AssertUtil.notNull(user, "用户不存在");

        // 初始化响应数据
        Map<String, Object> responseData = new HashMap<>();
        boolean canCreate = false;
        String reason = "用户有资格新建项目";
        String orgName = "";
        String contactPersonName = "";

        // 判断是否有资格
        if (RoleEnum.ORGANIZATION.getCode().equals(user.getRole())) {
            // 公益组织需要认证状态为通过（APPROVED）且状态为ACTIVE
            if (!CertificationStatusEnum.APPROVED.getCode().equals(user.getCertificationStatus())) {
                reason = "公益组织需要认证通过";
            } else if (!UserStatusEnum.ACTIVE.getCode().equals(user.getStatus())) {
                reason = "公益组织账户状态必须为ACTIVE";
            } else {
                orgName = user.getOrgName();
                contactPersonName = user.getContactPersonName();
                canCreate = true;
            }
        } else if (RoleEnum.INDIVIDUAL.getCode().equals(user.getRole())) {
            // 个体用户只需要状态为ACTIVE
            if (!UserStatusEnum.ACTIVE.getCode().equals(user.getStatus())) {
                reason = "个体用户账户状态必须为ACTIVE";
            } else {
                contactPersonName = user.getUserRealName();
                canCreate = true;
            }
        } else {
            reason = "用户角色无效，无法新建项目";
        }

        // 填充响应数据
        responseData.put("canCreate", canCreate);
        responseData.put("reason", reason);
        responseData.put("orgName", orgName);
        responseData.put("contactPersonName", contactPersonName);

        log.info("用户 ID {} 是否有资格新建项目：{}，原因：{}", userId, canCreate, reason);

        return CommonResponse.success("检查成功", responseData);
    }


    /**
     * 查询所有用户的数量
     */
    @GetMapping("/count")
    public CommonResponse<Long> getUserCount() {
        Long count = userService.count();
        return CommonResponse.success("用户总数查询成功", count);
    }
}