package csu.yulin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import csu.yulin.model.entity.User;

/**
 * 用户表 服务类
 *
 * @author lp
 * @create 2024-12-30
 */
public interface IUserService extends IService<User> {

    /**
     * 检查用户是否存在
     */
    boolean isUserExistByPhoneNumber(String phoneNumber);

    /**
     * 检查用户是否存在
     */
    boolean isUserExistById(Long userId);

    /**
     * 根据手机号查询用户信息
     */
    User getUserByPhoneNumber(String phoneNumber);
}
