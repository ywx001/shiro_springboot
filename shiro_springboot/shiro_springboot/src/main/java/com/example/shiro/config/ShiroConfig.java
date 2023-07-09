package com.example.shiro.config;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;
import com.example.shiro.realm.MyRealm;
import net.sf.ehcache.CacheManager;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.io.ResourceUtils;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author y
 */
@Configuration
public class ShiroConfig {
    @Resource
    private MyRealm myRealm;

    /**
     * 配置SecurityManager
     */
    @Bean
    public DefaultWebSecurityManager defaultWebSecurityManager() {
        //1创建对象
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        //2创建加密对象，设置相应属性
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //2.1MD5加密
        matcher.setHashAlgorithmName("MD5");
        //2.2迭代加密次数
        matcher.setHashIterations(3);
        //3将加密对象存储到myRealm中
        myRealm.setCredentialsMatcher(matcher);
        //4将myRealm存入
        defaultWebSecurityManager.setRealm(myRealm);
        //4.5设置rememberMe
        defaultWebSecurityManager.setRememberMeManager(rememberMeManager());
        //4.6设置缓存管理器
        defaultWebSecurityManager.setCacheManager(getEhCacheManager());
        //5返回
        return defaultWebSecurityManager;
    }

    //缓存管理器
    public EhCacheManager getEhCacheManager(){
        EhCacheManager em = new EhCacheManager();
        InputStream is = null;
        try {
            is = ResourceUtils.getInputStreamForPath("classpath:ehcache/ehcache-shiro.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        CacheManager cacheManager = new CacheManager(is);
        em.setCacheManager(cacheManager);
        return em;
    }

    //cookie属性设置
    public SimpleCookie rememberMeCookie() {
        SimpleCookie cookie = new SimpleCookie("rememberMe");
        //设置跨域
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(30 * 24 * 60 * 60);
        return cookie;
    }

    //创建shiro的cookie管理对象
    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        cookieRememberMeManager.setCipherKey("1234567890987654".getBytes());
        return cookieRememberMeManager;
    }

    //配置shiro内置拦截信息
    @Bean
    public DefaultShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition definition = new DefaultShiroFilterChainDefinition();
        // 设置不认证可以访问的资源
        definition.addPathDefinition("/myController/userLogin", "anon");
        definition.addPathDefinition("/myController/login", "anon");
        //配置登出过滤器
        definition.addPathDefinition("/logout", "logout");
        //设置需要进行登录认证的范围
        definition.addPathDefinition("/**", "authc");
        //添加存在用户的过滤器rememberMe
        definition.addPathDefinition("/**", "user");
        return definition;

    }

    @Bean
    public ShiroDialect shiroDialect() {
        return new ShiroDialect();
    }


}
