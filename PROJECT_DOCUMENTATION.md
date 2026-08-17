# ShopEase E-Commerce Application - Complete Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Backend Architecture](#backend-architecture)
5. [Frontend Architecture](#frontend-architecture)
6. [Database Schema](#database-schema)
7. [API Endpoints](#api-endpoints)
8. [Features Implemented](#features-implemented)
9. [How to Run](#how-to-run)
10. [Default Test Flow](#default-test-flow)

---

## Project Overview

**ShopEase** is a full-stack e-commerce web application inspired by the Myntra fashion platform. It includes user authentication, a Myntra-style header with mega-menu dropdowns, shop-by-category homepage, product listing with filters, product detail pages, shopping bag (cart), and wishlist functionality.

The application follows a **module-based Angular 18** frontend and a **layered Spring Boot 3** backend with **MySQL** database.

---

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Frontend | Angular 18 (Module-based, NOT standalone) |
| Styling | Tailwind CSS 3 |
| Backend | Spring Boot 3.3.5 |
| Database | MySQL 8 |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA / Hibernate |
| Build Tools | Maven (backend), npm/Angular CLI (frontend) |

---

## Project Structure

```
e-commerce/
├── backend/                          # Spring Boot REST API
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/ecommerce/
│       │   ├── EcommerceApplication.java    # Main entry point
│       │   ├── config/                      # Security, CORS, Data Seeder
│       │   ├── controller/                  # REST API controllers
│       │   ├── dto/                         # Data Transfer Objects
│       │   ├── entity/                    # JPA Entities (DB tables)
│       │   ├── repository/                # Spring Data JPA Repositories
│       │   ├── security/                  # JWT filter & utilities
│       │   └── service/                     # Business logic layer
│       └── resources/
│           └── application.properties       # DB & JWT configuration
│
├── frontend/                         # Angular 18 Application
│   └── src/app/
│       ├── app.module.ts             # Root module
│       ├── app-routing.module.ts     # Lazy-loaded routes
│       ├── core/                     # Singleton services, guards, models
│       │   ├── guards/               # AuthGuard, GuestGuard
│       │   ├── interceptors/       # JWT token interceptor
│       │   ├── models/               # TypeScript interfaces
│       │   └── services/             # API service classes
│       ├── shared/                   # Reusable components module
│       │   └── components/
│       │       ├── header/           # Myntra-style navbar + mega menu
│       │       ├── category-card/    # Reusable category card
│       │       ├── product-card/     # Reusable product card
│       │       └── breadcrumb/       # Breadcrumb navigation
│       └── features/                 # Feature modules (lazy-loaded)
│           ├── auth/                 # Login & Register
│           ├── home/                 # Shop by Category page
│           ├── category/             # Product listing with filters
│           ├── product/              # Product detail page
│           ├── cart/                 # Shopping bag page
│           ├── wishlist/             # Wishlist page
│           ├── profile/              # User profile page
│           └── search/               # Search results page
│
└── PROJECT_DOCUMENTATION.md          # This file
```

---

## Backend Architecture

### Why This Structure?

Each layer has a single responsibility following the **Separation of Concerns** principle:

| Layer | Purpose | Why |
|-------|---------|-----|
| **Entity** | Maps Java classes to database tables | JPA/Hibernate auto-creates tables and handles relationships |
| **Repository** | Database access (CRUD queries) | Spring Data JPA eliminates boilerplate SQL |
| **DTO** | Data sent to/from the API | Prevents exposing internal entity structure; validates input |
| **Service** | Business logic | Keeps controllers thin; reusable logic |
| **Controller** | HTTP request handling | REST endpoints only; delegates to services |
| **Config** | App-wide settings | Security, CORS, data seeding in one place |
| **Security** | JWT authentication | Stateless auth; token sent in Authorization header |

### Key Backend Files Explained

- **`SecurityConfig.java`** - Configures which endpoints are public (auth, products) vs protected (cart, wishlist). Disables CSRF for REST API.
- **`JwtUtil.java`** - Creates and validates JWT tokens with 24-hour expiry.
- **`JwtAuthenticationFilter.java`** - Intercepts every request, extracts Bearer token, validates it.
- **`DataSeeder.java`** - Auto-populates categories, products, and navigation menus on first startup.
- **`AuthService.java`** - Handles registration (BCrypt password hashing) and login.
- **`CartService.java`** - Manages cart items, calculates price summary with discounts.

---

## Frontend Architecture

### Why Module-Based (Not Standalone)?

As requested, the app uses **NgModule** architecture:
- Each feature is a **lazy-loaded module** (better performance - code loads only when needed)
- **SharedModule** exports reusable components (Header, CategoryCard, ProductCard, Breadcrumb)
- **Core** folder contains singleton services, guards, and interceptors

### Component Structure

Every component follows the standard Angular pattern with **3 separate files**:
```
component-name/
├── component-name.component.ts    # Logic
├── component-name.component.html  # Template
└── component-name.component.css   # Styles
```

### Key Frontend Files Explained

| File | What It Does |
|------|-------------|
| `auth.guard.ts` | Blocks unauthenticated users from accessing protected pages |
| `guest.guard.ts` | Redirects logged-in users away from login/register |
| `auth.interceptor.ts` | Automatically attaches JWT token to every HTTP request |
| `header.component` | Myntra-style navbar with mega-menu dropdown on hover |
| `category-card.component` | Reusable card showing category image, name, discount |
| `product-card.component` | Reusable card showing product image, brand, price, rating |

### Routing Flow

```
/login          → Login page (GuestGuard)
/register       → Registration page (GuestGuard)
/home           → Shop by Category (AuthGuard)
/category/:slug → Product listing with filters (AuthGuard)
/product/:id    → Product detail with Add to Bag (AuthGuard)
/cart           → Shopping bag (AuthGuard)
/wishlist       → Wishlist page (AuthGuard)
/profile        → User profile (AuthGuard)
/search?q=...   → Search results (AuthGuard)
```

---

## Database Schema

MySQL database `ecommerce_db` is auto-created. Tables:

| Table | Purpose |
|-------|---------|
| `users` | Registered user accounts |
| `addresses` | User delivery addresses |
| `categories` | Shop-by-category items (Ethnic Wear, Casual Wear, etc.) |
| `products` | Product catalog with prices, sizes, colors |
| `product_images` | Multiple images per product |
| `product_sizes` | Available sizes per product |
| `product_colors` | Available colors per product |
| `cart_items` | Items in user's shopping bag |
| `wishlist_items` | Items in user's wishlist |
| `nav_menu_items` | Mega-menu dropdown content for MEN, WOMEN, etc. |

---

## API Endpoints

### Public (No Auth Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT token |
| GET | `/api/categories` | Get all categories |
| GET | `/api/categories/{slug}` | Get category by slug |
| GET | `/api/products` | Get all products |
| GET | `/api/products/{id}` | Get product details |
| GET | `/api/products/category/{id}` | Get products by category (with filters) |
| GET | `/api/products/search?q=` | Search products |
| GET | `/api/nav/{section}` | Get mega-menu for MEN/WOMEN/etc. |

### Protected (JWT Token Required)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/cart` | Get cart with price summary |
| POST | `/api/cart` | Add item to cart |
| PUT | `/api/cart/{id}/quantity` | Update item quantity |
| DELETE | `/api/cart/{id}` | Remove item from cart |
| GET | `/api/cart/count` | Get cart item count |
| GET | `/api/wishlist` | Get wishlist items |
| POST | `/api/wishlist/{productId}` | Add to wishlist |
| DELETE | `/api/wishlist/{productId}` | Remove from wishlist |

---

## Features Implemented

### 1. Authentication (Login/Register)
- Email + password registration with validation
- JWT-based login (token stored in localStorage)
- Route guards protect all pages after login

### 2. Myntra-Style Header
- Logo, navigation links (MEN, WOMEN, KIDS, HOME, BEAUTY, GENZ, STUDIO)
- STUDIO has "NEW" badge
- Mega-menu dropdown on hover (5-column layout like Myntra)
- Search bar with live search navigation
- Profile, Wishlist, Bag icons with count badges

### 3. Shop by Category (Home Page)
- Grid of category cards (6 per row)
- Each card shows image, category name, discount text, "Shop Now"
- Reusable `CategoryCardComponent`

### 4. Product Listing (Category Page)
- Breadcrumb navigation
- Left sidebar filters: Brand, Price slider, Color, Discount range
- Sort dropdown (Recommended, Price, Discount)
- Product grid with reusable `ProductCardComponent`
- Rating overlay, brand, price, discount display

### 5. Product Detail Page
- Image gallery with thumbnail selection
- Brand, name, price, MRP, discount percentage
- Size selector (circular buttons)
- ADD TO BAG and WISHLIST buttons
- Delivery options card with pincode
- Best offers section

### 6. Shopping Bag (Cart)
- Checkout stepper (BAG → ADDRESS → PAYMENT)
- Delivery address card
- Cart items with size/qty selectors
- Price details sidebar (MRP, discount, coupon, platform fee, total)
- PLACE ORDER button

### 7. Wishlist
- Grid of wishlisted products
- Remove from wishlist
- Navigate to product detail

---

## How to Run

### Prerequisites
- **Java JDK 25** (or JDK 17+) — Set `JAVA_HOME` to JDK 25 if you have JDK 26
- **Maven 3.9+**
- **Node.js 18+** and **npm**
- **MySQL 8** running on `localhost:3306`

### Step 1: Setup MySQL Database

1. Start MySQL server
2. The app auto-creates database `ecommerce_db` on first run
3. Update credentials in `backend/src/main/resources/application.properties` if needed:

```properties
spring.datasource.username=root
spring.datasource.password=root
```

Change `root`/`root` to your MySQL username and password.

### Step 2: Run Backend (Spring Boot)

```powershell
# Set JAVA_HOME to JDK 25 (required if JDK 26 is default)
$env:JAVA_HOME="C:\Program Files\Java\jdk-25"

cd D:\PROJECT\e-commerce\backend
mvn spring-boot:run
```

Backend starts at: **http://localhost:8080**

On first run, `DataSeeder` automatically creates:
- 12 categories (Ethnic Wear, Casual Wear, etc.)
- 10 sample products
- Mega-menu data for MEN and WOMEN sections

### Step 3: Run Frontend (Angular)

Open a **new terminal**:

```powershell
cd D:\PROJECT\e-commerce\frontend
npm install
npm start
```

Frontend starts at: **http://localhost:4200**

### Step 4: Use the Application

1. Open **http://localhost:4200** in browser
2. Click **"Create an account"** to register
3. After login, you'll see the **Shop by Category** homepage
4. Click any category card → see product listing with filters
5. Click any product → see product detail page
6. Select a size → click **ADD TO BAG** or **WISHLIST**
7. Click **Bag** icon in header → see shopping cart
8. Hover over **MEN/WOMEN** in header → see mega-menu dropdown

---

## Default Test Flow

```
1. Register → name: "Test User", email: "test@test.com", password: "123456"
2. Login → redirects to /home
3. Click "Casual Wear" category card
4. See 4+ casual shirts with filters on left
5. Click "ONYX WEAR Men Casual Shirt"
6. Select size "40" → Click "ADD TO BAG"
7. Click Bag icon (shows count badge)
8. See item in cart with price breakdown
9. Click Wishlist on another product
10. Click Wishlist icon → see wishlisted items
```

---

## Configuration Reference

### Backend (`application.properties`)
| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | 8080 | Backend server port |
| `spring.datasource.url` | jdbc:mysql://localhost:3306/ecommerce_db | MySQL connection |
| `jwt.expiration` | 86400000 (24 hours) | Token validity |
| `cors.allowed-origins` | http://localhost:4200 | Frontend URL for CORS |

### Frontend (`environment.ts`)
| Property | Default | Description |
|----------|---------|-------------|
| `apiUrl` | http://localhost:8080/api | Backend API base URL |

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Backend won't compile with JDK 26 | Set `JAVA_HOME` to JDK 25: `$env:JAVA_HOME="C:\Program Files\Java\jdk-25"` |
| MySQL connection refused | Ensure MySQL is running and credentials in `application.properties` are correct |
| CORS errors in browser | Ensure backend is running on port 8080 and frontend on 4200 |
| Empty categories/products | Delete database and restart backend to re-run DataSeeder |
| 401 Unauthorized on cart/wishlist | Login again; JWT token may have expired (24h) |

---

*Documentation generated for ShopEase E-Commerce Project*
