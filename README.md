# Agricultural E-Commerce Platform

A complete, production-ready full-stack e-commerce website for agricultural products built with Spring Boot and MySQL.

## Features

- **User Authentication**: JWT-based authentication with registration and login
- **Product Management**: 50+ agricultural products across 10 categories
- **Shopping Cart**: Add, update, and remove items from cart
- **Order Management**: Place orders from cart or buy now directly
- **Admin Panel**: Manage products, categories, and orders
- **Search & Filter**: Search products and filter by category
- **Pagination**: Efficient product listing with pagination
- **Image Upload**: Admin can upload product images
- **Stock Management**: Real-time stock tracking and validation

## Technologies Used

- **Backend**: Spring Boot 3.2.0
- **Data Storage**: In-memory H2 (auto-configured, no setup required)
- **Security**: Spring Security with JWT
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Java Version**: 17+

## Data Storage (No External Database Needed)

By default the app uses an **in‑memory H2 database**, created automatically on startup.  
You do **not** need to install or configure MySQL or any other external database.

Key properties:

```properties
spring.datasource.url=jdbc:h2:mem:agriculdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.username=sa
spring.datasource.password=
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
```

All schema and sample data (admin user, categories, 50+ products) are created fresh each time you run the app.

## Prerequisites

- Java 17 or higher
- Maven 3.6+

## Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd E-Commerence
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

   The application will start on `http://localhost:8080`

4. **Data will be auto-populated** with:
   - Admin user: `admin` / `admin123`
   - 10 categories (Seeds, Fertilizers, Pesticides, Tools, Fruits, Vegetables, Grains, Pulses, Irrigation, Equipment)
   - 50+ products with real data, images, prices, and stock

## API Endpoints

### Authentication (Public)
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token

### Products (Public)
- `GET /api/products` - Get all products (with pagination, search, filter)
  - Query params: `page`, `size`, `categoryId`, `search`, `sortBy`
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/category/{categoryId}` - Get products by category

### Categories (Public)
- `GET /api/categories` - Get all categories
- `GET /api/categories/{id}` - Get category by ID

### Cart (Authenticated)
- `GET /api/cart` - Get user's cart
- `POST /api/cart/add?productId={id}&quantity={qty}` - Add item to cart
- `PUT /api/cart/item/{itemId}?quantity={qty}` - Update cart item quantity
- `DELETE /api/cart/item/{itemId}` - Remove item from cart

### Orders (Authenticated)
- `POST /api/orders` - Create order from cart
- `POST /api/orders/buy-now?productId={id}&quantity={qty}` - Buy now (direct order)
- `GET /api/orders` - Get user's orders
- `GET /api/orders/{id}` - Get order by ID

### Admin (Admin Role Required)
- `GET /api/admin/categories` - Get all categories
- `POST /api/admin/categories` - Create category
- `PUT /api/admin/categories/{id}` - Update category
- `DELETE /api/admin/categories/{id}` - Delete category
- `POST /api/products` - Create product
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product
- `POST /api/products/upload-image` - Upload product image
- `GET /api/orders/admin/all` - Get all orders
- `PUT /api/orders/{id}/status?status={status}` - Update order status

## Authentication

All authenticated endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Sample API Requests

### Register User
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123"
}
```

### Login
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

### Get Products (with pagination and search)
```bash
GET http://localhost:8080/api/products?page=0&size=12&search=tomato&categoryId=1
```

### Add to Cart
```bash
POST http://localhost:8080/api/cart/add?productId=1&quantity=2
Authorization: Bearer <token>
```

### Place Order
```bash
POST http://localhost:8080/api/orders
Authorization: Bearer <token>

Response:
{
  "message": "Order placed successfully",
  "orderId": 1,
  "totalAmount": 25.98,
  "status": "PENDING"
}
```

## Default Credentials

- **Admin**: `admin` / `admin123`
- **Regular users**: Register via `/api/auth/register`

## Project Structure

```
src/
├── main/
│   ├── java/com/agricultecommerce/
│   │   ├── AgricultecommerceApplication.java
│   │   ├── config/
│   │   │   ├── DataInitializer.java
│   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   ├── JwtRequestFilter.java
│   │   │   ├── JwtUtil.java
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── AdminController.java
│   │   │   ├── AuthController.java
│   │   │   ├── CartController.java
│   │   │   ├── CategoryController.java
│   │   │   ├── OrderController.java
│   │   │   └── ProductController.java
│   │   ├── dto/
│   │   │   ├── JwtResponse.java
│   │   │   ├── LoginRequest.java
│   │   │   └── RegisterRequest.java
│   │   ├── entity/
│   │   │   ├── Cart.java
│   │   │   ├── CartItem.java
│   │   │   ├── Category.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   ├── Product.java
│   │   │   └── User.java
│   │   ├── exception/
│   │   │   ├── BadRequestException.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java
│   │   ├── repository/
│   │   │   ├── CartItemRepository.java
│   │   │   ├── CartRepository.java
│   │   │   ├── CategoryRepository.java
│   │   │   ├── OrderItemRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   ├── ProductRepository.java
│   │   │   └── UserRepository.java
│   │   └── service/
│   │       ├── CartService.java
│   │       ├── CategoryService.java
│   │       ├── OrderService.java
│   │       ├── ProductService.java
│   │       └── UserService.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/agricultecommerce/
        └── AgricultecommerceApplicationTests.java
```

## Database Schema

The application uses the following entities:
- **User**: Users and admins
- **Category**: Product categories
- **Product**: Products with details, price, stock, images
- **Cart**: User shopping carts
- **CartItem**: Items in cart
- **Order**: Orders placed by users
- **OrderItem**: Items in orders

## Deployment

### Local Development
- Run `mvn spring-boot:run`
- Access API at `http://localhost:8080`

### Production Deployment
1. Update database credentials in `application.properties`
2. Set strong JWT secret
3. Build JAR: `mvn clean package`
4. Run: `java -jar target/agricul-ecommerce-0.0.1-SNAPSHOT.jar`

## Troubleshooting

- **Database Connection Issues**: Ensure MySQL is running and credentials are correct
- **Port Conflicts**: Change `server.port` in `application.properties`
- **JWT Errors**: Ensure JWT secret is set correctly
- **Compilation Errors**: Ensure Java 17+ and Maven are installed

## License

This project is for internship demonstration purposes.
