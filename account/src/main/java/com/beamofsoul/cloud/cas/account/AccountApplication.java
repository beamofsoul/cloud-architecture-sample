package com.beamofsoul.cloud.cas.account;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.LongStream;

@SpringBootApplication
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

    public static final ConcurrentHashMap<Long, Account> accounts = new ConcurrentHashMap<>();

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            LongStream.rangeClosed(1, 4).parallel().forEach(i -> accounts.put(i, Account.builder().id(i).name("account_" + i).money(BigDecimal.valueOf(10000L)).build()));
            accounts.entrySet().stream().forEachOrdered(System.out::println);
        };
    }
}
