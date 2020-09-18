package com.xrlj.framework.config.ds.myself;

import com.xrlj.framework.config.ds.DynamicDataSource;
import com.xrlj.framework.dao.base.BaseRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(order = 10)
@EnableJpaRepositories(value = "${system.project.package-dao}", entityManagerFactoryRef = MyselfDbJpaConfigNames.entityManagerFactoryMyself, transactionManagerRef = MyselfDbJpaConfigNames.transactionManagerMyself, repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
public class MyselfDbJpaConfig {

	@Autowired
	protected Environment env;

	@Autowired
	@Qualifier("dynamicDataSource")
	protected DynamicDataSource dynamicDataSource; // 数据源

	@Bean(name = MyselfDbJpaConfigNames.entityManagerFactoryMyself)
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryMyself(EntityManagerFactoryBuilder builder) {
		LocalContainerEntityManagerFactoryBean emFactory = builder.dataSource(DataSourceSpyUtils.conversion(env,dynamicDataSource)).properties(myselfJpaProperties().getProperties())
				.packages(env.getProperty("system.project.package-entities")) // 设置数据表对应实体类所在位置
				.persistenceUnit(MyselfDbJpaConfigNames.unitName).build(); //设置持久化管理工厂别名
//		moreConfig(emFactory);
		return emFactory;
	}

	@Primary
	@Bean(name = MyselfDbJpaConfigNames.transactionManagerMyself)
	public PlatformTransactionManager transactionManagerMyself(EntityManagerFactoryBuilder builder) {
		return new JpaTransactionManager(entityManagerFactoryMyself(builder).getObject());
	}

	@ConfigurationProperties(prefix = "spring.myself-db.jpa")
	@Bean(name = "myselfJpaProperties")
	@Primary
	public JpaProperties myselfJpaProperties() {
		return new JpaProperties();
	}

	/*@PostConstruct
	public void init(){
		GenericConversionService conversionService = (GenericConversionService) DefaultConversionService.getSharedInstance();
		conversionService.addConverter(new JPAConverter());
	}*/

}
