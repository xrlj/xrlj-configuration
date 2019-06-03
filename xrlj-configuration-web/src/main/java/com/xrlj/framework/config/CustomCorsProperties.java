package com.xrlj.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 跨域配置信息对象。
 */
@Configuration
@ConfigurationProperties(prefix = "custom.cors")
public class CustomCorsProperties {

    //描述 : 扫描地址
    private String mapping;
    //描述 : 允许证书
    private Boolean allowCredentials;
    //描述 : 允许的域
    private String allowedOrigins;
    //描述 : 允许的方法
    private String allowedMethods;
    //描述 : 允许的头信息
    private String allowedHeaders;

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public Boolean getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public String getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public String getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }
}
