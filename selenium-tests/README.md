# SkillTask — Selenium + TestNG Automation Suite

End-to-end UI automation for the SkillTask app, built with **Selenium 4 + TestNG**
using the **Page Object Model (POM)** design pattern.

---

## ✅ Prerequisites

1. **Java 17+** installed
2. **Maven** (or use IntelliJ's bundled Maven)
3. **Google Chrome** installed (ChromeDriver auto-downloads via WebDriverManager)
4. **Backend running** on `http://localhost:8081`
   ```bash
   cd backend && mvn spring-boot:run
   ```
5. **Frontend running** on `http://localhost:4200`
   ```bash
   cd frontend && npm start
   ```

---

## 📁 Project structure (POM architecture)

```
selenium-tests/
├── pom.xml                         # Maven deps + surefire config
├── testng.xml                      # Main suite (Smoke + Regression)
├── smoke-suite.xml                 # Smoke-only suite
└── src/test/java/com/skilltask/
    ├── base/
    │   ├── TestConfig.java         # URLs, credentials, timeouts (single source of truth)
    │   └── BaseTest.java           # @BeforeMethod browser setup / @AfterMethod teardown
    ├── pages/                      # PAGE OBJECTS — selectors + actions live here
    │   ├── LoginPage.java
    │   ├── SignupPage.java
    │   ├── ManagerPage.java
    │   ├── EmployeePage.java
    │   └── ProfilePage.java
    └── tests/                      # TEST CLASSES — only business steps + assertions
        ├── LoginTest.java          # login (manager / employee / wrong-password)
        ├── SignupTest.java         # new signup + duplicate email
        ├── ManagerTaskTest.java    # create task + auto-allocation
        ├── EmployeeTaskTest.java   # full lifecycle ASSIGNED → IN_PROGRESS → DONE
        └── ProfileTest.java        # add skill + availability toggle
```

---

## ▶️ How to run

### From the command line (Maven)
```bash
cd selenium-tests

# Run the full suite (testng.xml)
mvn test

# Run only smoke tests
mvn test -DsuiteXmlFile=smoke-suite.xml

# Run a single test class
mvn test -Dtest=LoginTest

# Run a single method
mvn test "-Dtest=LoginTest#managerCanLogIn"

# Run by group
mvn test -Dgroups=smoke
```

### From IntelliJ IDEA
- Right-click `testng.xml` → **Run 'SkillTaskSuite'**
- Or right-click any test class → **Run 'ClassName'**
- Pre-made run configs appear in the top-right dropdown:
  - **Run All Tests (testng.xml)**
  - **Smoke Tests Only**
  - **LoginTest (all methods)**

---

## 🧪 What's covered

| Test class | Scenarios |
|------------|-----------|
| `LoginTest` | Page loads, manager login, employee demo-pill login, wrong-password error |
| `SignupTest` | New employee signup, new manager signup, duplicate-email error |
| `ManagerTaskTest` | Dashboard loads, create task + auto-allocation banner, task appears in table |
| `EmployeeTaskTest` | Full task lifecycle: manager creates → employee advances to Done |
| `ProfileTest` | Add skill, toggle availability, seeded employee has skills |

---

## 🏷️ Test groups

| Group | Purpose |
|-------|---------|
| `smoke` | Fast critical-path checks |
| `login` / `signup` / `manager` / `employee` / `profile` | Per-feature |
| `negative` | Error / edge cases |
| `e2e` | Full end-to-end flows |

---

## 📊 Reports

After a run, open:
- `test-output/index.html` — TestNG HTML report
- `target/surefire-reports/emailable-report.html` — Maven Surefire report

---

## 🔑 Demo credentials (seeded by the backend)

| Role | Email | Password |
|------|-------|----------|
| Manager | manager@demo.com | password123 |
| Employee (Java expert) | alice@demo.com | password123 |
| Employee (React expert) | bob@demo.com | password123 |
| Employee (DevOps) | charlie@demo.com | password123 |

---

## 🛠️ Tech stack

- Selenium Java `4.21.0`
- TestNG `7.10.2`
- WebDriverManager `5.8.0` (auto driver management)
- Java 17, Maven
