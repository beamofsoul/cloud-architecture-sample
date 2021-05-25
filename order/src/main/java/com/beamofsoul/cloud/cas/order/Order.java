package com.beamofsoul.cloud.cas.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Order {
    private Long id;
    private Long account;
    private Long product;
    private Long amount;
    private Long totalPrice;
}
