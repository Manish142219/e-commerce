# Frontend Guide (Angular 18 + Tailwind)

This document explains the **frontend** of ShopEase for beginners.

---

## 1. What is the frontend?

Frontend is what users see in the browser:
- Login / Register pages
- Header, product cards, bag, wishlist
- Forms and buttons

It is built with **Angular 18 (NgModule based)** + **Tailwind CSS**.

---

## 2. Why module-based (not standalone)?

As required for this project:
- Each feature has an `NgModule`
- Routes use `loadChildren` (lazy loading)
- Shared UI lives in `SharedModule`

This keeps the first page load smaller.

---

## 3. Folder structure

```
frontend/src/app/
├── app.module.ts              ← root module
├── app-routing.module.ts      ← lazy routes
├── core/
│   ├── guards/                ← AuthGuard, GuestGuard
│   ├── interceptors/          ← attaches JWT to requests
│   ├── models/                ← TypeScript interfaces
│   └── services/              ← HTTP calls to backend
├── shared/
│   └── components/            ← Header, cards, address modal
└── features/
    ├── auth/
    ├── home/
    ├── category/
    ├── product/
    ├── cart/
    ├── checkout/
    ├── orders/
    ├── addresses/
    ├── wishlist/
    ├── profile/
    ├── search/
    └── account/               ← coupons, contact
```

Every component has **3 files**:
- `.ts` → logic
- `.html` → UI
- `.css` → local styles (most styling is Tailwind / global theme)

---

## 4. Global theme (change colors in one place)

File: `src/styles.css`

```css
:root {
  --color-primary: #ff3f6c;
  --color-accent: #03a685;
  --color-text-heading: #282c3f;
  /* change here → whole app updates */
}
```

Useful classes:
- `.btn-primary`, `.btn-outline`
- `.heading-primary`, `.heading-secondary`, `.heading-section`
- `.text-subheading`, `.text-caption`
- `.card`, `.input-field`

Tailwind colors like `myntra-pink` also point to these CSS variables.

---

## 5. Services (how frontend talks to backend)

| Service | Talks to |
|---------|----------|
| AuthService | `/api/auth` |
| ProductService | `/api/products` |
| CategoryService | `/api/categories` |
| CartService | `/api/cart` |
| WishlistService | `/api/wishlist` |
| AddressService | `/api/addresses` |
| DeliveryService | `/api/delivery` |
| OrderService | `/api/orders` |
| NavService | `/api/nav` |

API base URL is in:
`src/environments/environment.ts` → `http://localhost:8080/api`

---

## 6. Auth flow in frontend

1. Login success → save `token` + user in `localStorage`
2. `AuthInterceptor` adds token to every HTTP request
3. `AuthGuard` blocks pages if not logged in
4. Logout clears storage and redirects to `/login`

---

## 7. Header features

- Mega menu on hover (MEN/WOMEN…)
- Search bar
- Wishlist + Bag count badges
- **Profile hover dropdown** (like Myntra):
  - Orders
  - Wishlist
  - Coupons / Gift Cards
  - Contact Us
  - Saved Addresses
  - Edit Profile
  - Logout

---

## 8. Category-based size UI

Product API returns:
- `variantType` → CLOTHING / FOOTWEAR / BEAUTY / ACCESSORY
- `variantLabel` → “SELECT SIZE”, “SELECT QUANTITY / VOLUME”, etc.
- `sizes` → actual options list

Product detail page shows:
- Circular buttons for clothing/footwear sizes
- Pill buttons for beauty volumes (`100ml`, `Pack of 1`)
- Auto-selects if only one option (example: Free Size)

---

## 9. Important pages & routes

| Route | Page |
|-------|------|
| `/login`, `/register` | Auth |
| `/home` | Shop by category |
| `/category/:slug` | Product listing + filters |
| `/product/:id` | Product detail |
| `/cart` | Bag |
| `/checkout/address` | Choose address → place order |
| `/orders` | Order history |
| `/wishlist` | Wishlist |
| `/addresses` | Saved addresses |
| `/coupons` | Coupons list |
| `/contact` | Contact info |
| `/profile` | Profile + logout |
| `/search?q=` | Search results |

---

## 10. How to run frontend

```powershell
cd D:\PROJECT\e-commerce\frontend
npm install
npm start
```

Open: **http://localhost:4200**

Backend must already be running on port 8080.

---

## 11. Beginner tips for frontend changes

1. Prefer editing `.html` + Tailwind classes for UI tweaks
2. Put API calls only in **services**, not components (when possible)
3. Always handle `success` and `error` in `subscribe`
4. Use `*ngIf` for loading/empty states
5. Restart `npm start` if environment file changes
