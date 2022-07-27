package gov.utah.dts.openid.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public interface DataAccessConfig {

	AbstractPlatformTransactionManager transactionManager();

	LocalContainerEntityManagerFactoryBean entityManagerFactory();

	DataSource dataSource();

}
