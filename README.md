# ShopEase - Myntra-Style E-Commerce Application

Full-stack e-commerce app built with **Angular 18** (module-based) + **Spring Boot 3** + **MySQL**.

## Quick Start (local)

### 1. MySQL
MySQL must be running. Credentials come from env vars (defaults: `root` / `root`).

```powershell
$env:DB_PASSWORD="9236"   # your local password
```

### 2. Backend
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-25"
cd backend
mvn spring-boot:run
```

### 3. Frontend
```powershell
cd frontend
npm install
npm start
```

Open **http://localhost:4200** → Register → Login → Shop!

## Deploy to Render

Full step-by-step guide (GitHub → Render → updates):

**[docs/DEPLOY_TO_RENDER.md](docs/DEPLOY_TO_RENDER.md)**

## Docs privacy

- Live website users **cannot** see `docs/` or markdown explanations (not part of Angular build).
- To keep docs only for you on GitHub: make the repository **Private**.

## Beginner documentation

| Doc | What it teaches |
|-----|-----------------|
| [docs/BEGINNER_GUIDE.md](docs/BEGINNER_GUIDE.md) | How to run, checklist, common mistakes |
| [docs/BACKEND_GUIDE.md](docs/BACKEND_GUIDE.md) | Spring Boot structure |
| [docs/FRONTEND_GUIDE.md](docs/FRONTEND_GUIDE.md) | Angular structure |
| [docs/REST_API_AND_DATA_FLOW.md](docs/REST_API_AND_DATA_FLOW.md) | How API data reaches UI |
| [docs/DEPLOY_TO_RENDER.md](docs/DEPLOY_TO_RENDER.md) | Deploy with GitHub + Render |
| [PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md) | Full project overview |

## Key Features
- Login / Register (JWT)
- Myntra-style header + mega menu + profile hover menu
- Category-based size/quantity selection
- Bag, wishlist, coupons, delivery check
- Checkout address + place order (payment next phase)
