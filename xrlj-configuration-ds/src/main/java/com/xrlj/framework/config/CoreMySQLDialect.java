package com.xrlj.framework.config;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.IntegerType;
import org.hibernate.type.TimestampType;

public class CoreMySQLDialect extends MySQL8Dialect {

    public CoreMySQLDialect() {
        super();
        this.registerFunction("now",new SQLFunctionTemplate(new TimestampType(), "now()"));
        this.registerFunction("timestampdiff",new SQLFunctionTemplate(new IntegerType(), "timestampdiff(?1,?2,?3)"));
        this.registerKeyword("minute");
    }
}
