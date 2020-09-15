package com.xrlj.framework.config.ds.myself;

import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

public final class DataSourceSpyUtils {

    private DataSourceSpyUtils(){}

    public static DataSource conversion(Environment environment, DataSource dynamicDataSource) {
        boolean show = environment.getProperty("spring.myself-db.datasource.log-jdbc-show", Boolean.class);
        if (!show) {
            return dynamicDataSource;
        }
        /*if (environment.acceptsProfiles(Profiles.of("dev"))
                || environment.acceptsProfiles(Profiles.of("test"))
                || environment.acceptsProfiles(Profiles.of("update"))
        ) {// log4jdbc打印sql日志
            DataSource dsSpy = new DataSourceSpy(dynamicDataSource);
            return dsSpy;
        }*/

        DataSource dsSpy = new DataSourceSpy(dynamicDataSource);
        return dsSpy;
    }
}
