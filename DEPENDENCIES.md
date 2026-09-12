# Project Dependencies Documentation

This document provides comprehensive information about all dependencies used in the POS System Spring Web MVC project.

## Project Properties

- **Spring Framework Version**: 6.1.4
- **Hibernate Version**: 6.4.8.Final
- **Java Version**: 17
- **Build Tool**: Maven
- **Packaging**: WAR (Web Application Archive)

---

## Dependencies Overview

### 1. Spring Framework Dependencies

#### 1.1 Spring Web MVC
- **Group ID**: `org.springframework`
- **Artifact ID**: `spring-webmvc`
- **Version**: 6.1.4
- **Scope**: compile (default)

**Description**: 
Spring Web MVC is the web framework for building web applications. It provides the Model-View-Controller (MVC) architecture for web applications, including:

- DispatcherServlet for handling HTTP requests
- Controller annotations (@Controller, @RequestMapping, @GetMapping, @PostMapping, etc.)
- View resolution and template integration
- Form handling and data binding
- Validation support
- RESTful web service capabilities

**Usage in Project**:
Used for handling HTTP requests, routing to controllers, and managing the web layer of the application.

---

#### 1.2 Spring Core
- **Group ID**: `org.springframework`
- **Artifact ID**: `spring-core`
- **Version**: 6.1.4
- **Scope**: compile (default)

**Description**:
Spring Core is the foundational module of the Spring Framework. It provides:

- IoC (Inversion of Control) container
- Dependency injection
- Bean lifecycle management
- Resource loading
- Expression language (SpEL)
- Utilities and helper classes

**Usage in Project**:
Provides the core infrastructure for dependency injection and bean management throughout the application.

---

#### 1.3 Spring Context
- **Group ID**: `org.springframework`
- **Artifact ID**: `spring-context`
- **Version**: 6.1.4
- **Scope**: compile (default)

**Description**:
Spring Context extends the core container and provides:

- Application context implementation
- Bean factory extensions
- Event publishing and listening
- Resource loading
- Internationalization (i18n) support
- Enterprise services integration (JNDI, EJB, etc.)
- Annotation-based configuration support

**Usage in Project**:
Used for application context management, component scanning, and annotation-based configuration (@Component, @Service, @Repository, @Autowired).

---

#### 1.4 Spring ORM
- **Group ID**: `org.springframework`
- **Artifact ID**: `spring-orm`
- **Version**: 6.1.4
- **Scope**: compile (default)

**Description**:
Spring ORM provides integration between Spring and ORM (Object-Relational Mapping) frameworks:

- Hibernate integration
- JPA integration
- Transaction management for ORM
- DAO support classes
- Exception translation

**Usage in Project**:
Integrates Spring with Hibernate/JPA for database operations, providing transaction management and DAO support.

---

### 2. Spring Data Dependencies

#### 2.1 Spring Data JPA
- **Group ID**: `org.springframework.data`
- **Artifact ID**: `spring-data-jpa`
- **Version**: 3.2.5
- **Scope**: compile (default)

**Description**:
Spring Data JPA simplifies data access with JPA repositories:

- Repository abstraction layer
- Automatic query generation
- Custom query methods
- Pagination and sorting support
- Auditing capabilities
- Query DSL integration

**Usage in Project**:
Provides repository interfaces (JpaRepository) for database operations, eliminating boilerplate DAO code.

---

### 3. Jakarta EE Dependencies

#### 3.1 Jakarta Servlet API
- **Group ID**: `jakarta.servlet`
- **Artifact ID**: `jakarta.servlet-api`
- **Version**: 6.0.0
- **Scope**: provided

**Description**:
Jakarta Servlet API provides the core servlet specification:

- HTTP servlet classes
- Request/response handling
- Session management
- Filter support
- Web application configuration

**Scope Note**: Marked as `provided` because the servlet container (e.g., Tomcat) provides this at runtime.

**Usage in Project**:
Required for compiling servlet-based web applications but not packaged in the WAR file.

---

#### 3.2 Jakarta Annotations
- **Group ID**: `jakarta.annotation`
- **Artifact ID**: `jakarta.annotation-api`
- **Version**: 2.1.1
- **Scope**: compile (default)

**Description**:
Jakarta Annotations provides common annotations:

- @Resource for dependency injection
- @PostConstruct and @PreDestroy for lifecycle callbacks
- @Generated for code generation
- Other common annotations

**Usage in Project**:
Used for lifecycle management annotations (@PostConstruct, @PreDestroy) and resource injection.

---

#### 3.3 Jakarta Persistence (JPA) API
- **Group ID**: `jakarta.persistence`
- **Artifact ID**: `jakarta.persistence-api`
- **Version**: 3.1.0
- **Scope**: compile (default)

**Description**:
Jakarta Persistence API (formerly Java Persistence API) provides:

- Entity annotations (@Entity, @Id, @GeneratedValue, etc.)
- EntityManager API
- JPQL (Java Persistence Query Language)
- Criteria API
- Transaction management
- Mapping annotations

**Usage in Project**:
Used for defining JPA entities, relationships, and database mappings.

---

### 4. Hibernate ORM

#### 4.1 Hibernate Core
- **Group ID**: `org.hibernate.orm`
- **Artifact ID**: `hibernate-core`
- **Version**: 6.4.8.Final
- **Scope**: compile (default)

**Description**:
Hibernate is the most popular JPA implementation providing:

- Object-relational mapping
- Automatic schema generation
- HQL (Hibernate Query Language)
- Caching (first-level and second-level)
- Connection pooling
- Transaction management
- Database dialects

**Usage in Project**:
Acts as the JPA provider for database operations, handling entity persistence and queries.

---

### 5. Database Driver

#### 5.1 PostgreSQL JDBC Driver
- **Group ID**: `org.postgresql`
- **Artifact ID**: `postgresql`
- **Version**: 42.7.11
- **Scope**: compile (default)

**Description**:
PostgreSQL JDBC driver enables Java applications to connect to PostgreSQL databases:

- JDBC 4.2+ compliance
- SSL/TLS support
- Connection pooling support
- Large object support
- Array support
- Notification/listen support

**Usage in Project**:
Provides database connectivity to PostgreSQL for the application's data persistence layer.

---

### 6. JSON Processing

#### 6.1 Jackson Databind
- **Group ID**: `com.fasterxml.jackson.core`
- **Artifact ID**: `jackson-databind`
- **Version**: 2.15.3
- **Scope**: compile (default)

**Description**:
Jackson is a high-performance JSON processor for Java:

- JSON serialization/deserialization
- Object mapping (POJO to JSON)
- Tree model for JSON processing
- Streaming API
- Custom serializers/deserializers
- Data format support (XML, YAML, etc.)

**Usage in Project**:
Used for converting Java objects to JSON and vice versa, typically for REST API responses and requests.

---

### 7. Testing Dependencies

#### 7.1 JUnit
- **Group ID**: `junit`
- **Artifact ID**: `junit`
- **Version**: 4.13.2
- **Scope**: test

**Description**:
JUnit is a unit testing framework for Java:

- @Test, @Before, @After annotations
- Assertions (assertEquals, assertTrue, etc.)
- Test runners
- Test suites
- Parameterized tests

**Scope Note**: Marked as `test` scope, only used during compilation and execution of tests.

**Usage in Project**:
Used for writing unit tests to verify application functionality.

---

## Dependency Relationships

```
Spring Web MVC
    ├── Spring Core (transitive)
    ├── Spring Context (transitive)
    └── Spring Beans (transitive)

Spring ORM
    ├── Spring Core (transitive)
    ├── Spring Context (transitive)
    └── Spring JDBC (transitive)

Spring Data JPA
    ├── Spring Core (transitive)
    ├── Spring Context (transitive)
    ├── Spring ORM (transitive)
    └── Spring TX (transitive)

Hibernate Core
    ├── Jakarta Persistence API (required)
    └── Jakarta Transactions API (transitive)
```

## Version Compatibility Matrix

| Dependency | Version | Java Version | Notes |
|------------|---------|--------------|-------|
| Spring Framework | 6.1.4 | 17+ | Requires Java 17 or higher |
| Hibernate | 6.4.8.Final | 11+ | Compatible with Java 17 |
| Spring Data JPA | 3.2.5 | 17+ | Requires Spring Framework 6.x |
| Jakarta Servlet API | 6.0.0 | 11+ | Jakarta EE 10 specification |
| Jakarta Persistence | 3.1.0 | 11+ | Jakarta EE 10 specification |
| PostgreSQL Driver | 42.7.11 | 8+ | Compatible with PostgreSQL 12+ |
| Jackson | 2.15.3 | 8+ | Latest stable version |
| JUnit | 4.13.2 | 8+ | Legacy version (JUnit 5 is newer) |

## Security Considerations

### Known Vulnerabilities
- **Jackson Databind 2.15.3**: This version includes fixes for several CVEs. Always check for security updates.
- **PostgreSQL Driver 42.7.11**: Recent version with security patches. Keep updated.

### Recommendations
1. Regularly update dependencies to the latest stable versions
2. Use tools like OWASP Dependency-Check to scan for vulnerabilities
3. Monitor security advisories for all dependencies
4. Consider upgrading from JUnit 4 to JUnit 5 for better testing capabilities

## Build Plugins

### Maven Compiler Plugin
- **Version**: 3.13.0
- **Configuration**:
  - Source: 17
  - Target: 17
  - Parameters: true (enables parameter name retention for reflection)

### Maven WAR Plugin
- **Version**: 3.4.0
- **Configuration**:
  - WAR Name: pos-system-spring-web-mvc

## Summary

This project uses a modern Spring Boot-like stack with:
- **Spring Framework 6.1.4** for core functionality and web MVC
- **Spring Data JPA 3.2.5** for repository abstraction
- **Hibernate 6.4.8.Final** as JPA provider
- **PostgreSQL** as the database
- **Jackson** for JSON processing
- **Jakarta EE 10** specifications (Servlet, Persistence, Annotations)

All dependencies are compatible with Java 17 and follow the latest Jakarta EE standards (post-Java EE migration).
