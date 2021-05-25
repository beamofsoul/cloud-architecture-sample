package com.beamofsoul.cloud.cas.account;

import com.alibaba.fastjson.JSON;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @GetMapping("hello")
    public String hello() {
        System.out.println("Hello, World!");
        return "Hello, World!";
    }

    @GetMapping("accounts")
    public String accounts() {
        return JSON.toJSONString(AccountApplication.accounts.values());
    }
}
