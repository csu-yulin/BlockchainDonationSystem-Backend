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
    boolean isUserExist(String phoneNumber);
}
