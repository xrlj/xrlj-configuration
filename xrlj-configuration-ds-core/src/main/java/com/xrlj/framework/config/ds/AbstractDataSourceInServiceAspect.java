package com.xrlj.framework.config.ds;

import com.xrlj.framework.base.BaseEntity;
import com.xrlj.framework.config.ds.myself.DSType;
import com.xrlj.framework.spring.mvc.api.APIs;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

/**
 *  解析注入的数据源。
 */
public abstract class AbstractDataSourceInServiceAspect{

	private static final Logger log = LoggerFactory.getLogger(AbstractDataSourceInServiceAspect.class);

	public abstract String defaultDb();

	@Pointcut("within(com.xrlj.*.service.impl..*)")
	public void dataSourcePointcut(){}

	/**
	 * 在service的方法上添加注解事务注解Transactional，则使用主库，此时方法里面的读数据也是采用主库的。
	 * <br> 否则在{@link DynamicDataSource} 中切换到读库。
	 */
	@Before("dataSourcePointcut()")
	public void setDataSourceType(JoinPoint point) {
		String dataSource = defaultDb(); //默认主库

		// 获得当前访问的class
		Class<?> className = point.getTarget().getClass();
		// 获得访问的方法名
		String methodName = point.getSignature().getName();
		// 得到方法的参数的类型
		Class[] argClass = ((MethodSignature) point.getSignature()).getParameterTypes();
		try {
			// 得到访问的方法对象
			Method method = className.getMethod(methodName, argClass);
			Class returnType = method.getReturnType();
			if (returnType.getSuperclass() == BaseEntity.class) {
				throw new IllegalArgumentException("服务层接口不能直接返回实体领域对象");
			}
			// 判断是否存在@Transactional注解
			if (method.isAnnotationPresent(Transactional.class)) {//存在
				Transactional annotation = method.getAnnotation(Transactional.class);
				// 取出注解中的数据源名
				boolean readOnly = annotation.readOnly();
				if (readOnly) {
					dataSource = DSType.Myself.SLAVE1;
				} else {
					dataSource = DSType.Myself.MASTER;
				}
			} else {
				dataSource = DSType.Myself.SLAVE1;
			}
		} catch (Exception e) {
			log.error(">>>>切换数据源异常：",e);
			throw APIs.error(0000,e.getMessage(),null);
		}
		// 切换数据源
		DataSourceContextHolder.setDS(dataSource);
	}

	@After("dataSourcePointcut()")
	public void afterSwitchDS() {
		DataSourceContextHolder.clearDS();
	}
}
