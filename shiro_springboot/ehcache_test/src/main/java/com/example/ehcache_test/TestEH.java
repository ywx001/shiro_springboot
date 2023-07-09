package com.example.ehcache_test;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.io.InputStream;
import java.io.InputStreamReader;

public class TestEH {
    public static void main(String[] args) {
        //获取编译目录下的资源的流对象
        InputStream input = TestEH.class.getClassLoader().getResourceAsStream("ehcache.xml");
        //获取EhCache的缓存管理对象
        CacheManager cacheManager = new CacheManager(input);
        // 获取缓存对象
        Cache cache = cacheManager.getCache("exampleCache");
        //创建缓存数据
        Element element = new Element("name", "zhangsan");
        //存入缓存
        cache.put(element);
        //从缓存中取出数据输出
        Element element1 = cache.get("name");
        System.out.println("element1 = " + element1.getObjectValue());
    }
}
