package com.xrlj.framework.config.aop;

import com.xrlj.framework.config.ds.AbstractDataSourceAspect;
import com.xrlj.framework.config.ds.myself.DSType;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 *  解析注入的数据源。
 */
@Aspect
@Component
public class DynamicDataSourceAspect extends AbstractDataSourceAspect {

	@Override
	public String defaultDb() {
		return DSType.Myself.MASTER;
	}
}
