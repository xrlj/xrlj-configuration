package com.xrlj.framework.config.ds;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceContextHolder {

    private static final Logger log = LoggerFactory.getLogger(DataSourceContextHolder.class);

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /**
     * 设置数据源名。
     * @param dbType
     */
    public static void setDS(String dbType) {
        if (dbType == null) {
            throw new NullPointerException("数据源不能null");
        }
        contextHolder.set(dbType);
    }

    /**
     * 获取数据源名。
     * @return
     */
    public static String getDS() {
        return (contextHolder.get());
    }

    /**
     * 清除数据源名。
     */
    public static void clearDS() {
        contextHolder.remove();
    }
}
