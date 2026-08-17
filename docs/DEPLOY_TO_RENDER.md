# Deploy ShopEase to Render (Beginner Guide)

This guide explains **how deployment works** and **exact steps** to put your app live using **GitHub + Render**.

---

## Important: Your docs will NOT appear on the live website

| Where | Who can see `docs/` and `PROJECT_DOCUMENTATION.md`? |
|-------|------------------------------------------------------|
| **Live site** (Render frontend) | **Nobody.** Angular only ships HTML/JS/CSS from `frontend/dist`. Markdown docs are **not** part of the build. |
| **GitHub repo** | Anyone who can open the repo. |

### How to keep docs private (only you)

1. Create / keep your GitHub repository as **Private**
2. Do **not** share the repo invite with others
3. Optional: never put secrets (DB password, JWT) inside docs

You do **not** need to delete docs from the repo. Website users cannot open `/docs/BACKEND_GUIDE.md` on your Render URL.

---

## Big picture: how deployment works

```
Your PC ──git push──► GitHub (private repo)
                         │
                         │  Render watches the repo
                         ▼
              ┌──────────────────────┐
              │  Render builds code  │
              └──────────────────────┘
                         │
          ┌──────────────┴──────────────┐
          ▼                             ▼
   Backend Web Service            Frontend Static Site
   (Spring Boot API)              (Angular build)
          │                             │
          └──────── talks via ──────────┘
               HTTPS REST APIs
          │
          ▼
     MySQL Database (cloud)
```

### Workflow you asked for

1. **First time:** push code → connect Render → go live  
2. **Later updates:** change code on PC → `git push` → Render auto-redeploys  

---

## What you will create on Render (3 pieces)

| Piece | Render type | Purpose |
|-------|-------------|---------|
| 1. Database | External MySQL (recommended) OR paid MySQL host | Stores users, products, cart |
| 2. Backend | **Web Service** | Spring Boot API |
| 3. Frontend | **Static Site** | Angular UI |

> Render free tier has **PostgreSQL**, not free MySQL. Your app is written for **MySQL**, so use a free/cheap MySQL host (Railway, Aiven, Clever Cloud, PlanetScale-compatible, etc.) OR later migrate to Postgres.

---

## STEP 0 — Install Git (required)

On your PC, Git was not found in PATH earlier.

1. Install Git: https://git-scm.com/download/win  
2. Restart Cursor / PowerShell  
3. Check:

```powershell
git --version
```

Also install / login to GitHub Desktop if you prefer GUI: https://desktop.github.com/

---

## STEP 1 — Create a PRIVATE GitHub repository

1. Go to https://github.com/new  
2. Repository name: `e-commerce` (or `shopease`)  
3. Visibility: **Private** ← this hides docs from strangers  
4. Do **not** add README if your project already has files  
5. Click **Create repository**

---

## STEP 2 — Push your project to GitHub (first time)

In PowerShell:

```powershell
cd D:\PROJECT\e-commerce

git init
git add .
git commit -m "Initial ShopEase full-stack app"

git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
git push -u origin main
```

Replace `YOUR_USERNAME` / `YOUR_REPO`.

### If Git asks for login
Use GitHub login / Personal Access Token (not your GitHub password for HTTPS).

---

## STEP 3 — Create a cloud MySQL database

Example options:
- Railway → New → Database → MySQL  
- Aiven → MySQL free trial  
- Any MySQL host that gives you host, port, db name, user, password

Collect these values:

```
Host: xxxx.railway.app (example)
Port: 3306 (or given port)
Database: railway (example)
Username: root
Password: ********
```

Build JDBC URL like:

```
jdbc:mysql://HOST:PORT/DATABASE?useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

---

## STEP 4 — Deploy Backend on Render (Web Service)

1. Go to https://dashboard.render.com → Sign up with **GitHub**  
2. **New +** → **Web Service**  
3. Connect your **private** repo  
4. Settings:

| Field | Value |
|-------|--------|
| Name | `shopease-api` (example) |
| Root Directory | `backend` |
| Runtime | Java |
| Build Command | `./mvnw clean package -DskipTests` **OR** `mvn clean package -DskipTests` |
| Start Command | `java -jar target/ecommerce-backend-1.0.0.jar` |

### If Maven wrapper is missing
Add Maven wrapper locally, or use Render’s Maven.  
Your project currently uses system Maven. On Render, easiest is:

**Build Command:**
```bash
mvn -DskipTests clean package
```

**Start Command:**
```bash
java -jar target/ecommerce-backend-1.0.0.jar
```

(Confirm jar name inside `backend/target/` after local `mvn package`.)

### Environment Variables (Backend)

In Render → Web Service → Environment:

| Key | Value |
|-----|--------|
| `DB_URL` | your JDBC URL from Step 3 |
| `DB_USERNAME` | MySQL username |
| `DB_PASSWORD` | MySQL password |
| `JWT_SECRET` | long random string (32+ chars) |
| `CORS_ALLOWED_ORIGINS` | leave blank first, update after frontend URL exists |
| `DDL_AUTO` | `update` |
| `SHOW_SQL` | `false` |

Click **Create Web Service** and wait for deploy.

Your API will look like:
`https://shopease-api.onrender.com`

Test:
`https://shopease-api.onrender.com/api/categories`

---

## STEP 5 — Point frontend to live API

Edit file:

`frontend/src/environments/environment.prod.ts`

```ts
export const environment = {
  production: true,
  apiUrl: 'https://shopease-api.onrender.com/api'  // your real backend URL
};
```

Commit and push:

```powershell
cd D:\PROJECT\e-commerce
git add frontend/src/environments/environment.prod.ts
git commit -m "Set production API URL for Render"
git push
```

---

## STEP 6 — Deploy Frontend on Render (Static Site)

1. Render → **New +** → **Static Site**  
2. Connect same GitHub repo  
3. Settings:

| Field | Value |
|-------|--------|
| Name | `shopease-web` |
| Root Directory | `frontend` |
| Build Command | `npm install && npm run build` |
| Publish Directory | `dist/frontend/browser` |

> Angular 18 application builder usually outputs to `dist/frontend/browser`.  
> If deploy fails looking for files, check folder after local `npm run build` and fix Publish Directory.

4. Create Static Site → wait for build.

Frontend URL example:
`https://shopease-web.onrender.com`

---

## STEP 7 — Connect CORS (final backend update)

In backend Render env, set:

```
CORS_ALLOWED_ORIGINS=https://shopease-web.onrender.com
```

(If you also need local testing while deployed:)
```
CORS_ALLOWED_ORIGINS=https://shopease-web.onrender.com,http://localhost:4200
```

Save → Render redeploys backend automatically.

---

## STEP 8 — Test live app

1. Open frontend Render URL  
2. Register a new user  
3. Login → Home → Add to bag  

Free Render services **sleep after idle**. First request may take 30–60 seconds.

---

## Later updates (your daily workflow)

Whenever you change code:

```powershell
cd D:\PROJECT\e-commerce
git add .
git commit -m "Describe your change"
git push
```

Render detects the push and redeploys.  
No need to re-create services.

---

## Docs privacy checklist (again)

- [x] Docs are not served by Angular → live users cannot browse them  
- [ ] GitHub repo is **Private** → strangers cannot read docs on GitHub  
- [x] Secrets are in Render Environment Variables, not in markdown  

If repo is **Public**, anyone can read `docs/` even if website hides them.

---

## Common deploy mistakes

| Mistake | Result | Fix |
|---------|--------|-----|
| Public repo with secrets in `application.properties` | Password leaked | Use env vars (already prepared) + rotate password |
| Wrong `apiUrl` in `environment.prod.ts` | Frontend cannot call API | Update URL → push → redeploy frontend |
| CORS missing frontend URL | Browser blocks API | Set `CORS_ALLOWED_ORIGINS` |
| Wrong Publish Directory | Blank site | Use `dist/frontend/browser` |
| Free service slept | Slow first load | Wait / upgrade plan |
| MySQL SSL / URL wrong | Backend crash loop | Fix `DB_URL` |
| Forgot Root Directory `backend`/`frontend` | Build fails | Set Root Directory correctly |

---

## Optional: Maven Wrapper (recommended for Render)

From `backend` folder (when Maven works locally):

```powershell
cd D:\PROJECT\e-commerce\backend
mvn -N wrapper:wrapper
```

Then Render build command can be:
```bash
./mvnw -DskipTests clean package
```

---

## What I already prepared in your project

1. Root `.gitignore` (hides `node_modules`, `target`, `.env`)  
2. Backend `application.properties` reads **environment variables** for Render  
3. `.env.example` shows which keys to set  
4. `environment.prod.ts` placeholder for Render API URL  
5. Angular production `fileReplacements` so prod build uses `environment.prod.ts`  

Your local MySQL password is no longer hard-required in committed config (defaults are localhost `root/root`; override locally if needed).

For local run with your password `9236`:

```powershell
$env:DB_PASSWORD="9236"
cd D:\PROJECT\e-commerce\backend
mvn spring-boot:run
```

Or set in IDE run config.

---

## Suggested order (do this exactly)

1. Install Git  
2. Create **Private** GitHub repo  
3. Push code  
4. Create cloud MySQL  
5. Deploy backend Web Service on Render  
6. Put real API URL into `environment.prod.ts` → push  
7. Deploy frontend Static Site  
8. Set `CORS_ALLOWED_ORIGINS`  
9. Test register/login  

After that, every `git push` updates the live app.
