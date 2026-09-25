package com.pos.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Web MVC Configuration with Hibernate/JPA Support
 * 
 * This configuration:
 * 1. Sets up a DataSource for database connections (PostgreSQL)
 * 2. Configures Hibernate as the JPA provider
 * 3. Enables transaction management with @Transactional support
 * 4. Enables component scanning for @Service, @Repository, @Controller
 * 5. Enables Spring Data JPA repositories for automatic CRUD implementation
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@EnableTransactionManagement  // Enable @Transactional annotation processing. //without this annotation, @Transactional will not work
@ComponentScan("com.pos")
@EnableJpaRepositories("com.pos.repository")  // Enable Spring Data JPA repository scanning. without this annotation, @Repository will not work
@PropertySource("classpath:application.properties")
public class WebMvcConfig {

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String dbDriver;

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/pos_system}")
    private String dbUrl;

    @Value("${spring.datasource.username:postgres}")
    private String dbUser;

    @Value("${spring.datasource.password:postgres}")
    private String dbPassword;

    @Value("${spring.jpa.database-platform:org.hibernate.dialect.PostgreSQLDialect}")
    private String hibernateDialect;

    @Value("${spring.jpa.hibernate.ddl-auto:create-drop}")
    private String hibernateDdlAuto;

    @Value("${spring.jpa.show-sql:true}")
    private boolean showSql;

    @Value("${spring.jpa.properties.hibernate.format_sql:true}")
    private boolean formatSql;

    @Value("${spring.jpa.properties.hibernate.use_sql_comments:true}")
    private boolean useSqlComments;

    // Enables ${...} placeholder resolution for @Value in non-Boot Spring MVC config.
    @Bean
    public static org.springframework.context.support.PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new org.springframework.context.support.PropertySourcesPlaceholderConfigurer();
    }

    /**
     * Configure DataSource (Database Connection)
     * Reads database URL, username, password from system environment or uses defaults
     */
    //Datasource is a bean that provides a connection to the database and is used by the EntityManagerFactory
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }

    /**
     * Configure Entity Manager Factory with Hibernate
     * This is responsible for managing all JPA entity operations
     */
    //EntityManagerFactory is a bean that manages the entity operations. without this bean, @Repository will not work
    //LocalContainerEntityManagerFactoryBean is a bean that provides a container for the EntityManagerFactory. without this bean, @Repository will not work
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.pos.entity");  // Scan for @Entity classes
        
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(adapter);
        
        // Hibernate configuration properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.ddl-auto", hibernateDdlAuto);  // Automatically create/drop tables
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.format_sql", formatSql);
        properties.put("hibernate.use_sql_comments", useSqlComments);

        emf.setJpaPropertyMap(properties);
        return emf;
    }

    /**
     * Configure Transaction Manager for Hibernate
     * Enables automatic transaction management with Spring
     */
    //Transaction manager is a bean that manages the transaction. without this bean, @Transactional will not work
    //PlatformTransactionManager - The Spring interface for transaction management. It defines methods like commit(), rollback(), and getTransaction().
    /*
    * transactionManager bean + @EnableTransactionManagement + @Transactional annotation = Working transactions

The Complete Picture
@EnableTransactionManagement (on WebMvcConfig) - Tells Spring to look for @Transactional annotations and create transactional proxies around those methods
transactionManager bean - The actual engine that handles begin/commit/rollback operations
@Transactional (on service methods) - Marks which methods should be transactional
Without Any One of These
No @EnableTransactionManagement: Spring ignores @Transactional annotations completely
No transactionManager bean: Spring can't manage transactions (throws exception at startup)
No @Transactional: Methods run without transaction boundaries (no automatic rollback)
    * */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    /**
     * Configure View Resolver (if using JSP views - optional for REST API)
     */
    //View resolver is a bean that maps the view name to the actual view
    //InternalResourceViewResolver is a view resolver that maps the view name to the actual view
    //we dont use it here because we are using REST API (no JSP views).
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    /**
     * Initialize database schema on application startup
     * Note: With hibernate.ddl-auto=create-drop, Hibernate will automatically create tables
     * This method can be kept for additional initialization logic if needed
     */
    //This method is called after the bean is initialized. It is used for additional initialization logic
    //@PostConstruct is a lifecycle annotation that marks a method to be called after the bean is initialized
    @PostConstruct
    public void init() {
        System.out.println("[POS-Spring] Spring Context initialized with Hibernate ORM");
        System.out.println("[POS-Spring] Database will be auto-initialized by Hibernate");
    }

    /*
    *
    * 1. @PostConstruct - called after the bean is initialized
    * 2. @PreDestroy - called before the bean is destroyed
    *
    */
}

