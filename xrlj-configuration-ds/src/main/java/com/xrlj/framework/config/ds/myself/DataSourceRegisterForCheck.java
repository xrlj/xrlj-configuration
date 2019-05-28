package com.xrlj.framework.config.ds.myself;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class DataSourceRegisterForCheck {

    private static Map<Object, HikariDataSource> dsOri; //记录最初设置的数据源。
    private static  Map<Object, HikariDataSource> dsAvailable; //可用数据源

    public Map<Object, HikariDataSource> getDsOri() {
        return dsOri;
    }

    public void setDsOri(Map<Object, HikariDataSource> dsOri) {
        this.dsOri = dsOri;
    }

    public Map<Object, HikariDataSource> getDsAvailable() {
        return dsAvailable;
    }

    public void setDsAvailable(Map<Object, HikariDataSource> dsAvailable) {
        this.dsAvailable = dsAvailable;
    }
}
