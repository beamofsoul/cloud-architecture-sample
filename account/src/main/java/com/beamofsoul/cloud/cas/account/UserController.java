package com.beamofsoul.cloud.cas.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/currentUser")
    public SecurityUser currentUser() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String userStr = request.getHeader("user");
        JSONObject json = JSON.parseObject(userStr);
        System.out.println(json.get("authorities"));
        return SecurityUser.builder()
                .username(json.getString("user_name"))
                .id(Long.valueOf(json.getString("id")))
                .roles(json.getObject("authorities", new TypeReference<ArrayList<String>>() {}))
                .build();
    }
}
