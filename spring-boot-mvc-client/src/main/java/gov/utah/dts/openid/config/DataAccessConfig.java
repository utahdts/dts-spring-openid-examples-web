package gov.utah.dts.openid.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = {"gov.utah.dts.openid.model"})
@EnableJpaRepositories(basePackages = {"gov.utah.dts.openid.repository"})
public class DataAccessConfig {

	@Autowired
	private JpaProperties jpaProperties;

	@Bean
	@ConfigurationProperties(prefix = "datasource.primary")
	@Primary
	@Profile("primary")
	public DataSource primaryDataSource() {
//		Map<String, String> existingProperties = jpaProperties.getProperties();
//		existingProperties.put("hibernate.hbm2ddl.auto", "create-drop");
//		existingProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//		existingProperties.put("hibernate.globally_quoted_identifiers","true");
//		existingProperties.put("hibernate.hbm2ddl.import_files","classpath:db/data.sql");)

		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "datasource.unittest")
	@Profile("test")
	public DataSource unitTestDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public AbstractPlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(primaryDataSource());
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(primaryDataSource());
		em.setPackagesToScan(new String[] { "gov.utah.dts.openid.model" });

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaProperties(hibernateProperties());
		em.setJpaVendorAdapter(vendorAdapter);

		return em;
	}

	Properties hibernateProperties() {
		return new Properties() {
			{
				setProperty("hibernate.hbm2ddl.auto", "create-drop");
				setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
				setProperty("hibernate.globally_quoted_identifiers","true");
				setProperty("hibernate.hbm2ddl.import_files","classpath:db/data.sql");
			}
		};
	}

}