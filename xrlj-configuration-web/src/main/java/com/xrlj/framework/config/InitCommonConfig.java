package com.xrlj.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.File;

@Slf4j
@Configuration
@Order(-1)
public class InitCommonConfig implements CommandLineRunner {

    @Value("${spring.application.name}")
    protected String appName;

    @Override
    public void run(String... args) throws Exception {
        String tmpDir = FileUtils.getUserDirectoryPath().concat(File.separator).concat(".xrlj");
        File file = new File(tmpDir.concat(File.separator).concat(appName));
        if (!file.exists()) {
            file.mkdirs();
            log.info("成功创建应用程序文件目录：{}", file.getAbsolutePath());
        } else {
            log.info("应用程序文件目录已存在：{}", file.getAbsolutePath());
        }
    }
}
