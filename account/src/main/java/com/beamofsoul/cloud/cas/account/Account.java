package com.beamofsoul.cloud.cas.account;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class Account {
    private Long id;
    private String name;
    private BigDecimal money;
}
