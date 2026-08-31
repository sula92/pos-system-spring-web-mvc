# POS System - Spring Web MVC Quick Start Guide

## Overview

This is a complete Spring Core Web MVC implementation of the POS System. It provides the same API endpoints and functionalities as the JDBC version but uses Spring Framework for enterprise-grade application development.

## What's Implemented

### ✅ Full Feature Parity with JDBC Version
- Customer CRUD operations
- Item CRUD operations  
- Order management with transaction support
- Stock management and deduction
- Automatic ID generation (C001, I001, O001, etc.)
- Same REST API URLs and response formats

### ✅ Spring Framework Features
- Annotation-driven controllers (@RestController, @RequestMapping)
- Service layer for business logic
- Repository layer for data access
- Automatic component scanning
- CORS support for React frontend
- Proper transaction management in order processing

### ✅ Database
- PostgreSQL with automatic schema initialization
- ACID transactions for order operations
- Data validation and constraints
- Foreign key relationships

## System Requirements

- **Java**: JDK 17 or higher
- **Maven**: 3.6 or higher  
- **PostgreSQL**: 12 or higher
- **Tomcat**: 9.0 or higher (or any Servlet 6.0 compatible container)
- **RAM**: Minimum 512MB
- **Disk**: Minimum 500MB for dependencies

## Installation & Setup

### 1. Prerequisites Installation

**PostgreSQL Installation** (if not already installed):

Windows:
```bash
# Download installer from https://www.postgresql.org/download/windows/
# Run installer and set password for postgres user (default is "postgres")
```

**Maven Installation**:
```bash
# Download from https://maven.apache.org/download.cgi
# Extract and add bin directory to PATH environment variable
```

**Tomcat Installation**:
```bash
# Download from https://tomcat.apache.org/download-9.cgi
# Extract to a directory (e.g., C:\tomcat)
# Set CATALINA_HOME environment variable
```

### 2. Database Setup

1. Create the PostgreSQL database:
```sql
CREATE DATABASE pos_system;
```

2. The application will automatically create tables and seed sample data on startup.

### 3. Build the Application

```bash
cd pos-system-spring-web-mvc
mvn clean package
```

This creates `pos-system-spring-web-mvc.war` in the `target/` directory.

### 4. Deploy to Tomcat

**Option A: Manual Deployment**

1. Copy the WAR file to Tomcat's webapps directory:
```bash
copy target\pos-system-spring-web-mvc.war %CATALINA_HOME%\webapps\
```

2. Start Tomcat:
```bash
# Windows
%CATALINA_HOME%\bin\startup.bat

# Linux/Mac
$CATALINA_HOME/bin/startup.sh
```

3. The application will be available at:
```
http://localhost:8080/pos-system-spring-web-mvc
```

**Option B: Embedded Tomcat (for development)**

Add the following to pom.xml and run `mvn spring-boot:run` (requires Spring Boot setup)

## API Documentation

### Base URL
```
http://localhost:8080/pos-system-spring-web-mvc
```

### Customer Endpoints

**Get All Customers**
```
GET /customer
```

**Get Specific Customer**
```
GET /customer?id=C001
```

**Create Customer**
```
POST /customer
Content-Type: application/json

{
  "name": "John Doe",
  "address": "123 Main St",
  "email": "john@example.com"
}
```

**Update Customer**
```
PUT /customer
Content-Type: application/json

{
  "id": "C001",
  "name": "Jane Doe",
  "address": "456 Oak Ave",
  "email": "jane@example.com"
}
```

**Delete Customer**
```
DELETE /customer?id=C001
```

### Item Endpoints

**Get All Items**
```
GET /item
```

**Get Specific Item**
```
GET /item?code=I001
```

**Create Item**
```
POST /item
Content-Type: application/json

{
  "description": "Product Name",
  "unitPrice": 99.99,
  "qtyOnHAnd": 50
}
```

**Update Item**
```
PUT /item
Content-Type: application/json

{
  "code": "I001",
  "description": "Updated Product",
  "unitPrice": 109.99,
  "qtyOnHAnd": 45
}
```

**Delete Item**
```
DELETE /item?code=I001
```

### Order Endpoints

**Get All Orders**
```
GET /order
```

**Get Specific Order**
```
GET /order?id=O001
```

**Place Order**
```
POST /order
Content-Type: application/json

{
  "date": "2026-08-31",
  "customerId": "C001",
  "orderDetails": [
    {
      "itemCode": "I001",
      "qty": 5,
      "unitPrice": 99.99
    },
    {
      "itemCode": "I002",
      "qty": 3,
      "unitPrice": 149.99
    }
  ]
}
```

## Configuration

### Database Connection

Set environment variables to customize database connection:

```powershell
# Windows PowerShell
$env:POS_DB_URL = "jdbc:postgresql://localhost:5432/pos_system"
$env:POS_DB_USER = "postgres"
$env:POS_DB_PASSWORD = "postgres"
```

Or create a `.env` file in the project root.

### CORS Configuration

The application accepts requests from `http://localhost:5173` (React dev server).

To change CORS origins, modify the `@CrossOrigin` annotation in:
- `com/pos/controller/CustomerController.java`
- `com/pos/controller/ItemController.java`
- `com/pos/controller/OrderController.java`

Example:
```java
@CrossOrigin(origins = "http://your-frontend-url:port")
```

### Server Port

Default Tomcat port is 8080. To change:

1. Edit `$CATALINA_HOME/conf/server.xml`
2. Find the connector element:
```xml
<Connector port="8080" protocol="HTTP/1.1" ...
```
3. Change port to your desired value
4. Restart Tomcat

## Frontend Integration

The React/TypeScript frontend connects to this Spring MVC backend using:

```typescript
const BASE = 'http://localhost:8080/pos-system-spring-web-mvc';

// Customer API
axios.get(`${BASE}/customer`)
axios.get(`${BASE}/customer?id=C001`)
axios.post(`${BASE}/customer`, data)
axios.put(`${BASE}/customer`, data)
axios.delete(`${BASE}/customer?id=C001`)

// Item API  
axios.get(`${BASE}/item`)
axios.get(`${BASE}/item?code=I001`)
axios.post(`${BASE}/item`, data)
axios.put(`${BASE}/item`, data)
axios.delete(`${BASE}/item?code=I001`)

// Order API
axios.get(`${BASE}/order`)
axios.get(`${BASE}/order?id=O001`)
axios.post(`${BASE}/order`, data)
```

## Project Structure

```
pos-system-spring-web-mvc/
├── src/
│   ├── main/
│   │   ├── java/com/pos/
│   │   │   ├── config/              # Spring configuration
│   │   │   │   ├── WebMvcConfig.java
│   │   │   │   └── DispatcherServletInitializer.java
│   │   │   ├── controller/          # REST controllers
│   │   │   │   ├── CustomerController.java
│   │   │   │   ├── ItemController.java
│   │   │   │   └── OrderController.java
│   │   │   ├── service/             # Business logic
│   │   │   │   ├── CustomerService.java
│   │   │   │   ├── ItemService.java
│   │   │   │   └── OrderService.java
│   │   │   ├── repository/          # Data access
│   │   │   │   ├── CustomerRepository.java
│   │   │   │   ├── ItemRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   └── OrderDetailRepository.java
│   │   │   ├── entity/              # Domain models
│   │   │   │   ├── CustomerEntity.java
│   │   │   │   ├── ItemEntity.java
│   │   │   │   ├── OrderEntity.java
│   │   │   │   └── OrderDetailEntity.java
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   ├── CustomerDTO.java
│   │   │   │   ├── ItemDTO.java
│   │   │   │   ├── OrderDTO.java
│   │   │   │   └── OrderDetailDTO.java
│   │   │   ├── db/                  # Database utilities
│   │   │   │   ├── Database.java
│   │   │   │   └── IdGenerator.java
│   │   │   └── listener/            # Application lifecycle
│   │   │       └── ApplicationStartupListener.java
│   │   ├── resources/
│   │   │   ├── servlet-config.xml   # Spring servlet config
│   │   │   └── applicationContext.xml
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── web.xml          # Deployment descriptor
│   └── test/                         # Unit tests
├── target/                           # Build output
├── pom.xml                           # Maven configuration
└── README.md                          # This documentation
```

## Logging

Logs are output to console. To customize logging, create a `logback.xml` in `src/main/resources`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

## Troubleshooting

### Issue: 404 Not Found on /pos-system-spring-web-mvc

**Solution**: The application context path must match the WAR file name. Rename or check deployment.

### Issue: Database Connection Error

**Solution**:
1. Verify PostgreSQL is running: `pg_isready -h localhost`
2. Check environment variables are set correctly
3. Verify database `pos_system` exists
4. Check database credentials in Database.java

### Issue: Port Already in Use (8080)

**Solution**:
1. Find process using port: `netstat -ano | findstr :8080` (Windows)
2. Kill the process or change Tomcat port in server.xml
3. Restart Tomcat

### Issue: Application Won't Start

**Solution**:
1. Check Tomcat logs: `$CATALINA_HOME/logs/catalina.out`
2. Ensure Java 17+ is installed: `java -version`
3. Verify all dependencies are resolved: `mvn dependency:tree`

## Building from Source

To rebuild after making changes:

```bash
cd pos-system-spring-web-mvc
mvn clean package
```

Then redeploy the WAR file to Tomcat.

## Development Tips

### Enable Debug Mode

1. In web.xml, add:
```xml
<init-param>
    <param-name>debug</param-name>
    <param-value>true</param-value>
</init-param>
```

2. Restart application

### Testing API Endpoints

Use curl or Postman:

```bash
# Get all customers
curl http://localhost:8080/pos-system-spring-web-mvc/customer

# Create a customer
curl -X POST http://localhost:8080/pos-system-spring-web-mvc/customer \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","address":"123 St","email":"test@example.com"}'
```

## Performance Considerations

- The application uses connection pooling for database connections
- Each request gets a fresh database connection
- Consider adding Spring Data JPA or Hikari connection pool for production
- Order processing uses transactions for data consistency

## Security Considerations

For production deployment:
1. Change database password from default
2. Enable HTTPS in Tomcat
3. Add authentication/authorization layer
4. Implement rate limiting for API endpoints
5. Add input validation beyond current regex patterns
6. Use prepared statements (already implemented)

## Migration from JDBC Version

This Spring MVC version is fully compatible with the existing React frontend. The API responses are identical to the JDBC version:

| Feature | JDBC | Spring MVC |
|---------|------|-----------|
| Customer CRUD | ✅ | ✅ |
| Item CRUD | ✅ | ✅ |
| Order Management | ✅ | ✅ |
| Transactions | ✅ | ✅ |
| CORS Support | ✅ (Manually added) | ✅ (@CrossOrigin) |
| Automatic ID Generation | ✅ | ✅ |
| Stock Management | ✅ | ✅ |
| Logging | ✅ | ✅ |

Simply update the `BASE` URL in the frontend to point to this deployment:

```typescript
// Before (JDBC)
const BASE = 'http://localhost:8090/customer';

// After (Spring MVC)
const BASE = 'http://localhost:8080/pos-system-spring-web-mvc/customer';
```

## Support & Documentation

For more information:
- Spring Framework: https://spring.io
- Spring Web MVC: https://docs.spring.io/spring-framework/reference/web/webmvc.html
- PostgreSQL: https://www.postgresql.org/docs/
- Tomcat: https://tomcat.apache.org/

## License

This project is part of the POS System demonstration.

---

**Last Updated**: August 31, 2026  
**Version**: 1.0.0

