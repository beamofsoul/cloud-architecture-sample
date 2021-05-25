package com.beamofsoul.cloud.cas.gateway;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {
// https://blog.csdn.net/yelvgou9995/article/details/107229699/
    private final AuthorizationManager authorizationManager;
    private final IgnoreUrlsConfig ignoreUrlsConfig;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                .and()
                .accessDeniedHandler(((exchange, denied) -> dealResponse(exchange.getResponse(), ResponseEntity.FORBIDDEN)))
                .authenticationEntryPoint((exchange, exception) -> dealResponse(exchange.getResponse(),
                        exception.getCause() instanceof JwtValidationException ? ResponseEntity.UNAUTHORIZED_EXPIRED :
                                exception.getCause() instanceof BadJwtException ? ResponseEntity.UNAUTHORIZED_INVALID : ResponseEntity.UNAUTHORIZED));
        http.authorizeExchange()
                .pathMatchers(ignoreUrlsConfig.getUrls().toArray(new String[] {})).permitAll()
                .anyExchange().access(authorizationManager) // 鉴权管理器配置
                .and().exceptionHandling()
                .accessDeniedHandler((exchange, denied) -> dealResponse(exchange.getResponse(), ResponseEntity.FORBIDDEN)) // 处理未授权
                .authenticationEntryPoint((exchange, exception) -> dealResponse(exchange.getResponse(), ResponseEntity.UNAUTHORIZED)) // 处理未认证
                .and().csrf().disable();
        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    public Mono<Void> dealResponse(ServerHttpResponse response, ResponseEntity responseEntity) {
        byte[] responseText = responseEntity.toJSONString().getBytes(StandardCharsets.UTF_8); // 自定义Response中的code、message和data内容
        DataBuffer buffer = response.bufferFactory().wrap(responseText);
        response.setStatusCode(HttpStatus.OK); // 响应状态总时成功，说明链接无误，具体响应状态和信息通过自定义相应对象返回
        return response.writeWith(Flux.just(buffer));
    }
}
