package com.xrlj.framework.config.ds.myself;

import com.xrlj.framework.config.ds.DynamicDataSource;
import org.jooq.impl.DataSourceConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

@Configuration
@AutoConfigureAfter(DataSourceConfig.class)
public class JooqConfig {

    @Autowired
    protected Environment environment;

    @Autowired
    @Qualifier("dynamicDataSource")
    protected DynamicDataSource dynamicDataSource; // 数据源

    /**
     * 这里定义了该bean，将不会执行{@link org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration}中的。
     * 因为它里面该bean加了注解@ConditionalOnMissingBean
     * @return
     */
    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(DataSourceSpyUtils.conversion(environment,dynamicDataSource)));
    }
}
