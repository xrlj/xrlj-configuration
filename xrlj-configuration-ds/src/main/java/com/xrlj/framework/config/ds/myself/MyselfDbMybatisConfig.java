package com.xrlj.framework.config.ds.myself;

import com.xrlj.framework.config.ds.DynamicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@MapperScan(value = "${mybatis.mapper-scan-location}", sqlSessionTemplateRef = MyselfDbMybatisConfig.sqlSessionTemplate)
public class MyselfDbMybatisConfig {

	public final static String sqlSessionTemplate = "sqlSessionTemplate";

	@Autowired
	protected Environment env;

	@Autowired
	@Qualifier("dynamicDataSource")
	protected DynamicDataSource dynamicDataSource;

	@Bean(name = "sqlSessionFactory")
	@Primary
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(DataSourceSpyUtils.conversion(env,dynamicDataSource));
		factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml")); // 映射xml文件
		return factoryBean.getObject();
	}

	@Bean(name = MyselfDbMybatisConfig.sqlSessionTemplate)
	@Primary
	public SqlSessionTemplate sqlSessionTemplate(
			@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@Bean(name = "transactionManager")
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dynamicDataSource);
	}
}
