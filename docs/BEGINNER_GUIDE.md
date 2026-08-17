# Beginner Guide: Build, Run & Common Mistakes

Use this checklist whenever you work on ShopEase (or any similar app).

---

## 1. What you need installed

- JDK 17+ (prefer JDK 25 on this machine)
- Maven
- Node.js + npm
- MySQL running locally
- VS Code / Cursor

---

## 2. Correct order to start the app

### Step 1 — Start MySQL
Make sure MySQL service is running.

### Step 2 — Check DB credentials
File: `backend/src/main/resources/application.properties`

```properties
spring.datasource.username=root
spring.datasource.password=root
```

Change to your real MySQL password.

### Step 3 — Start backend

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-25"
cd D:\PROJECT\e-commerce\backend
mvn spring-boot:run
```

Wait until you see app started on port **8080**.

### Step 4 — Start frontend (new terminal)

```powershell
cd D:\PROJECT\e-commerce\frontend
npm start
```

Open **http://localhost:4200**

### Step 5 — Register & login
Then use Home → Category → Product → Bag → Place Order.

---

## 3. Mental model: how to start ANY application

1. **Define features** (login, list, detail, cart…)
2. **Design data** (tables/entities)
3. **Build backend APIs first** (test with Postman)
4. **Build frontend screens**
5. **Connect UI to APIs via services**
6. **Add auth/guards**
7. **Test full user journey**
8. **Write docs** for future you

Do not start with fancy UI before APIs exist.

---

## 4. Necessary things to remember

### Architecture
- Separate frontend and backend
- Use layers: Controller → Service → Repository
- Keep secrets in backend config, not Angular code

### Security
- Hash passwords
- Use JWT/session for protected APIs
- Validate input (email, pincode, required fields)

### UX
- Loading states
- Empty states (“No orders yet”)
- Clear error messages (“Please select a size”)

### Data consistency
- Cart totals calculated on backend
- Delivery date from backend
- Category decides size type (clothing vs footwear vs beauty)

### Code quality
- Reusable components (ProductCard, Header)
- One responsibility per file
- Consistent API response format

---

## 5. Common mistakes beginners do

| Mistake | What happens | Fix |
|---------|--------------|-----|
| Start frontend only | Login/API fails | Start backend too |
| Wrong MySQL password | Backend crash | Fix `application.properties` |
| Using JDK 26 with old Lombok | Compile fails | Use JDK 25 / update Lombok |
| Hardcoding API URL in many files | Hard to change | Use `environment.ts` |
| Not handling API errors | Blank/broken UI | Use `error:` in subscribe |
| Putting business logic in HTML | Hard to maintain | Move to `.ts` / service |
| Forgetting Auth header | 401 on cart | Use interceptor + login |
| Changing entity but not DTO | UI missing fields | Update DTO + frontend model |
| Assuming DB reset every run | Old data remains | Delete DB or write migration/backfill |
| Editing docs instead of code | Feature not working | Implement then document |

---

## 6. Quick debug checklist

### “Add to bag not working”
1. Are you logged in?
2. Is size/volume selected?
3. Is backend running?
4. Browser Network tab → is request 200 or 401/400?
5. Is pincode deliverable?

### “Profile hover not showing”
1. Hard refresh browser
2. Confirm header HTML has profile dropdown
3. Check `showProfileMenu` becomes true on hover

### “Wrong size options”
1. Check product’s category `variantType`
2. Restart backend so DataSeeder backfill runs
3. Open product API in browser and inspect `sizes` + `variantType`

### “Orders empty”
1. Place order from checkout address page
2. Call `GET /api/orders` with token
3. Open `/orders` page

---

## 7. Docs map (read in this order)

1. `docs/BEGINNER_GUIDE.md` ← you are here
2. `docs/BACKEND_GUIDE.md`
3. `docs/FRONTEND_GUIDE.md`
4. `docs/REST_API_AND_DATA_FLOW.md`
5. `PROJECT_DOCUMENTATION.md` (overall project summary)

---

## 8. Suggested learning path after this project

1. Add Payment page (next phase)
2. Add Admin panel to create products
3. Add pagination on product listing
4. Add unit tests for CartService
5. Deploy backend + frontend separately

---

## 9. One-line summary

**Database stores data → Backend APIs expose data → Angular services fetch data → Components bind data → HTML displays data.**

Master that sentence, and you can build almost any CRUD app.
