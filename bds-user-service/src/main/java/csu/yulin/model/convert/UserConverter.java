package csu.yulin.model.convert;

import csu.yulin.model.dto.OrganizationDTO;
import csu.yulin.model.dto.UserDTO;
import csu.yulin.model.entity.User;
import csu.yulin.model.vo.OrganizationVO;
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

    /**
     * 将 User 实体转换为 UserDTO
     *
     * @param user 用户实体
     * @return 用户 DTO
     */
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    /**
     * 将 UserDTO 转换为 User 实体
     *
     * @param dto 用户 DTO
     * @return 用户实体
     */
    public static User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return user;
    }

    /**
     * 将 OrganizationDTO 转换为 User 实体
     *
     * @param dto 组织 DTO
     * @return 用户实体
     */
    public static User toEntity(OrganizationDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        return user;
    }

    /**
     * 将 User 实体转换为 UserVO
     *
     * @param user 用户实体
     * @return 用户 VO
     */
    public static UserVO toUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /**
     * 将 User 实体转换为 OrganizationVO
     *
     * @param user 用户实体
     * @return 组织 VO
     */
    public static OrganizationVO toOrganizationVO(User user) {
        if (user == null) {
            return null;
        }
        OrganizationVO vo = new OrganizationVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
