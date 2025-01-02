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
    public boolean isUserExist(String phoneNumber) {
        return count(new LambdaQueryWrapper<User>()
                .eq(User::getPhoneNumber, phoneNumber)) > 0;
    }
}
