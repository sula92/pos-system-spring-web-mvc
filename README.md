# POS System - Spring Core Web MVC Implementation

This is a Spring Core Web MVC implementation of the POS (Point of Sale) System with the same API URLs and functionalities as the JDBC version.

## Features

- **Customer Management**: Create, Read, Update, and Delete customers
- **Item Management**: Create, Read, Update, and Delete items  
- **Order Management**: Place orders and retrieve order details
- **Transaction Support**: ACID transactions for order processing with stock management
- **Database**: PostgreSQL with automatic schema initialization
- **REST API**: Full REST API compatible with the React/TypeScript frontend

## API Endpoints

### Customer Endpoints
- `GET /customer` - Get all customers
- `GET /customer?id=C001` - Get a specific customer
- `POST /customer` - Create a new customer
- `PUT /customer` - Update a customer
- `DELETE /customer?id=C001` - Delete a customer

### Item Endpoints
- `GET /item` - Get all items
- `GET /item?code=I001` - Get a specific item
- `POST /item` - Create a new item
- `PUT /item` - Update an item
- `DELETE /item?code=I001` - Delete an item

### Order Endpoints
- `GET /order` - Get all orders
- `GET /order?id=O001` - Get a specific order
- `POST /order` - Place a new order

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Tomcat 9+ (or any Servlet 4.0 compatible server)

## Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE pos_system;
```

2. The application will automatically create the schema and initialize sample data on startup.

### Environment Variables (Optional)

Set these environment variables to customize the database connection:
- `POS_DB_URL` - Database URL (default: `jdbc:postgresql://localhost:5432/pos_system`)
- `POS_DB_USER` - Database username (default: `postgres`)
- `POS_DB_PASSWORD` - Database password (default: `postgres`)

## Building and Running

### Build the Project

```bash
cd pos-system-spring-web-mvc
mvn clean package
```

This creates a WAR file in the `target` directory.

### Deploy to Tomcat

1. Copy the generated WAR file to Tomcat's `webapps` directory:
```bash
cp target/pos-system-spring-web-mvc.war $CATALINA_HOME/webapps/
```

2. Start Tomcat:
```bash
$CATALINA_HOME/bin/startup.sh  # On Unix/Linux
$CATALINA_HOME/bin/startup.bat # On Windows
```

3. Access the application at `http://localhost:8080/pos-system-spring-web-mvc`

### Development Mode

For development, you can run the application using Maven:

```bash
mvn tomcat7:run
```

(Note: You'll need the Tomcat Maven plugin configured in pom.xml)

## Project Structure

```
pos-system-spring-web-mvc/
├── src/
│   ├── main/
│   │   ├── java/com/pos/
│   │   │   ├── config/          # Spring configuration classes
│   │   │   ├── controller/      # Spring REST controllers
│   │   │   ├── service/         # Business logic services
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── entity/          # Entity classes
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── db/              # Database utilities
│   │   │   └── listener/        # Application lifecycle listeners
│   │   ├── resources/
│   │   │   ├── applicationContext.xml   # Application context
│   │   │   └── servlet-config.xml       # Servlet configuration
│   │   └── webapp/
│   │       └── WEB-INF/
│   │           └── web.xml      # Deployment descriptor
│   └── test/                     # Test classes
└── pom.xml                       # Maven configuration
```

## API Response Format

All API responses follow a consistent JSON format.

### Success Response
```json
{
  "message": "Operation successful",
  "id": "C001"  // Or orderId, code, etc.
}
```

### Error Response
```json
{
  "error": "Error message describing what went wrong"
}
```

## CORS Configuration

The application is configured to accept requests from `http://localhost:5173` (React development server). To change this, modify the `@CrossOrigin` annotation in the controllers or update the `servlet-config.xml`.

## Transaction Management

Order placement uses ACID transactions to ensure data consistency:
1. Validates customer and items
2. Checks stock availability
3. Creates order and order details
4. Updates item stock
5. Commits or rolls back the entire transaction

## Logging

The application uses Java's built-in logging. Logs are output to the console by default.

## Troubleshooting

### Database Connection Error
- Ensure PostgreSQL is running
- Check database URL, username, and password
- Verify the `pos_system` database exists

### Port Already in Use
- Tomcat default port is 8080
- To change: Edit `$CATALINA_HOME/conf/server.xml` and change the port in the connector

### CORS Issues
- Ensure the frontend is running on `http://localhost:5173`
- Check the `@CrossOrigin` annotations in controller classes

## License

This project is part of the POS System demonstration.

