package com.beamofsoul.cloud.cas.product;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.LongStream;

@SpringBootApplication
public class ProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }

    public static final ConcurrentHashMap<Long, Product> products = new ConcurrentHashMap<>();

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            LongStream.rangeClosed(1, 4).parallel().forEach(i -> products.put(i, Product.builder().id(i).name("product_" + i).price(ProductApplication.randomPrice()).quantity(100).build()));
            products.entrySet().stream().forEachOrdered(System.out::println);
        };
    }

    private static BigDecimal randomPrice() {
        BigDecimal price = new BigDecimal(Math.random() * 99 + 1);
        return price.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
