package com.xrlj.framework.config.ds.myself;

import com.xrlj.framework.config.ds.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;

/**
 * 按规则定死的数据源。一主双从。适用所有服务。
 */
@Configuration
public class DataSourceConfig {

    @Bean(name = "masterDataSource")
    @Qualifier("masterDataSource")
    @ConfigurationProperties(prefix="spring.myself-db.datasource.hikari.master")
    public HikariDataSource masterDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "slave1DataSource")
    @Qualifier("slave1DataSource")
    @ConfigurationProperties(prefix="spring.myself-db.datasource.hikari.slave1")
    public HikariDataSource slave1DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "slave2DataSource")
    @Qualifier("slave2DataSource")
    @ConfigurationProperties(prefix="spring.myself-db.datasource.hikari.slave2")
    public HikariDataSource slave2DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    /**
     * 动态数据源: 通过AOP在不同数据源之间动态切换
     *
     * @return
     */
    @Primary
    @Bean(name = "dynamicDataSource")
    @Qualifier("dynamicDataSource")
    @Scope("singleton")
    @DependsOn({"masterDataSource","slave1DataSource","slave2DataSource"}) //要加入这个注解，在数据源初始化之后，再初始化本bean，否则会出现循环依赖注入无法启动。
    public DynamicDataSource dynamicDataSource(@Qualifier("masterDataSource") HikariDataSource masterDataSource,
                                               @Qualifier("slave1DataSource") HikariDataSource slave1DataSource, @Qualifier("slave2DataSource") HikariDataSource slave2DataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        return dynamicDataSource.setMultipleDataSource(masterDataSource,slave1DataSource,slave2DataSource);
    }

}
