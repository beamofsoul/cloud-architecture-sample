package com.beamofsoul.cloud.cas.authentication;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    /**
     * 通过keytool生成密钥后，在本地resources文件下读取密钥对信息
     */
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "91297918".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "91297918".toCharArray());
    }

    /**
     * 在原始JWTToken基础上增加用户编号信息，用于后续传递过程中对当前授权用户信息的使用
     */
    @Bean
    public TokenEnhancer jwtTokenEnhancer() {
        return (oAuth2AccessToken, oAuth2Authentication) -> {
            SecurityUser user = (SecurityUser) oAuth2Authentication.getPrincipal();
            Map<String, Object> info = new HashMap<>();
            info.put("id", user.getId());
            ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);
            return oAuth2AccessToken;
        };
    }

    /**
     * 帮助程序在JWT编码的令牌值(JWTToken)和OAuth身份验证信息之间进行转换
     * 其中，指定Token签名的方式(RSA非对称加密算法来对Token进行签名)
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setKeyPair(keyPair());
        return jwtAccessTokenConverter;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client-app")
                .secret(passwordEncoder.encode("91297918"))
                .scopes("all")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(86400);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain chain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancers = new ArrayList<>();
        enhancers.add(jwtTokenEnhancer());
        enhancers.add(accessTokenConverter());
        chain.setTokenEnhancers(enhancers); // 配置JWT的内容增强器
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService) // 配置加载用户信息的服务
                .accessTokenConverter(accessTokenConverter())
                .tokenEnhancer(chain);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
    }
}
