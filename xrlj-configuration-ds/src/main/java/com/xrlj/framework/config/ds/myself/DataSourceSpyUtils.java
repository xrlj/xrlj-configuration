package com.xrlj.framework.config.ds.myself;

import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import javax.sql.DataSource;

public final class DataSourceSpyUtils {

    private DataSourceSpyUtils(){}

    public static DataSource conversion(Environment environment, DataSource dynamicDataSource) {
        if (environment.acceptsProfiles(Profiles.of("dev")) || environment.acceptsProfiles(Profiles.of("test"))
                || environment.acceptsProfiles(Profiles.of("update"))
        || environment.acceptsProfiles(Profiles.of("local"))) {// log4jdbc打印sql日志
            DataSource dsSpy = new DataSourceSpy(dynamicDataSource);
            return dsSpy;
        }
        return dynamicDataSource;
    }
}
