# Maven Wrapper placeholder note

If `mvnw` is not present yet, generate it on your PC:

```powershell
cd D:\PROJECT\e-commerce\backend
mvn -N wrapper:wrapper
```

Then commit `mvnw`, `mvnw.cmd`, and `.mvn/` so Render can build without global Maven.
