package com.xrlj.framework.config.ds;

import com.xrlj.framework.config.ds.myself.DSType;
import com.xrlj.framework.spring.mvc.api.APIs;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);

    private AtomicInteger count = new AtomicInteger(0);

    private static Map<Object, Object> dsMapFull; //记录最初设置的数据源。
    private static Map<Object, Object> dsMap; //记录的是可用的数据源，已经剔除挂机数据源。

    @Value("${spring.myself-db.datasource.slave-size}")
    private int readSize;

    @Override
    protected Object determineCurrentLookupKey() {
        if (dsMap.size() == 0) {
            log.error(">>>>>>>>>>>>>>>>无可用数据源！");
            throw APIs.error(1000,"数据源异常!",null);
        }
        if (!dsMap.containsKey(DSType.Myself.MASTER)) {
            log.error(">>>>>>>>>>>>>>>>主数据源挂机！");
            throw APIs.error(1001,"数据源异常!",null);
        }

        String currentDs = DataSourceContextHolder.getDS();
        //切面没有明确指定数据源，采用的是默认数据源，即主库，但是切换到从库来
        if (currentDs == null) {
            return null;
        }
        if (currentDs.equals(DSType.Myself.MASTER)) {
            return DSType.Myself.MASTER;
        }

        //没有可用读库，就用主库；有的话就负载获取一个。
        Set<Object> keys = dsMap.keySet();
        boolean  haveSlave = false;
        for (Object key : keys) {
            String keyStr = (String) key;
            if (keyStr.contains(myselfDbSlavePrefix())) {
                haveSlave = true;
                break;
            }
        }
        if (haveSlave) {
            currentDs = simpleLoadBalance();
        } else { //没有可用读库，用主库
            currentDs = DSType.Myself.MASTER;
        }
        log.info(">>>>>>当前使用读数据源：{}",currentDs);
        return currentDs;
    }

    /**
     *  读库， 简单负载均衡
     * @return 返回选定的读库
     */
    private String simpleLoadBalance() {
        if (count.intValue() >= Integer.MAX_VALUE) {
            count.set(1);
        }
        //读库， 简单负载均衡
        int number = count.getAndAdd(1);
        int lookupKey = number % readSize;
        String slaveStr = myselfDbSlavePrefix();
        String newSlaveDS = slaveStr + (lookupKey + 1);
        if (!dsMap.containsKey(newSlaveDS)) {//如果选中数据源不在可用数据源记录中，则重新选择下一个，直到选定一个为止。
            newSlaveDS = simpleLoadBalance();
        }
        return  newSlaveDS;
    }

    /**
     * 配置多个数据源。
     * @param dataSources 数组的第一个为主库，其它为从库。
     * @return
     */
    public DynamicDataSource setMultipleDataSource(@NonNull HikariDataSource...dataSources) {
        dsMap = new HashMap<>(dataSources.length);
        dsMapFull = new HashMap<>(dataSources.length);
        String prefix = myselfDbSlavePrefix();
        for (int i = 0; i < dataSources.length; i++) {
            if (i == 0) {
                dsMap.put(DSType.Myself.MASTER, dataSources[0]);
                this.setDefaultTargetDataSource(dataSources[0]); // 设置默认数据源,主库

                dsMapFull.put(DSType.Myself.MASTER, dataSources[0]);
            } else {
                dsMap.put(prefix.concat(String.valueOf(i)),dataSources[i]);

                dsMapFull.put(prefix.concat(String.valueOf(i)),dataSources[i]);
            }
        }
        this.setTargetDataSources(dsMap);
        return this;
    }

    public Map<Object,Object> getMultipleDataSource() {
        return this.dsMap;
    }

    public Map<Object,Object> getMultipleDataSourceFull() {
        return this.dsMapFull;
    }

    private String myselfDbSlavePrefix() {
        String prefix = DSType.Myself.SLAVE1.substring(0,DSType.Myself.SLAVE1.length() - 1);
        return prefix;
    }
}
