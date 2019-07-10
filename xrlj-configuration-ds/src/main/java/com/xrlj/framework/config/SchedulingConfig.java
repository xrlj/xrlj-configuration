package com.xrlj.framework.config;

import com.xrlj.framework.config.ds.DynamicDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

@ConditionalOnProperty(name = "scheduling.cron.validation-datasource-check")
@Configuration
@EnableScheduling
public class SchedulingConfig {
    private static final Logger log = LoggerFactory.getLogger(SchedulingConfig.class);

    @Autowired
    @Qualifier(value = "dynamicDataSource")
    private DynamicDataSource dynamicDataSource;

    @Scheduled(cron = "${scheduling.cron.validation-datasource-check:0/5 * * * * ?}")
    public void validationDataSource() {
        Map<Object, Object> registerDataSource = dynamicDataSource.getMultipleDataSourceFull();
        Map<Object, Object> availableDataSource = dynamicDataSource.getMultipleDataSource();
        Set<Object> keys = registerDataSource.keySet();
        for (Object key:  keys) {
            HikariDataSource realDataSource = (HikariDataSource) registerDataSource.get(key);
            String jdbcUrl = realDataSource.getJdbcUrl();
            Connection connection = null;
            try {
                connection = DriverManager.getConnection(jdbcUrl, realDataSource.getUsername(), realDataSource.getPassword());
                if (!availableDataSource.containsKey(key)) { //重新加入该数据源
                    availableDataSource.put(key,realDataSource);
                }
            } catch (Exception e) {
                //剔除该数据源，并重新设置。
                availableDataSource.remove(key);
                e.printStackTrace();
                log.error(">>>>>>dataSource halt::::{}",jdbcUrl);
            } finally {
                if (connection  != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        dynamicDataSource.setTargetDataSources(availableDataSource);
    }
}
