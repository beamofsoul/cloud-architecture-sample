package com.beamofsoul.cloud.cas.authentication;

import com.google.common.collect.Lists;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class ResourceServiceImpl {

    private Map<String, List<String>> resourceRolesMap;

    @Resource
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        resourceRolesMap = new TreeMap<>();
        resourceRolesMap.put("/account/hello", Lists.newArrayList("ADMIN"));
        resourceRolesMap.put("/account/user/currentUser", Lists.newArrayList("ADMIN", "TEST"));
        redisTemplate.opsForHash().putAll("SYSTEM:AUTH:RESOURCE_ROLE_MAPPING", resourceRolesMap);
    }
}
