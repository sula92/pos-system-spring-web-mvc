package com.pos.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
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
@EnableTransactionManagement  // Enable @Transactional annotation processing
@ComponentScan("com.pos")
@EnableJpaRepositories("com.pos.repository")  // Enable Spring Data JPA repository scanning
public class WebMvcConfig {

    /**
     * Configure DataSource (Database Connection)
     * Reads database URL, username, password from system environment or uses defaults
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(
            System.getenv().getOrDefault("POS_DB_URL", "jdbc:postgresql://localhost:5432/pos_system")
        );
        dataSource.setUsername(
            System.getenv().getOrDefault("POS_DB_USER", "postgres")
        );
        dataSource.setPassword(
            System.getenv().getOrDefault("POS_DB_PASSWORD", "postgres")
        );
        return dataSource;
    }

    /**
     * Configure Entity Manager Factory with Hibernate
     * This is responsible for managing all JPA entity operations
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.pos.entity");  // Scan for @Entity classes
        
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(adapter);
        
        // Hibernate configuration properties
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.ddl-auto", "create-drop");  // Automatically create/drop tables
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        properties.put("hibernate.use_sql_comments", true);
        
        emf.setJpaPropertyMap(properties);
        return emf;
    }

    /**
     * Configure Transaction Manager for Hibernate
     * Enables automatic transaction management with Spring
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    /**
     * Configure View Resolver (if using JSP views - optional for REST API)
     */
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
    @PostConstruct
    public void init() {
        System.out.println("[POS-Spring] Spring Context initialized with Hibernate ORM");
        System.out.println("[POS-Spring] Database will be auto-initialized by Hibernate");
    }
}

