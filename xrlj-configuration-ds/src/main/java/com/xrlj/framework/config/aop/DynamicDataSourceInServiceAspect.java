package com.xrlj.framework.config.aop;

import com.xrlj.framework.config.ds.AbstractDataSourceInServiceAspect;
import com.xrlj.framework.config.ds.myself.DSType;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 *  解析注入的数据源。/
 */
@Aspect
@EnableAspectJAutoProxy(exposeProxy=true,proxyTargetClass=true)
@Component
@Lazy(false)
@Order(0) //值越小，越优先执行,要优于事务的执行,在启动类中加上了@EnableTransactionManagement(order = 10)
public class DynamicDataSourceInServiceAspect extends AbstractDataSourceInServiceAspect {

	@Override
	public String defaultDb() {
		return DSType.Myself.MASTER;
	}
}
