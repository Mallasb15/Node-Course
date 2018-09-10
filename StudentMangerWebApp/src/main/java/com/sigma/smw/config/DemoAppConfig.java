package com.sigma.smw.config;

import java.beans.PropertyVetoException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("com.sigma.smw")
@PropertySource({ "persistence-mysql.properties", "security-persistence-mysql.properties" })
public class DemoAppConfig{

	// setup variable to hold up the properties
	@Autowired
	private Environment env;

	private Logger logger = Logger.getLogger(getClass().getName());

	// Define a bean for ViewResolver
	@Bean
	public ViewResolver viewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/view/");
		viewResolver.setSuffix(".jsp");

		return viewResolver;
	}

	// Define a bean for myDataSource

	@Bean
	public DataSource myDataSource() {

		// create connection pool
		ComboPooledDataSource myDataSource = new ComboPooledDataSource();

		// set JDBC driver
		try {
			myDataSource.setDriverClass(env.getProperty("jdbc.driver"));
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		// log the connection pool
		logger.info(">>> jdbc.url=" + env.getProperty("jdbc.url"));
		logger.info(">>> jdbc.user=" + env.getProperty("jdbc.user"));

		// set Database connection props
		myDataSource.setJdbcUrl(env.getProperty("jdbc.url"));
		myDataSource.setUser(env.getProperty("jdbc.user"));
		myDataSource.setPassword(env.getProperty("jdbc.password"));

		// set connection pool

		myDataSource.setInitialPoolSize(getIntProperty("connection.pool.initialPoolSize"));
		myDataSource.setMinPoolSize(getIntProperty("connection.pool.minPoolSize"));
		myDataSource.setMaxPoolSize(getIntProperty("connection.pool.maxPoolSize"));
		myDataSource.setMaxIdleTime(getIntProperty("connection.pool.maxIdleTime"));

		return myDataSource;
	}

	// Define Bean for security dataSource

	@Bean
	public DataSource securityDataSource() {

		// create connection pool
		ComboPooledDataSource securityDataSource = new ComboPooledDataSource();

		try {
			securityDataSource.setDriverClass(env.getProperty("security.jdbc.driver"));
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		// log the connection pool
		logger.info(">>> jdbc.url=" + env.getProperty("security.jdbc.url"));
		logger.info(">>> jdbc.user=" + env.getProperty("security.jdbc.user"));

		// Set Database Connection
		securityDataSource.setJdbcUrl(env.getProperty("security.jdbc.url"));
		securityDataSource.setUser(env.getProperty("security.jdbc.user"));
		securityDataSource.setPassword(env.getProperty("security.jdbc.password"));

		// Set connection Pool

		securityDataSource.setInitialPoolSize(getIntProperty("security.connection.pool.initialPoolSize"));
		securityDataSource.setMinPoolSize(getIntProperty("security.connection.pool.minPoolSize"));
		securityDataSource.setMaxPoolSize(getIntProperty("security.connection.pool.maxPoolSize"));
		securityDataSource.setMaxIdleTime(getIntProperty("security.connection.pool.maxIdleTime"));

		return securityDataSource;

	}

	// set up hibernate properites
	
		private Properties getHibernateProperties() {
		
			Properties props = new Properties();
			
			props.setProperty("hibernate.dialect",env.getProperty("hibernate.dialect"));
			props.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
			
			return props;
		}
		
	// Bean property for sessionFactory
		
		@Bean
		public LocalSessionFactoryBean sessionFactory() {
			
			LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
			
			// set the properties
			sessionFactory.setDataSource(myDataSource());
			sessionFactory.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
			sessionFactory.setHibernateProperties(getHibernateProperties());
			
			return sessionFactory;
			
		}
		
		@Bean
		@Autowired
		public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
			
			// setup transaction manager based on session factory
			HibernateTransactionManager txManager = new HibernateTransactionManager();
			txManager.setSessionFactory(sessionFactory);

			return txManager;
		}	
		
		
//	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//	        registry
//	          .addResourceHandler("/resources/**")
//	          .addResourceLocations("/resources/"); 
//	    }	
	
	private int getIntProperty(String propName) {

		String propVal = env.getProperty(propName);

		// now convert to integer 
		int intPropVal = Integer.parseInt(propVal);

		return intPropVal;
	}
}
