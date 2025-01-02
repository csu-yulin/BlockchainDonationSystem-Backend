package csu.yulin.model.convert;

import csu.yulin.model.dto.UserDTO;
import csu.yulin.model.entity.User;
import csu.yulin.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * 用户转换器
 *
 * @author lp
 * @create 2025-01-02
 */
@Slf4j
public class UserConverter {

    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    public static User toEntity(UserDTO dto) {
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return user;
    }

    public static UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
