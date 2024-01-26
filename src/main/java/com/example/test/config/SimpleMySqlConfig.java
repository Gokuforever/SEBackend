package com.example.test.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableJpaRepositories(basePackages = { "com.example.test.repository"}, entityManagerFactoryRef = "entityManager", transactionManagerRef = "transactionManager")
public class SimpleMySqlConfig {

	@Value("${spring.datasource.driver-class-name}")
	private String mysql_db_driver;

	@Value("${spring.datasource.url}")
	private String mysql_db_url;

	@Value("${spring.datasource.username}")
	private String mysql_db_username;

	@Value("${spring.datasource.password}")
	private String mysql_db_password;

//	@Primary
//	@Bean
	public DataSource dataSource() {

		// String dbPass = System.getenv("DB_AVANT_GARDE_PASS");
		final String decryptedDbPass = mysql_db_password;// PayGateCryptoUtils.decrypt(mysql_db_password, dbPass);

		final DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName(mysql_db_driver);
//		if (Constants.PROD.equals(env)) {
//			AwsSecrets secrets = getSecret();
//			driverManagerDataSource.setUrl(
//					"jdbc:" + secrets.getEngine() + "://" + secrets.getHost() + ":" + secrets.getPort() + database);
//			driverManagerDataSource.setUsername(secrets.getUsername());
//			driverManagerDataSource.setPassword(secrets.getPassword());
//		} else {
		driverManagerDataSource.setUrl(mysql_db_url);
		driverManagerDataSource.setUsername(mysql_db_username);
		driverManagerDataSource.setPassword(decryptedDbPass);
//		}

		// return
		// DataSourceBuilder.create().driverClassName(mysql_db_driver).url(mysql_db_url).username(mysql_db_username)
		// .password(decryptedDbPass).build();
		return driverManagerDataSource;

	}

	@Bean
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManager() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dataSource());
		em.setJpaVendorAdapter(jpaVendorAdapter());
		em.setPackagesToScan("com.example.test.entity");

		return em;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabase(Database.MYSQL);
		jpaVendorAdapter.setGenerateDdl(true);
		jpaVendorAdapter.setShowSql(true);
		return jpaVendorAdapter;
	}

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}
}
