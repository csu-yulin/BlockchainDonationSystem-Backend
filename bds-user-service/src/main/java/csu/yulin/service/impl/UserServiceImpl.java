package csu.yulin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import csu.yulin.mapper.UserMapper;
import csu.yulin.model.entity.User;
import csu.yulin.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * 用户表 服务实现类
 *
 * @author lp
 * @create 2024-12-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    /**
     * 检查用户是否存在
     */
    @Override
    public boolean isUserExistByPhoneNumber(String phoneNumber) {
        return count(new LambdaQueryWrapper<User>()
                .eq(User::getPhoneNumber, phoneNumber)) > 0;
    }

    /**
     * 检查用户是否存在
     */
    @Override
    public boolean isUserExistById(Long userId) {
        return count(new LambdaQueryWrapper<User>()
                .eq(User::getUserId, userId)) > 0;
    }

    /**
     * 根据手机号查询用户信息
     */
    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhoneNumber, phoneNumber));
    }
}
