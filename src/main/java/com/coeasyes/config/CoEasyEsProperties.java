package com.coeasyes.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @program: coeasyes-spring-boot-starter
 * @description: 配置属性类
 * @author: jiangchengxuan
 * @created: 2023/11/22 16:44
 */
@ConfigurationProperties(prefix = "coeasyes")
@Data
public class CoEasyEsProperties {


    private String hosts;

    private String username;

    private String password;

    private boolean enableSSL;

    private String crtFile;

    /**
     * 是否开启
     */
    private boolean enable;


    /**
     * 平台：不同服务使用的区分，默认取 spring.application.name
     */
    @Value("${spring.application.name:#{null}}")
    private String platform;
}
