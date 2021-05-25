package com.beamofsoul.cloud.cas.account;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Builder
@Data
public class SecurityUser {

    private Long id;
    private String username;
    private Collection roles;
}
