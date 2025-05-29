# 🛒 A Online Store made with Spring Boot

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Maven](https://img.shields.io/badge/Maven-Build%20Tool-blue.svg)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Template%20Engine-green.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

A modern, minimalist online store application built with Spring Boot. Features a complete e-commerce solution with user authentication, product management, shopping cart functionality, and comprehensive admin tools.

## ✨ Features

### 🛍️ Customer Features

- **User Authentication** - Registration and login system with role-based access
- **Product Catalog** - Browse products with category filtering and search functionality
- **Shopping Cart** - Add, remove, and update product quantities with real-time calculations
- **Checkout Process** - Streamlined checkout with payment simulation
- **Order Management** - Track order history and status updates
- **Responsive Design** - Modern, mobile-friendly interface

### 👑 Admin Features

- **Admin Dashboard** - Comprehensive analytics and management overview
- **Product Management** - CRUD operations with bulk actions and CSV export
- **User Management** - Manage customer accounts and permissions
- **Order Processing** - Process orders with status tracking (PENDING, PROCESSED, CANCELED)
- **Inventory Control** - Track stock levels and manage product availability
- **Bulk Operations** - Efficient management of multiple items

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git
- Docker (optional, for containerized deployment)

### Installation Options

#### Option 1: Direct Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd online_store
   ```

2. **Build the project**

   ```bash
   ./mvnw clean install
   ```

3. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

#### Option 2: Docker Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd online_store
   ```

2. **Run with Docker Compose**

   ```bash
   docker-compose up -d
   ```

#### Access the Application

- Open your browser and navigate to `http://localhost:8080`
- The application will start with pre-loaded demo data

## 🔐 Demo Credentials

The application comes with pre-configured demo accounts:

| Role     | Username   | Password      | Access Level                          |
| -------- | ---------- | ------------- | ------------------------------------- |
| Admin    | `admin`    | `admin123`    | Full admin access + customer features |
| Customer | `customer` | `customer123` | Shopping and order management         |

## 📱 Usage Examples

### Customer Workflow

1. **Register/Login** - Create account or use demo credentials
2. **Browse Products** - Explore catalog with category filters
3. **Add to Cart** - Select products and quantities
4. **Checkout** - Complete purchase with payment simulation
5. **Track Orders** - Monitor order status and history

### Admin Workflow

1. **Login as Admin** - Use admin credentials
2. **Dashboard Overview** - View sales analytics and metrics
3. **Manage Products** - Add, edit, or remove products
4. **Process Orders** - Update order statuses
5. **Export Data** - Download reports in CSV format

## 🏗️ Architecture

### Technology Stack

- **Backend**: Spring Boot 3.5.0 with Spring Web, Thymeleaf, Validation
- **Frontend**: Thymeleaf templates with responsive CSS and modern UI components
- **Build Tool**: Maven with wrapper for cross-platform builds
- **Data Storage**: In-memory repositories (no external database required)
- **Security**: Role-based authentication system
- **Containerization**: Docker with multi-stage builds for optimized deployments

### Project Structure

```
src/main/java/com/sandmor/online_store/
├── OnlineStoreApplication.java    # Main application entry point
├── config/                        # Configuration classes
│   └── DataInitializer.java       # Demo data initialization
├── controller/                    # Web controllers
│   ├── AdminController.java       # Admin functionality
│   ├── AuthController.java        # Authentication
│   ├── CartController.java        # Shopping cart
│   └── HomeController.java        # Public pages
├── dto/                           # Data transfer objects
├── model/                         # Entity models
│   ├── User.java                  # User entity
│   ├── Product.java               # Product entity
│   ├── Order.java                 # Order entity
│   └── ShoppingCart.java          # Cart entity
├── repository/                    # Data access layer
└── service/                       # Business logic layer

src/main/resources/
├── application.properties        # Application configuration
├── static/css/                   # Stylesheets
│   └── style.css                 # Main stylesheet
└── templates/                    # Thymeleaf templates
    ├── admin/                    # Admin interface templates
    ├── auth/                     # Authentication templates
    ├── cart/                     # Shopping cart templates
    ├── orders/                   # Order management templates
    └── products/                 # Product catalog templates
```

### Key Components

- **Models**: User, Product, Category, Order, OrderItem, ShoppingCart, CartItem
- **Services**: UserService, ProductService, CategoryService, OrderService, ShoppingCartService
- **Controllers**: HomeController, AuthController, CartController, OrderController, AdminController
- **Repositories**: In-memory implementations for all entities

## 🛠️ Development

### Setup Development Environment

1. **IDE Configuration**

   - Import as Maven project
   - Set Java 17 as project SDK
   - Enable annotation processing

2. **Run in Development Mode**

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **Hot Reload**
   - Spring Boot DevTools is included for automatic restarts
   - Templates are reloaded without restart

### Running Tests

```bash
./mvnw test
```

### Building for Production

```bash
./mvnw clean package
java -jar target/online_store-0.0.1-SNAPSHOT.jar
```

## 📋 API Endpoints

### Public Endpoints

- `GET /` - Home page with product listings
- `GET /products` - Product catalog
- `GET /products/{id}` - Product details
- `GET /auth/login` - Login page
- `POST /auth/login` - Login processing
- `GET /auth/register` - Registration page
- `POST /auth/register` - Registration processing

### Authenticated Endpoints

- `GET /cart` - View shopping cart
- `POST /cart/add` - Add item to cart
- `GET /orders` - Order history
- `POST /checkout` - Process checkout

### Admin Endpoints

- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/products` - Product management
- `GET /admin/orders` - Order management
- `GET /admin/users` - User management

## 🔧 Configuration

### Application Properties

```properties
# Server configuration
server.port=8080

# Application settings
spring.application.name=online_store

# Development settings
spring.devtools.restart.enabled=true
```

### Customization

- **Products**: Modify `DataInitializer.java` to change initial product data
- **Styling**: Update `src/main/resources/static/css/style.css`
- **Templates**: Customize Thymeleaf templates in `src/main/resources/templates/`

## 🚀 Deployment

### Local Deployment

```bash
./mvnw clean package
java -jar target/online_store-0.0.1-SNAPSHOT.jar
```

### Docker Deployment

#### Building the Docker Image

The project includes a multi-stage Dockerfile for optimized production deployments:

```bash
# Build the Docker image
docker build -t online-store:latest .

# Build with custom tag
docker build -t online-store:v1.0.0 .
```

#### Running the Container

```bash
# Run the container on port 8080
docker run -d \
  --name online-store-app \
  -p 8080:8080 \
  online-store:latest

# Run with custom port mapping
docker run -d \
  --name online-store-app \
  -p 3000:8080 \
  online-store:latest

# Run with environment variables
docker run -d \
  --name online-store-app \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  online-store:latest
```

#### Docker Compose

The project includes a pre-configured `docker-compose.yml` file for easier management:

```bash
# Start the application
docker-compose up -d

# View logs
docker-compose logs -f

# Stop the application
docker-compose down

# Rebuild and restart
docker-compose up --build -d
```

#### Container Management

```bash
# View running containers
docker ps

# View logs
docker logs online-store-app

# Access container shell
docker exec -it online-store-app sh

# Stop container
docker stop online-store-app

# Remove container
docker rm online-store-app

# Remove image
docker rmi online-store:latest
```

#### Docker Features

- **Multi-stage build**: Optimized image size using separate build and runtime stages
- **Security**: Runs as non-root user for enhanced security
- **Health checks**: Built-in health monitoring
- **Alpine Linux**: Lightweight base image for smaller footprint
- **Layer caching**: Efficient rebuilds through optimized layer structure

### Cloud Deployment

The application is ready for deployment on:

- Heroku
- AWS Elastic Beanstalk
- Google Cloud Platform
- Azure App Service

### Development Guidelines

- Follow Spring Boot best practices
- Update documentation for API changes
- Use meaningful commit messages

## 🐛 Troubleshooting

### Common Issues

**Port Already in Use**

```bash
# Kill process using port 8080
sudo lsof -ti:8080 | xargs kill -9
```

**Maven Build Issues**

```bash
# Clean and rebuild
./mvnw clean install -U
```

**Docker Container Issues**

```bash
# Check if Docker is running
docker --version
docker-compose --version

# View container logs
docker-compose logs online-store

# Restart containers
docker-compose restart
```

**Java Version Issues**

```bash
# Check Java version
java -version
# Should be Java 17 or higher

# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/java17
```

## 📈 Roadmap

### Planned Features

- [ ] Spring Security integration
- [ ] Database persistence (PostgreSQL/MySQL support)
- [ ] Payment gateway integration
- [ ] Email notifications for orders
- [ ] Product reviews and ratings
- [ ] Advanced search and filtering
- [ ] REST API endpoints
- [ ] Mobile app companion

### Known Limitations

- In-memory data storage (data resets on restart)
- Basic authentication system (no password encryption)
- Simulated payment processing
- No real-time inventory updates

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:

- Check existing documentation
- Create an issue in the repository and I may look at it
- Review the troubleshooting section and/or send a PR, you are welcome

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Thymeleaf for the powerful templating engine
- Maven for build management

---

**Built with ❤️ using Spring Boot**
