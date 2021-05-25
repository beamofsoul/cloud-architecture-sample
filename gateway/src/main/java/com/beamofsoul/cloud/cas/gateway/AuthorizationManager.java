package com.beamofsoul.cloud.cas.gateway;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        URI uri = object.getExchange().getRequest().getURI();
        Object obj = redisTemplate.opsForHash().get("SYSTEM:AUTH:RESOURCE_ROLE_MAPPING", uri.getPath());
        List<String> authorities = ((ArrayList<String>) obj).stream().map(each -> "ROLE_" + each).collect(Collectors.toList());
        return authentication // 认证通过且角色匹配的用户可访问当前路径
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
