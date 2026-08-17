# REST APIs & How Data Reaches the UI (Beginner)

This is the most important concept for full-stack apps.

---

## 1. Simple idea

```
Browser (Angular UI)
        │  HTTP request (JSON)
        ▼
Spring Boot API
        │  JPA / SQL
        ▼
MySQL Database
        │
        ▼
JSON response back to Angular
        │
        ▼
UI shows the data
```

Frontend never writes SQL. Backend never paints HTML for product pages (in this project).

---

## 2. What is a REST API?

REST API = URL + HTTP method + JSON body/response.

| Method | Meaning | Example |
|--------|---------|---------|
| GET | Read data | Get products |
| POST | Create | Add to cart / login |
| PUT | Update | Change quantity |
| DELETE | Remove | Remove cart item |

Example:
`GET http://localhost:8080/api/products/1`

Response shape used everywhere:

```json
{
  "success": true,
  "message": "optional message",
  "data": { "...product fields..." }
}
```

---

## 3. Example: Show product on UI

### Step A — Backend Controller
`ProductController.java`

```java
@GetMapping("/{id}")
public ResponseEntity<ApiResponse<ProductDto>> getProductById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(productService.getProductById(id)));
}
```

### Step B — Service
Reads from DB through repository and converts Entity → DTO  
(includes `variantType`, prices, sizes, images)

### Step C — Frontend Service
`product.service.ts`

```ts
getById(id: number) {
  return this.http.get<ApiResponse<Product>>(`${this.apiUrl}/${id}`);
}
```

### Step D — Component
`product-detail.component.ts`

```ts
this.productService.getById(+params['id']).subscribe({
  next: (res) => {
    if (res.success) {
      this.product = res.data; // store in component variable
    }
  }
});
```

### Step E — Template
`product-detail.component.html`

```html
<h1>{{ product.brand }}</h1>
<p>₹{{ product.price }}</p>
```

Angular automatically updates the screen when `product` changes.

---

## 4. Example: Add to Bag (POST with auth)

### Frontend
```ts
this.cartService.addToCart(productId, selectedSize).subscribe(...)
```

### HTTP request (simplified)
```
POST /api/cart
Authorization: Bearer <jwt>
Body: { "productId": 1, "size": "M", "quantity": 1 }
```

### Backend
1. JWT filter identifies user from token
2. CartService finds product
3. Saves `CartItem` in MySQL
4. Returns saved item JSON
5. Frontend refreshes bag count badge

---

## 5. Where JWT is attached automatically

`AuthInterceptor` runs for every HTTP call:

```ts
req = req.clone({
  setHeaders: { Authorization: `Bearer ${token}` }
});
```

You do **not** manually add token in each service method.

---

## 6. Mapping: UI action → API

| User action | Frontend call | Backend API |
|-------------|---------------|-------------|
| Open home | `CategoryService.getAll()` | GET `/api/categories` |
| Open product | `ProductService.getById(id)` | GET `/api/products/{id}` |
| Check pincode | `DeliveryService.checkPincode()` | GET `/api/delivery/check` |
| Add to bag | `CartService.addToCart()` | POST `/api/cart` |
| Open bag | `CartService.getCart()` | GET `/api/cart` |
| Apply coupon | `CartService.applyCoupon()` | POST `/api/cart/coupon` |
| Place order | `OrderService.placeOrder()` | POST `/api/orders` |
| View orders | `OrderService.getOrders()` | GET `/api/orders` |
| Hover WOMEN menu | `NavService.getMenu('WOMEN')` | GET `/api/nav/WOMEN` |

---

## 7. Why we use Observables (`subscribe`)

HTTP in Angular is asynchronous (takes time).

```ts
this.productService.getById(1).subscribe({
  next: (res) => { /* success */ },
  error: (err) => { /* failed */ }
});
```

- `next` → API worked
- `error` → network/401/400 etc.

Always show loading and error messages for beginners’ UX.

---

## 8. Common data flow mistakes

1. Calling wrong URL (`/products` instead of `/api/products`)
2. Backend not running → CORS / connection refused
3. Forgot token → 401 Unauthorized on cart/wishlist
4. Using entity fields that DTO does not include
5. Forgetting `*ngIf="product"` before showing `product.name` (null error)
6. Expecting sync return from HTTP (must use subscribe)

---

## 9. How to test APIs without frontend

Use Postman / browser:

1. Register:
`POST /api/auth/register`
```json
{ "name": "Aman", "email": "a@test.com", "password": "123456" }
```

2. Copy `token` from response

3. Call protected API with header:
`Authorization: Bearer <token>`

4. Then open Angular UI — same APIs are used.

---

## 10. Practice exercise (recommended)

1. Add one new product in DataSeeder
2. Restart backend
3. Confirm it appears in category page
4. Trace: Entity → Repository → Service → Controller → Angular Service → Component → HTML

If you can explain that chain verbally, you understand full-stack data flow.
