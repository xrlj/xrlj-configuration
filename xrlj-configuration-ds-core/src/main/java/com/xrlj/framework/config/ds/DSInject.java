package com.xrlj.framework.config.ds;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DSInject {
	String value() default "";
}
