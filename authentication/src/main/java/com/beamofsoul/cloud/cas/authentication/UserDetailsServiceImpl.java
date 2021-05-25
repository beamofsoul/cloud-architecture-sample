package com.beamofsoul.cloud.cas.authentication;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private Map<String, SecurityUser> userMap;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        String password = passwordEncoder.encode("91297918");
        SecurityUser user1 = SecurityUser.builder().id(1L).username("beamofsoul").password(password).enabled(true).accountNonLocked(true).accountNonExpired(true).credentialsNonExpired(true).authorities(Lists.newArrayList(new SimpleGrantedAuthority("ADMIN"))).build();
        SecurityUser user2 = SecurityUser.builder().id(2L).username("tutou").password(password).enabled(true).accountNonLocked(true).accountNonExpired(true).credentialsNonExpired(true).authorities(Lists.newArrayList(new SimpleGrantedAuthority("TEST"))).build();
        userMap = new HashMap<>();
        userMap.put(user1.getUsername(), user1);
        userMap.put(user2.getUsername(), user2);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<SecurityUser> optUser = Optional.ofNullable(userMap.get(username));
        SecurityUser user = optUser.orElseThrow(() -> new UsernameNotFoundException("用户名或密码错误"));
        if (!user.isEnabled()) {
            throw new DisabledException("账户不可用");
        } else if (!user.isAccountNonLocked()) {
            throw new LockedException("账户已被锁定");
        } else if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("账户已过期");
        } else if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException("证书已过期");
        }
        return user;
    }
}
