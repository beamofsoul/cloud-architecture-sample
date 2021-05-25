package com.beamofsoul.cloud.cas.gateway;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public final class ResponseEntity {

    private static final int STATUS_CODE_BASE = 100000;
    private static final Map<HttpStatus, Integer> CODE_MAPPING = Collections.unmodifiableMap(new HashMap<HttpStatus, Integer>() {{
        put(HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.value() + STATUS_CODE_BASE);
        put(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value() + STATUS_CODE_BASE);
    }});
    public static final ResponseEntity FORBIDDEN = ResponseEntity.of(HttpStatus.FORBIDDEN, "拒绝访问");
    public static final ResponseEntity UNAUTHORIZED = ResponseEntity.of(HttpStatus.UNAUTHORIZED, "未经授权");
    public static final ResponseEntity UNAUTHORIZED_EXPIRED = ResponseEntity.of(HttpStatus.UNAUTHORIZED, "未经授权[令牌过期]");
    public static final ResponseEntity UNAUTHORIZED_INVALID = ResponseEntity.of(HttpStatus.UNAUTHORIZED, "未经授权[无效令牌]");

    private Integer code;
    private String message;
    private Object data;

    public static ResponseEntity of(HttpStatus status, String message, Object data) {
        return ResponseEntity.builder().code(CODE_MAPPING.get(status)).message(message).data(data).build();
    }

    public static ResponseEntity of(HttpStatus status, String message) {
        return ResponseEntity.of(status, message, null);
    }

    public String toJSONString() {
        return JSON.toJSONString(this, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty);
    }
}
