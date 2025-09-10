# E-Commerce Spring Boot API

## Project Overview

A complete backend for an e-commerce platform built with Spring Boot. This RESTful API provides comprehensive functionality for user management, product catalog, shopping cart, order processing, and secure authentication.

## Key Objectives Achieved

-  **RESTful API Development** - Complete e-commerce functionality
-  **JWT Authentication** - Secure user authentication and authorization  
-  **Email Verification** - SMTP-based account verification system
-  **Product Pagination & Filtering** - Efficient product browsing
-  **File Upload Support** - Product image management
-  **Input Validation** - Robust data integrity checks
-  **Comprehensive Exception Handling** - Graceful error management
-  **Unit Testing** - Extensive test coverage
-  **Spring Data JPA with PostgreSQL** - Production-ready database layer

## System Architecture

### Core Entities
- **User** - Customers and administrators with role-based access
- **Product** - Catalog items with inventory management
- **Category** - Product organization system
- **Order** - Customer purchases with status tracking
- **OrderItem** - Individual products within orders
- **Cart** - User shopping sessions with persistent storage

### Database Relationships
```
User (1) ↔ (1) Cart (1) ↔ (N) CartItem (N) ↔ (1) Product
User (1) ↔ (N) Order (1) ↔ (N) OrderItem (N) ↔ (1) Product  
Category (1) ↔ (N) Product
```

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 14+ 
- Git

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/your-username/ecommerce-spring-boot.git
cd ecommerce-spring-boot
```

2. **Set up PostgreSQL database**
```bash
# Create PostgreSQL database
createdb -U postgres "E-Commerce"
```

3. **Configure environment variables**
Create a `.env` file in the root directory:
```bash
# Database Configuration
DB_USER=your_postgres_username
DB_PASS=your_postgres_password

# JWT Configuration  
JWT_SECRET=your-very-secure-jwt-secret-key-with-at-least-32-characters

# Email Configuration
MAIL_PASS=your-gmail-app-password
```

4. **Build and run**
```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Core Features

### Security & Authentication
- JWT-based stateless authentication
- Role-based authorization (USER, ADMIN)
- Email verification system
- Password encryption with BCrypt
- Secure API endpoint protection

### Product Catalog
- Complete CRUD operations for products
- Category-based organization
- Inventory management with stock tracking
- Pagination and filtering capabilities

### Shopping Experience
- Persistent user shopping carts
- Real-time quantity updates
- Automatic price calculations
- Stock validation during cart operations
- Seamless checkout process

### Order Management
- Order creation from cart contents
- Status tracking workflow
- Inventory automatic deduction
- Order history and details

### User System
- User registration and profile management
- Email verification for security
- Role-based access control
- Personal order history

## Testing the API

### Available Test Methods
1. **Swagger UI** - Interactive API documentation at `/swagger-ui.html`
2. **Postman/Insomnia** - API client tools

### Sample Test Flow
1. Register a new user account
2. Verify email through the sent link
3. Login to obtain JWT token
4. Browse products with pagination
5. Add items to shopping cart
6. Proceed through checkout
7. View order history and details


### API Endpoints Overview

#### Authentication (`/auth`)
| Method |        Endpoint       |      Description     | Access |
|--------|---------------------- |----------------------|--------|
| POST   | `/auth/register`      | Register new user    | Public |
| GET    | `/auth/verify-email`  | Verify email address | Public |
| POST   | `/auth/login`         | User login           | Public |
| POST   | `/auth/refresh-token` |  Refresh JWT token   | Public |

#### Products (`/products`)
| Method |      Endpoint      |          Description         | Access |
|--------|--------------------|------------------------------|--------|
| GET    | `/products`        | Get all products (paginated) | Public |
| GET    | `/products/{id}`   | Get product by ID            | Public |
| POST   | `/products`        | Create new product           | ADMIN  |
| DELETE | `/products/{id}`   | Delete product               | ADMIN  |

#### Categories (`/categories`)
| Method |      Endpoint      |      Description      | Access |
|--------|--------------------|-----------------------|--------|
| GET    | `/categories`      | Get all categories    | Public |
| GET    | `/categories/{id}` | Get category by ID    | Public |
| POST   | `/categories`      | Create new category   | ADMIN  |
| PUT    | `/categories/{id}` | Update category       | ADMIN  |

#### Cart (`/cart`)
| Method |              Endpoint              |       Description        | Access |
|--------|------------------------------------|--------------------------|--------|
| POST   | `/cart/{userId}/add/{productId}`   | Add product to cart      | USER   |
| GET    | `/cart/{userId}`                   | Get user's cart          | USER   |
| DELETE | `/cart/{userId}/remove/{productId}`| Remove product from cart | USER   |

#### Orders (`/orders`)
| Method |          Endpoint           |         Description          |    Access   |
|--------|-----------------------------|------------------------------|-------------|
| POST   | `/orders/{userId}/checkout` | Checkout and create order    | USER, ADMIN |
| GET    | `/orders/user/{userId}`     | Get user's orders            | USER, ADMIN |
| GET    | `/orders/{orderId}`         | Get order details            | USER, ADMIN |

#### Users (`/users`)
| Method |     Endpoint      |      Description      |     Access    |
|--------|-------------------|-----------------------|---------------|
| GET    | `/users/me`       | Get current user info | Authenticated |
| PUT    | `/users/{userId}` | Update user profile   | USER, ADMIN   |


## Support & Resources

- **API Documentation**: Interactive Swagger UI at `/swagger-ui.html`
- **Test Data**: Pre-configured test data utilities included
- **Error Handling**: Consistent error responses with proper HTTP codes
- **Logging**: Comprehensive logging for debugging and monitoring

This project provides a **production-ready foundation** for any e-commerce platform, with all essential features implemented following industry best practices and security standards.

---

** Pro Tip**: Start by exploring the Swagger UI at `http://localhost:8080/swagger-ui.html` to understand all available endpoints and try them out interactively!
