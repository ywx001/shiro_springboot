package com.example.shiro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shiro.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT r.name \n" +
            "FROM role r\n" +
            "JOIN role_user ru\n" +
            "ON r.`id` = ru.`rid`\n" +
            "JOIN USER u\n" +
            "ON u.`id` = ru.`uid`\n" +
            "WHERE u.`name`=#{principal} ")
    List<String> getUserRoleInfoMapper(@Param("principal") String principal);

    @Select("<script>" +
            "SELECT p.`info`\n" +
            "FROM permissions p\n" +
            "JOIN role_ps rp\n" +
            "ON p.`id` = rp.`pid`\n" +
            "JOIN role r\n" +
            "ON r.`id` = rp.`rid`\n" +
            "WHERE r.`name` IN " +
            "<foreach collection=\"roles\" item=\"role\" separator=\",\" open=\"(\" close=\")\">" +
            " #{role}" +
            "</foreach>" +
            "</script>")
    List<String> getUserPermissionInfoMapper(@Param("roles") List<String> roles);


}
