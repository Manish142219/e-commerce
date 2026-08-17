# Backend Guide (Spring Boot + MySQL)

This document explains the **backend** of ShopEase for beginners.

---

## 1. What is the backend?

The backend is a **server application**. It:
- Talks to the **MySQL database**
- Exposes **REST APIs** (URLs like `/api/products`)
- Checks login using **JWT tokens**
- Contains business rules (cart totals, coupons, delivery check)

Frontend (Angular) never talks to MySQL directly. It always calls backend APIs.

---

## 2. Technology used

| Tool | Why we use it |
|------|----------------|
| Java + Spring Boot 3 | Fast way to build REST APIs |
| Spring Data JPA | Save/read data without writing SQL for every query |
| MySQL | Stores users, products, cart, orders |
| Spring Security + JWT | Login security without sessions |
| Maven | Downloads libraries and builds the project |
| Lombok | Auto-generates getters/setters/builders |

---

## 3. Folder structure (important)

```
backend/src/main/java/com/ecommerce/
├── EcommerceApplication.java     ← app starts here
├── config/                       ← Security, CORS, DataSeeder
├── controller/                   ← REST endpoints (HTTP layer)
├── dto/                          ← request/response objects
├── entity/                       ← database tables as Java classes
├── repository/                   ← database queries
├── security/                     ← JWT filter & password checks
├── service/                      ← business logic
└── util/                         ← helpers (variant type labels)
```

### What each layer does

1. **Controller** receives HTTP request  
2. **Service** does the work / checks rules  
3. **Repository** reads/writes DB  
4. **Entity** = table row  
5. **DTO** = safe data sent to frontend (not full entity)

**Why DTO?**  
Never send password or internal DB fields to the browser.

---

## 4. Important entities (tables)

| Entity | Table | Purpose |
|--------|-------|---------|
| User | users | Login accounts |
| Category | categories | Shop by category + `variantType` |
| Product | products | Catalog items |
| CartItem | cart_items | Bag items |
| WishlistItem | wishlist_items | Saved products |
| Address | addresses | Delivery addresses |
| Order / OrderItem | orders / order_items | Placed orders |
| NavMenuItem | nav_menu_items | Mega menu content |

### Category `variantType` (size system)

| variantType | Used for | Example options |
|-------------|----------|-----------------|
| CLOTHING | shirts, dresses, jeans | S, M, L, XL or 38, 40, 42 |
| FOOTWEAR | shoes | UK 6, 7, 8, 9, 10 |
| BEAUTY | grooming/makeup | 50ml, 100ml, Pack of 1 |
| ACCESSORY | watches | Free Size |

This value is returned with every product so UI can show the correct selector.

---

## 5. Main API groups

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login` → returns JWT token

### Catalog (public GET)
- `GET /api/categories`
- `GET /api/products/{id}`
- `GET /api/products/category/{id}`
- `GET /api/products/search?q=`
- `GET /api/nav/{section}`
- `GET /api/delivery/check?pincode=`

### Protected (needs `Authorization: Bearer <token>`)
- Cart: `/api/cart`
- Wishlist: `/api/wishlist`
- Addresses: `/api/addresses`
- Orders: `/api/orders`

---

## 6. How login security works

1. User registers → password stored as **BCrypt hash** (not plain text)
2. User logs in → backend verifies password → creates **JWT**
3. Frontend stores token in `localStorage`
4. Every protected request sends header:  
   `Authorization: Bearer eyJhbGciOi...`
5. `JwtAuthenticationFilter` reads token and sets logged-in user

Public APIs (products/categories) do not need token.

---

## 7. DataSeeder — why products appear automatically

On first run (empty DB), `DataSeeder` inserts:
- categories
- sample products (clothing, footwear, beauty, watches)
- mega menu items

On later runs it also:
- backfills `variantType` on categories
- ensures Footwear category exists
- can add missing beauty/footwear products

---

## 8. How to run backend

```powershell
# Use JDK 25 if your default is JDK 26
$env:JAVA_HOME="C:\Program Files\Java\jdk-25"

# Update MySQL username/password in:
# backend/src/main/resources/application.properties

cd D:\PROJECT\e-commerce\backend
mvn spring-boot:run
```

Server: **http://localhost:8080**

Test in browser or Postman:
`http://localhost:8080/api/categories`

---

## 9. Config file you must know

`application.properties`
- DB URL / username / password
- JWT secret & expiry
- CORS allowed origin (`http://localhost:4200`)

If MySQL password is wrong, backend will not start.

---

## 10. Beginner tips for backend changes

1. Change entity → restart app (`ddl-auto=update` updates table)
2. New API = Controller method + Service method
3. Always return `ApiResponse` so frontend gets `{ success, message, data }`
4. Keep business rules in **service**, not controller
5. Never log passwords or put secrets in frontend
