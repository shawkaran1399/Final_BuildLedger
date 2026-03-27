# BuildLedger — Construction Contract & Vendor Management System

A **production-grade monolithic Spring Boot application** that consolidates all modules (IAM, Vendor, Contract, Delivery, Finance, Compliance, Reporting, Notifications) into a single deployable unit backed by MySQL.

---

## 📋 Table of Contents
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup & Run](#setup--run)
- [Default Admin Credentials](#default-admin-credentials)
- [API Overview](#api-overview)
- [RBAC / Role Matrix](#rbac--role-matrix)
- [JWT Flow](#jwt-flow)
- [Swagger UI](#swagger-ui)
- [Database](#database)

---

## 🛠 Tech Stack

| Layer           | Technology                          |
|-----------------|-------------------------------------|
| Language        | Java 17                             |
| Framework       | Spring Boot 3.2.5                   |
| Security        | Spring Security + JWT (jjwt 0.11.5) |
| Persistence     | Spring Data JPA + Hibernate         |
| Database        | MySQL 8+                            |
| API Docs        | SpringDoc OpenAPI 3 (Swagger UI)    |
| Utilities       | Lombok                              |
| Build Tool      | Maven                               |

---

## 📁 Project Structure

```
com.buildledger
├── BuildLedgerApplication.java       # Entry point
├── bootstrap/
│   └── AdminBootstrap.java           # Seeds default admin on startup
├── config/
│   ├── JpaAuditingConfig.java
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── AuditLogController.java
│   ├── ComplianceController.java
│   ├── ContractController.java
│   ├── DeliveryController.java
│   ├── InvoiceController.java
│   ├── NotificationController.java
│   ├── PaymentController.java
│   ├── ProjectController.java
│   ├── ReportController.java
│   ├── ServiceController.java
│   └── UserController.java
├── dto/
│   ├── request/                      # All incoming request DTOs
│   └── response/                     # All outgoing response DTOs + ApiResponse<T>
├── entity/                           # JPA entities (14 tables)
├── enums/                            # All domain enumerations
├── exception/
│   ├── GlobalExceptionHandler.java   # @ControllerAdvice
│   ├── BadRequestException.java
│   ├── BuildLedgerAccessDeniedException.java
│   ├── DuplicateResourceException.java
│   └── ResourceNotFoundException.java
├── repository/                       # Spring Data JPA repositories
├── security/
│   ├── JwtAuthEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtUtils.java
│   └── UserDetailsServiceImpl.java
└── service/
    ├── (interfaces)
    └── impl/
        ├── AuthServiceImpl.java
        ├── AuditLogService.java
        ├── ComplianceServiceImpl.java
        ├── ContractServiceImpl.java
        ├── DeliveryServiceImpl.java
        ├── InvoiceServiceImpl.java
        ├── NotificationServiceImpl.java
        ├── PaymentServiceImpl.java
        ├── ProjectServiceImpl.java
        ├── ReportServiceImpl.java
        ├── ServiceTrackingServiceImpl.java
        ├── UserServiceImpl.java
        └── VendorServiceImpl.java
```

---

## ✅ Prerequisites

| Tool       | Version    |
|------------|------------|
| Java JDK   | 17+        |
| Maven      | 3.8+       |
| MySQL      | 8.0+       |

---

## 🚀 Setup & Run

### 1. Create MySQL Database

```sql
CREATE DATABASE buildledger_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'buildledger'@'localhost' IDENTIFIED BY 'yourpassword';
GRANT ALL PRIVILEGES ON buildledger_db.* TO 'buildledger'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configure `application.yml`

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/buildledger_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
    username: buildledger       # ← your MySQL user
    password: yourpassword      # ← your MySQL password

jwt:
  secret: BuildLedgerSecretKeyForJWTTokenGenerationMustBe256BitsLong123456789
  expiration: 86400000          # 24 hours
```

### 3. Build & Run

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/buildledger-1.0.0.jar

# Or with Maven directly
mvn spring-boot:run
```

The server starts at: `http://localhost:8080/api`

---

## 🔐 Default Admin Credentials

On **first startup**, the system automatically creates:

| Field    | Value          |
|----------|----------------|
| Username | `admin`        |
| Password | `admin123`     |
| Role     | `ADMIN`        |

> ⚠️ **Change the default password immediately in production!**

---

## 📡 API Overview

All endpoints are prefixed with `/api`.

### Authentication
| Method | Path           | Access  | Description              |
|--------|----------------|---------|--------------------------|
| POST   | /auth/login    | PUBLIC  | Login, returns JWT token |

### User Management
| Method | Path                  | Role    | Description               |
|--------|-----------------------|---------|---------------------------|
| POST   | /users                | ADMIN   | Create a new user         |
| GET    | /users                | ADMIN   | List all users            |
| GET    | /users/{id}           | ADMIN   | Get user by ID            |
| GET    | /users/role/{role}    | ADMIN   | Filter users by role      |
| PUT    | /users/{id}           | ADMIN   | Update user               |
| DELETE | /users/{id}           | ADMIN   | Delete user               |

### Vendor Management
| Method | Path                               | Role                      | Description               |
|--------|------------------------------------|---------------------------|---------------------------|
| POST   | /vendors                           | ADMIN                     | Register vendor           |
| GET    | /vendors                           | PUBLIC                    | List all vendors          |
| GET    | /vendors/{id}                      | PUBLIC                    | Get vendor                |
| PUT    | /vendors/{id}                      | ADMIN                     | Update vendor             |
| DELETE | /vendors/{id}                      | ADMIN                     | Delete vendor             |
| POST   | /vendors/{id}/documents            | ADMIN, COMPLIANCE_OFFICER | Upload document           |
| GET    | /vendors/{id}/documents            | PUBLIC                    | List documents            |
| PATCH  | /vendors/documents/{id}/verify     | COMPLIANCE_OFFICER, ADMIN | Verify document           |

### Projects
| Method | Path              | Role                      | Description       |
|--------|-------------------|---------------------------|-------------------|
| POST   | /projects         | ADMIN, PROJECT_MANAGER    | Create project    |
| GET    | /projects         | PUBLIC                    | List projects     |
| GET    | /projects/{id}    | PUBLIC                    | Get project       |
| PUT    | /projects/{id}    | ADMIN, PROJECT_MANAGER    | Update project    |
| DELETE | /projects/{id}    | ADMIN                     | Delete project    |

### Contracts
| Method | Path                          | Role                   | Description            |
|--------|-------------------------------|------------------------|------------------------|
| POST   | /contracts                    | PROJECT_MANAGER, ADMIN | Create contract        |
| GET    | /contracts                    | PUBLIC                 | List contracts         |
| GET    | /contracts/{id}               | PUBLIC                 | Get contract           |
| GET    | /contracts/vendor/{vid}       | PUBLIC                 | By vendor              |
| GET    | /contracts/project/{pid}      | PUBLIC                 | By project             |
| PUT    | /contracts/{id}               | PROJECT_MANAGER, ADMIN | Update contract        |
| PATCH  | /contracts/{id}/status        | PROJECT_MANAGER, ADMIN | Change status          |
| DELETE | /contracts/{id}               | ADMIN                  | Delete contract        |
| POST   | /contracts/{id}/terms         | PROJECT_MANAGER, ADMIN | Add term               |
| GET    | /contracts/{id}/terms         | PUBLIC                 | Get terms              |
| DELETE | /contracts/terms/{termId}     | PROJECT_MANAGER, ADMIN | Remove term            |

### Deliveries
| Method | Path                           | Role                              | Description      |
|--------|--------------------------------|-----------------------------------|------------------|
| POST   | /deliveries                    | VENDOR, PROJECT_MANAGER, ADMIN    | Record delivery  |
| GET    | /deliveries                    | PUBLIC                            | List deliveries  |
| GET    | /deliveries/{id}               | PUBLIC                            | Get delivery     |
| GET    | /deliveries/contract/{cid}     | PUBLIC                            | By contract      |
| PUT    | /deliveries/{id}               | VENDOR, PROJECT_MANAGER, ADMIN    | Update delivery  |
| PATCH  | /deliveries/{id}/status        | PROJECT_MANAGER, ADMIN            | Accept/reject    |
| DELETE | /deliveries/{id}               | ADMIN                             | Delete delivery  |

### Services
| Method | Path                         | Role                              | Description     |
|--------|------------------------------|-----------------------------------|-----------------|
| POST   | /services                    | VENDOR, PROJECT_MANAGER, ADMIN    | Record service  |
| GET    | /services                    | PUBLIC                            | List services   |
| GET    | /services/{id}               | PUBLIC                            | Get service     |
| GET    | /services/contract/{cid}     | PUBLIC                            | By contract     |
| PATCH  | /services/{id}/status        | PROJECT_MANAGER, ADMIN            | Update status   |
| DELETE | /services/{id}               | ADMIN                             | Delete service  |

### Invoices
| Method | Path                         | Role                      | Description       |
|--------|------------------------------|---------------------------|-------------------|
| POST   | /invoices                    | VENDOR, ADMIN             | Submit invoice    |
| GET    | /invoices                    | PUBLIC                    | List invoices     |
| GET    | /invoices/{id}               | PUBLIC                    | Get invoice       |
| GET    | /invoices/contract/{cid}     | PUBLIC                    | By contract       |
| GET    | /invoices/status/{status}    | PUBLIC                    | By status         |
| PATCH  | /invoices/{id}/approve       | FINANCE_OFFICER, ADMIN    | Approve invoice   |
| PATCH  | /invoices/{id}/reject        | FINANCE_OFFICER, ADMIN    | Reject invoice    |
| DELETE | /invoices/{id}               | ADMIN                     | Delete invoice    |

### Payments
| Method | Path                         | Role                      | Description        |
|--------|------------------------------|---------------------------|--------------------|
| POST   | /payments                    | FINANCE_OFFICER, ADMIN    | Process payment    |
| GET    | /payments                    | PUBLIC                    | List payments      |
| GET    | /payments/{id}               | PUBLIC                    | Get payment        |
| GET    | /payments/invoice/{iid}      | PUBLIC                    | By invoice         |
| PATCH  | /payments/{id}/status        | FINANCE_OFFICER, ADMIN    | Update status      |

### Compliance & Audits
| Method | Path                              | Role                      | Description            |
|--------|-----------------------------------|---------------------------|------------------------|
| POST   | /compliance                       | COMPLIANCE_OFFICER, ADMIN | Create record          |
| GET    | /compliance                       | PUBLIC                    | List records           |
| GET    | /compliance/{id}                  | PUBLIC                    | Get record             |
| GET    | /compliance/contract/{cid}        | PUBLIC                    | By contract            |
| POST   | /audits                           | COMPLIANCE_OFFICER, ADMIN | Schedule audit         |
| GET    | /audits                           | PUBLIC                    | List audits            |
| GET    | /audits/{id}                      | PUBLIC                    | Get audit              |
| PATCH  | /audits/{id}/status               | COMPLIANCE_OFFICER, ADMIN | Update audit           |

### Reports
| Method | Path                  | Role                                          | Description      |
|--------|-----------------------|-----------------------------------------------|------------------|
| POST   | /reports              | ADMIN, FINANCE_OFFICER, PROJECT_MANAGER       | Generate report  |
| GET    | /reports              | PUBLIC                                        | List reports     |
| GET    | /reports/{id}         | PUBLIC                                        | Get report       |
| GET    | /reports/scope/{s}    | PUBLIC                                        | By scope         |

### Notifications
| Method | Path                          | Access         | Description         |
|--------|-------------------------------|----------------|---------------------|
| GET    | /notifications/user/{uid}     | Authenticated  | User notifications  |
| GET    | /notifications/user/{uid}/unread | Authenticated | Unread only      |
| GET    | /notifications/user/{uid}/count | Authenticated | Unread count     |
| PATCH  | /notifications/{id}/read      | Authenticated  | Mark as read        |

### Audit Logs
| Method | Path                    | Role  | Description           |
|--------|-------------------------|-------|-----------------------|
| GET    | /audit-logs             | ADMIN | All system logs       |
| GET    | /audit-logs/user/{uid}  | ADMIN | Logs by user          |

---

## 👥 RBAC / Role Matrix

| Action                    | ADMIN | PROJECT_MANAGER | VENDOR | FINANCE_OFFICER | COMPLIANCE_OFFICER |
|---------------------------|:-----:|:---------------:|:------:|:---------------:|:------------------:|
| Create users              | ✅    | ❌              | ❌     | ❌              | ❌                 |
| Manage vendors            | ✅    | ❌              | ❌     | ❌              | ❌                 |
| Create contracts          | ✅    | ✅              | ❌     | ❌              | ❌                 |
| Record deliveries         | ✅    | ✅              | ✅     | ❌              | ❌                 |
| Accept/reject delivery    | ✅    | ✅              | ❌     | ❌              | ❌                 |
| Submit invoices           | ✅    | ❌              | ✅     | ❌              | ❌                 |
| Approve/reject invoices   | ✅    | ❌              | ❌     | ✅              | ❌                 |
| Process payments          | ✅    | ❌              | ❌     | ✅              | ❌                 |
| Create compliance records | ✅    | ❌              | ❌     | ❌              | ✅                 |
| Schedule audits           | ✅    | ❌              | ❌     | ❌              | ✅                 |
| View audit logs           | ✅    | ❌              | ❌     | ❌              | ❌                 |

---

## 🔑 JWT Flow

```
1. POST /api/auth/login  →  { username, password }
2. Server validates credentials
3. Server returns { accessToken, tokenType: "Bearer", expiresIn, userId, role, ... }
4. Client includes header:  Authorization: Bearer <accessToken>
5. JwtAuthenticationFilter validates token on every request
6. @PreAuthorize checks role for protected endpoints
```

---

## 📖 Swagger UI

After starting the application, open:

```
http://localhost:8080/api/swagger-ui.html
```

**To authenticate in Swagger:**
1. Call `POST /auth/login` with `{ "username": "admin", "password": "admin123" }`
2. Copy the `accessToken` from the response
3. Click the **Authorize 🔒** button at the top right
4. Enter: `Bearer <your_token>`
5. Click **Authorize** — all secured endpoints are now unlocked

---

## 🗄 Database

All entities share a **single MySQL database** (`buildledger_db`):

| Table               | Description                         |
|---------------------|-------------------------------------|
| users               | All system users (all roles)        |
| vendors             | Registered vendors/contractors      |
| vendor_documents    | Compliance documents per vendor     |
| projects            | Construction projects               |
| contracts           | Contracts linking vendors & projects|
| contract_terms      | Individual terms within contracts   |
| deliveries          | Material delivery records           |
| services            | Service completion records          |
| invoices            | Vendor invoices                     |
| payments            | Payment transactions                |
| compliance_records  | Compliance check results            |
| audits              | Formal audit records                |
| reports             | Generated analytics reports         |
| notifications       | In-app notifications                |
| audit_logs          | Immutable system event log          |

Schema is auto-created/updated by Hibernate (`ddl-auto: update`).

---

## 🔄 Example Workflow

```
1.  Application starts → admin user auto-created
2.  ADMIN logs in       → POST /auth/login
3.  ADMIN creates users → POST /users  (role: PROJECT_MANAGER, VENDOR, etc.)
4.  ADMIN creates vendor→ POST /vendors
5.  PROJECT_MANAGER creates project → POST /projects
6.  PROJECT_MANAGER creates contract → POST /contracts
7.  PROJECT_MANAGER adds terms → POST /contracts/{id}/terms
8.  VENDOR records delivery → POST /deliveries
9.  PROJECT_MANAGER accepts delivery → PATCH /deliveries/{id}/status?status=ACCEPTED
10. VENDOR submits invoice → POST /invoices
11. FINANCE_OFFICER approves invoice → PATCH /invoices/{id}/approve
12. FINANCE_OFFICER processes payment → POST /payments
13. COMPLIANCE_OFFICER schedules audit → POST /audits
14. COMPLIANCE_OFFICER updates findings → PATCH /audits/{id}/status
```
