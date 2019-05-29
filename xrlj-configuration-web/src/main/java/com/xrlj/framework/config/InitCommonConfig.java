package com.xrlj.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
@Order(-1)
public class InitCommonConfig implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info(">>>>>服务启动成功：{}",args);
    }
}
