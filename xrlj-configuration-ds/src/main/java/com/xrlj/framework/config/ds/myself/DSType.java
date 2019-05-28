package com.xrlj.framework.config.ds.myself;

/**
 * 数据源类型。
 *
 * @author zmt
 */
public interface DSType {

    interface Myself {
        String MASTER = "myself_master";
        String SLAVE1 = "myself_slave1";
        String SLAVE2 = "myself_slave2";
    }

}
