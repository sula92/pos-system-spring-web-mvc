# Hibernate ORM Migration Guide

## Overview

This document explains the migration of `pos-system-spring-web-mvc` from raw JDBC to **Hibernate ORM** with Spring Data JPA. Hibernate provides object-relational mapping, automatic SQL generation, transaction management, and lazy loading of entities.

---

## Table of Contents

1. [Why Hibernate?](#why-hibernate)
2. [Architecture Changes](#architecture-changes)
3. [Key Components](#key-components)
4. [Configuration](#configuration)
5. [Entity Mapping](#entity-mapping)
6. [Repository Layer](#repository-layer)
7. [Service Layer](#service-layer)
8. [Transaction Management](#transaction-management)
9. [How to Run](#how-to-run)
10. [Migration Checklist](#migration-checklist)

---

## Why Hibernate?

### Before (Raw JDBC)
```java
try (Connection conn = Database.getConnection();
     PreparedStatement ps = conn.prepareStatement(
        "INSERT INTO customers (id, name, address, email) VALUES (?, ?, ?, ?)")) {
    ps.setString(1, entity.getId());
    ps.setString(2, entity.getName());
    ps.setString(3, entity.getAddress());
    ps.setString(4, entity.getEmail());
    return ps.executeUpdate() > 0;
}
```

### After (Hibernate)
```java
@Autowired
private CustomerRepository customerRepository;

public CustomerDTO saveCustomer(CustomerDTO dto) {
    CustomerEntity entity = new CustomerEntity(dto.getId(), dto.getName(), dto.getAddress(), dto.getEmail());
    CustomerEntity saved = customerRepository.save(entity);  // Hibernate generates SQL automatically!
    return convertToDTO(saved);
}
```

### Benefits
1. **No SQL Writing**: Hibernate generates SQL based on annotations
2. **Type Safety**: Working with Java objects instead of SQL strings
3. **Lazy Loading**: Relationships are loaded on demand
4. **Transaction Management**: `@Transactional` handles begin/commit/rollback automatically
5. **Query Caching**: Hibernate caches query results for performance
6. **Multi-Database Support**: Easy to switch databases (PostgreSQL, MySQL, Oracle, etc.)
7. **Less Boilerplate**: No manual connection/statement/resultset closing

---

## Architecture Changes

### JDBC Architecture (Old)
```
Controller 
  ↓
Service (manual transactions, IdGenerator)
  ↓
Repository (manual JDBC with PreparedStatements)
  ↓
Database.getConnection()
  ↓
PostgreSQL DB
```

### Hibernate Architecture (New)
```
Controller 
  ↓
Service (@Transactional annotation)
  ↓
Spring Data JPA Repository Interface
  ↓
Hibernate (generates SQL & manages relationships)
  ↓
DataSource (connection pooling)
  ↓
PostgreSQL DB
```

---

## Key Components

### 1. Dependencies Added (pom.xml)

```xml
<!-- Spring ORM Support -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>${spring.version}</version>
</dependency>

<!-- Jakarta Persistence (JPA) API -->
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>

<!-- Hibernate ORM -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.4.8.Final</version>
</dependency>
```

### 2. Configuration Files

#### `application.properties` (New)
```properties
# Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/pos_system
spring.datasource.username=postgres
spring.datasource.password=postgres

# Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

#### `WebMvcConfig.java` (Updated)
- Configures `DataSource` for database connection pooling
- Creates `EntityManagerFactory` with Hibernate
- Enables `@EnableTransactionManagement` for `@Transactional` support
- Enables `@EnableJpaRepositories` for Spring Data JPA scanning

---

## Entity Mapping

### JDBC Entity (Old)
```java
public class CustomerEntity {
    private String id;
    private String name;
    private String address;
    private String email;
    
    // Getters/setters
}
```

### Hibernate Entity (New)
```java
@Entity
@Table(name = "customers")
public class CustomerEntity {
    
    @Id
    @Column(name = "id", length = 10)
    private String id;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "address", length = 255)
    private String address;
    
    @Column(name = "email", length = 150)
    private String email;
    
    // Getters/setters
}
```

### Key Annotations

| Annotation | Purpose |
|-----------|---------|
| `@Entity` | Marks class as JPA entity (maps to DB table) |
| `@Table` | Specifies table name and other table-level properties |
| `@Id` | Marks field as primary key |
| `@Column` | Maps field to specific column with constraints |
| `@Embeddable` | For composite keys (like `OrderDetailId`) |
| `@EmbeddedId` | References an `@Embeddable` composite key |
| `@OneToMany` | One-to-many relationship (Order has many OrderDetails) |
| `@ManyToOne` | Many-to-one relationship (OrderDetail belongs to Order) |
| `@JoinColumn` | Specifies foreign key column |

### Entity Relationships

#### Order ↔ OrderDetail (One-to-Many)
```java
@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    private String orderId;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetailEntity> orderDetails = new ArrayList<>();
}

@Entity
@Table(name = "order_details")
public class OrderDetailEntity {
    @EmbeddedId
    private OrderDetailId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderEntity order;
}
```

### Composite Keys

For `OrderDetailEntity` with composite key (order_id, item_code):

```java
@Embeddable
public class OrderDetailId implements Serializable {
    private String orderId;
    private String itemCode;
    
    @Override
    public boolean equals(Object o) { ... }
    
    @Override
    public int hashCode() { ... }
}
```

---

## Repository Layer

### JDBC Repository (Old)
```java
public class CustomerRepository {
    
    public boolean save(CustomerEntity entity) {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(...)) {
            ps.setString(1, entity.getId());
            // ... more setters ...
            return ps.executeUpdate() > 0;
        }
    }
    
    public List<CustomerEntity> findAll() {
        // Manual SQL query execution
    }
}
```

### Spring Data JPA Repository (New)
```java
@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String> {
    // JpaRepository provides:
    // - save(entity)
    // - findById(id)
    // - findAll()
    // - delete(id)
    // - And many more!
    
    // Custom queries can be added:
    // @Query("SELECT c FROM CustomerEntity c WHERE c.name = :name")
    // List<CustomerEntity> findByName(@Param("name") String name);
}
```

### JpaRepository Methods (Automatically Provided)

| Method | SQL Generated |
|--------|---------------|
| `save(entity)` | `INSERT INTO ... ON CONFLICT UPDATE` or `INSERT INTO ...` |
| `findById(id)` | `SELECT * FROM ... WHERE id = ?` |
| `findAll()` | `SELECT * FROM ...` |
| `deleteById(id)` | `DELETE FROM ... WHERE id = ?` |
| `delete(entity)` | `DELETE FROM ... WHERE id = ?` |
| `count()` | `SELECT COUNT(*) FROM ...` |
| `existsById(id)` | `SELECT 1 FROM ... WHERE id = ?` |

---

## Service Layer

### JDBC Service (Old)
```java
@Service
public class CustomerService {
    
    private CustomerRepository repo = new CustomerRepository();  // Manual instantiation
    
    public boolean saveCustomer(CustomerDTO dto) {
        try (Connection connection = Database.getConnection()) {
            String generatedId = IdGenerator.nextCustomerId(connection);  // Manual ID generation
            // ... more code ...
            return repo.save(entity);
        } catch (SQLException e) {
            throw new RuntimeException("Failed", e);
        }
    }
}
```

### Hibernate Service (New)
```java
@Service
@Transactional  // Spring manages transactions automatically!
public class CustomerService {
    
    @Autowired  // Spring injects the repository
    private CustomerRepository customerRepository;
    
    public CustomerDTO saveCustomer(CustomerDTO dto) {
        // Transaction starts automatically
        CustomerEntity entity = new CustomerEntity(dto.getId(), dto.getName(), ...);
        CustomerEntity saved = customerRepository.save(entity);  // Hibernate generates SQL
        // Transaction commits automatically at method end (no explicit commit needed!)
        return new CustomerDTO(saved.getId(), saved.getName(), ...);
    }
    
    @Transactional(readOnly = true)  // Read-only transactions are optimized
    public List<CustomerDTO> findAllCustomers() {
        // ... implementation ...
    }
}
```

---

## Transaction Management

### JDBC Transaction Management (Old)
```java
try (Connection connection = Database.getConnection()) {
    try {
        connection.setAutoCommit(false);            // BEGIN TRANSACTION
        // ... do work ...
        connection.commit();                        // COMMIT
    } catch (Exception e) {
        connection.rollback();                      // ROLLBACK on error
        throw e;
    } finally {
        connection.setAutoCommit(true);
    }
}
```

### Hibernate Transaction Management (New)
```java
@Service
@Transactional  // That's it! Spring handles begin/commit/rollback
public class OrderService {
    
    public OrderDTO placeOrder(OrderDTO dto) {
        // Transaction starts here automatically
        CustomerEntity customer = customerRepository.findById(dto.getCustomerId())
            .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        
        // Validation logic...
        
        OrderEntity order = orderRepository.save(new OrderEntity(...));
        
        for (OrderDetailDTO detail : dto.getOrderDetails()) {
            orderDetailRepository.save(new OrderDetailEntity(...));
            itemRepository.save(updatedItem);  // Update stock
        }
        
        // Transaction commits automatically here (if no exception)
        // If ANY exception occurs, ALL saves are rolled back automatically!
        
        return convertToDTO(order);
    }
}
```

### Transaction Isolation Levels

```java
@Transactional(isolation = Isolation.READ_COMMITTED)
public void updateInventory() { ... }

@Transactional(readOnly = true)  // Optimizes read-only queries
public List<OrderDTO> findAll() { ... }

@Transactional(propagation = Propagation.REQUIRES_NEW)
public void auditLog() { ... }
```

---

## Configuration

### `WebMvcConfig.java` (Hibernate Setup)

```java
@Configuration
@EnableWebMvc
@EnableTransactionManagement              // Enables @Transactional
@ComponentScan("com.pos")
@EnableJpaRepositories("com.pos.repository")  // Scans for repositories
public class WebMvcConfig {
    
    /**
     * DataSource: Connection pool to PostgreSQL
     */
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
            .driverClassName("org.postgresql.Driver")
            .url(env.getProperty("spring.datasource.url"))
            .username(env.getProperty("spring.datasource.username"))
            .password(env.getProperty("spring.datasource.password"))
            .build();
    }
    
    /**
     * EntityManagerFactory: Main Hibernate component
     * Manages entity lifecycle, caching, and SQL generation
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("com.pos.entity");  // Scans for @Entity classes
        
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(adapter);
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.ddl-auto", "create-drop");  // Auto schema management
        properties.put("hibernate.show_sql", true);           // Log SQL queries
        properties.put("hibernate.format_sql", true);         // Pretty-print SQL
        
        emf.setJpaPropertyMap(properties);
        return emf;
    }
    
    /**
     * PlatformTransactionManager: Manages transaction lifecycle
     * Works with @Transactional annotation
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
```

### `application.properties` (Database Configuration)

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/pos-system

# Database Connection
spring.datasource.url=${POS_DB_URL:jdbc:postgresql://localhost:5432/pos_system}
spring.datasource.username=${POS_DB_USER:postgres}
spring.datasource.password=${POS_DB_PASSWORD:postgres}
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true

# Connection Pool (HikariCP)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

## How to Run

### Prerequisites
1. Java 17+
2. PostgreSQL is running (default: localhost:5432)
3. Maven installed

### Setup

```bash
# 1. Clone/navigate to the project
cd pos-system-spring-web-mvc

# 2. Set environment variables (optional, uses defaults if not set)
$env:POS_DB_URL = "jdbc:postgresql://localhost:5432/pos_system"
$env:POS_DB_USER = "postgres"
$env:POS_DB_PASSWORD = "postgres"

# 3. Build the project
mvn clean package

# 4. Deploy to Tomcat or run with embedded server
# Option A: Maven Tomcat plugin
mvn tomcat7:run

# Option B: Deploy to standalone Tomcat
# Copy target/pos-system-spring-web-mvc.war to Tomcat webapps/

# Option C: Spring Boot embedded server (if using Spring Boot)
java -jar target/pos-system-spring-web-mvc.jar
```

### Testing Endpoints

```bash
# Get all customers
curl -X GET http://localhost:8080/pos-system/customer

# Get specific customer
curl -X GET "http://localhost:8080/pos-system/customer?id=C001"

# Create customer
curl -X POST http://localhost:8080/pos-system/customer \
  -H "Content-Type: application/json" \
  -d '{"id":"C005","name":"John","address":"Colombo","email":"john@example.com"}'

# Place order
curl -X POST http://localhost:8080/pos-system/order \
  -H "Content-Type: application/json" \
  -d '{
    "customerId":"C001",
    "date":"2026-08-31",
    "orderDetails":[
      {"itemCode":"I001","qty":2,"unitPrice":120.00}
    ]
  }'
```

---

## Migration Checklist

- [ ] Add Hibernate/JPA dependencies to `pom.xml`
- [ ] Create `application.properties` with database configuration
- [ ] Update entity classes with `@Entity`, `@Table`, `@Column` annotations
- [ ] Create `OrderDetailId` class for composite key
- [ ] Replace JDBC repositories with Spring Data JPA interfaces
- [ ] Update service classes with `@Transactional` annotation
- [ ] Update `WebMvcConfig.java` with Hibernate configuration
- [ ] Remove JDBC-based `Database.java` and `IdGenerator.java` usage
- [ ] Test CRUD operations on all entities
- [ ] Test transaction rollback on order placement failure
- [ ] Verify all API endpoints work with React frontend
- [ ] Performance test with sample data
- [ ] Document custom queries added to repositories

---

## Common Issues & Solutions

### Issue 1: "No qualifying bean of type 'EntityManagerFactory'"
**Cause**: Repository is not being scanned
**Solution**: Add `@EnableJpaRepositories("com.pos.repository")` to WebMvcConfig

### Issue 2: "Detached entity passed to persist"
**Cause**: Trying to save an entity that was loaded outside a transaction
**Solution**: Ensure the service method is annotated with `@Transactional`

### Issue 3: "Unknown column in field list"
**Cause**: Column name in `@Column` doesn't match database table
**Solution**: Verify column names match database schema

### Issue 4: "No row with the given identifier exists"
**Cause**: Trying to fetch entity that doesn't exist
**Solution**: Use `Optional` with `orElse(null)` or `orElseThrow()`

### Issue 5: LazyInitializationException
**Cause**: Accessing lazy-loaded relationship outside of transaction
**Solution**: Use `@Transactional(readOnly = true)` on read methods or set `fetch = FetchType.EAGER`

---

## Performance Tips

1. **Use `readOnly = true` for queries**
   ```java
   @Transactional(readOnly = true)
   public List<CustomerDTO> findAllCustomers() { ... }
   ```

2. **Use pagination for large datasets**
   ```java
   Page<CustomerEntity> customers = customerRepository.findAll(PageRequest.of(0, 10));
   ```

3. **Use `@Query` for complex queries**
   ```java
   @Query("SELECT c FROM CustomerEntity c WHERE c.name LIKE %:name%")
   List<CustomerEntity> findByNameContaining(@Param("name") String name);
   ```

4. **Enable query caching**
   ```properties
   spring.jpa.properties.hibernate.cache.use_second_level_cache=true
   ```

---

## Next Steps

1. Run the application and test all endpoints
2. Monitor Hibernate SQL logs to understand generated queries
3. Add custom `@Query` methods to repositories as needed
4. Implement pagination for large result sets
5. Add caching layer for frequently accessed data
6. Consider using Specification or QueryDSL for complex queries

---

## References

- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
- [Hibernate Official Documentation](https://hibernate.org/orm/documentation)
- [JPA Annotations Reference](https://jakarta.ee/specifications/persistence/)
- [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/)


