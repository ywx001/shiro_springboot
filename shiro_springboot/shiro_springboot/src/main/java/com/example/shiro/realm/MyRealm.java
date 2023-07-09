package com.example.shiro.realm;

import com.example.shiro.entity.User;
import com.example.shiro.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class MyRealm extends AuthorizingRealm {
    @Resource
    private UserService userService;

    //自定义授权方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("自定义授权方法");
        // 获取当前用户的身份信息
        String principal = (String) principals.getPrimaryPrincipal();
        // 根据 username 查询用户的角色和权限信息
        List<String> roles = userService.getUserRoleInfo(principal);
        System.out.println("roles = " + roles);
        List<String> permissions = userService.getUserPermissionInfo(roles);
        System.out.println("permissions = " + permissions);
        // 创建 SimpleAuthorizationInfo 对象，并设置角色和权限信息
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //存储信息
        info.addRoles(roles);
        info.addStringPermissions(permissions);
        //返回

        return info;
    }

    //自定义登录认证方法
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //获取用户身份信息
        String name = token.getPrincipal().toString();
        //调用业务层获取用户信息
        User user = userService.getUserInfoByName(name);
        //非空判断，封装返回
        if (user != null) {
            AuthenticationInfo info = new SimpleAuthenticationInfo(
                    token.getPrincipal(),
                    user.getPwd(),
                    ByteSource.Util.bytes("salt"),
                    token.getPrincipal().toString()
            );
            return info;

        }

        return null;
    }
}
