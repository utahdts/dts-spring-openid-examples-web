package gov.utah.dts.openid.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

	@Bean
	@ConfigurationProperties(prefix = "datasource.primary")
	@Primary
	@Profile("primary")
	public DataSource primaryDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "datasource.unittest")
	@Profile("test")
	public DataSource unitTestDataSource() {
		return DataSourceBuilder.create().build();
	}

}
