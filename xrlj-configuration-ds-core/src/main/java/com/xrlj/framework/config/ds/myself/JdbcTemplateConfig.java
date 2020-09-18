package com.xrlj.framework.config.ds.myself;

import com.xrlj.framework.config.ds.DynamicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@AutoConfigureAfter(DataSourceConfig.class)
@Import(DataSourceConfig.class)
public class JdbcTemplateConfig {

    @Autowired
    protected Environment environment;

    @Autowired
    @Qualifier("dynamicDataSource")
    private DynamicDataSource dynamicDataSource;

    /**
     * spring jdbc。
     *
     * @return
     */
    @Bean(name = "jdbcTemplate")
    @Qualifier("jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(DataSourceSpyUtils.conversion(environment,dynamicDataSource));
    }
}
