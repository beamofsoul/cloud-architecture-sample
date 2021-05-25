package com.beamofsoul.cloud.cas.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class Product {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
