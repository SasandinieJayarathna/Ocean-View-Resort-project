# Ocean View Resort — Room Reservation System

> **Online Room Reservation System for Ocean View Resort, Galle, Sri Lanka**
> CIS6003 Advanced Programming — Cardiff Metropolitan University

---

## Features

- **User Authentication** — Secure login with BCrypt password hashing and session management
  - Ocean-themed login page with professional resort background image
  - OVR Galle logo branding
- **Room Reservation Management** — Create, view, search, and cancel reservations
- **Automated Billing** — Generate invoices with multiple pricing strategies (Standard, Seasonal, Loyalty)
- **Meal Plan Pricing** — Four meal plan options per room: Room Only (RO), Bed & Breakfast (BB), Half Board (HB), Full Board (FB)
  - Professional invoice headers with resort logo (OVR Galle)
  - Contact information display (landline, WhatsApp)
  - Detailed invoice footers with payment terms and resort information
  - Print-optimized invoice layouts for easy printing
- **Business Reports** — Occupancy and revenue reports with stored procedure backends
- **Role-Based Access** — Staff and Admin roles with different privileges
- **Audit Trail** — Automatic database triggers log all reservation changes
- **Input Validation** — Both client-side and server-side validation
- **Help System** — Built-in user guide for new staff members
- **Professional Branding** — Ocean View Resort branding with OVR Galle logo and Galle location emphasis

---

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  index.html │ dashboard.html │ reservations │ billing │ ... │
│  style.css  │ app.js (Vanilla JavaScript + Fetch API)       │
├─────────────────────────────────────────────────────────────┤
│                      SERVLET LAYER                           │
│  AuthFilter │ LoginServlet │ ReservationServlet │ ...        │
├─────────────────────────────────────────────────────────────┤
│                   BUSINESS LOGIC LAYER                       │
│  AuthService │ ReservationService │ BillingService │ ...     │
├─────────────────────────────────────────────────────────────┤
│                    DATA ACCESS LAYER                         │
│  UserDAO │ RoomDAO │ ReservationDAO │ BillDAO (JDBC)         │
├─────────────────────────────────────────────────────────────┤
│                       DATABASE                               │
│  MySQL 8.0 │ Stored Procedures │ Triggers │ Constraints      │
└─────────────────────────────────────────────────────────────┘
```

---

## Design Patterns

| Pattern | Type | Implementation | Purpose |
|---------|------|----------------|---------|
| **Singleton** | Creational | `DBConnectionManager` | Single database connection manager |
| **Factory** | Creational | `RoomFactory` | Create room subtypes by type string |
| **DAO** | Structural | `*DAO` → `*DAOImpl` | Decouple business logic from database |
| **Strategy** | Behavioral | `BillingStrategy` + 3 impls | Interchangeable billing algorithms |
| **Observer** | Behavioral | `ReservationNotifier` | Decouple notifications from reservations |

---

## SOLID Principles

| Principle | Evidence |
|-----------|----------|
| **Single Responsibility** | Each class has one job (AuthService = auth, BillingService = billing) |
| **Open-Closed** | New BillingStrategy without modifying BillingService |
| **Liskov Substitution** | StandardRoom/DeluxeRoom/SuiteRoom substitute for Room |
| **Interface Segregation** | Separate focused DAO interfaces per entity |
| **Dependency Inversion** | Services depend on DAO interfaces, not implementations |

---

## Tech Stack

- **Language:** Java 17
- **Web:** Java Servlets 4.0 (no frameworks)
- **Database:** MySQL 8.0 with JDBC
- **Frontend:** Pure HTML5, CSS3, Vanilla JavaScript
- **Build:** Maven 3.9+
- **Testing:** JUnit 5, Mockito 5, JaCoCo (>70% coverage)
- **CI/CD:** GitHub Actions
- **Server:** Apache Tomcat 7 (embedded via Maven plugin)

---

## Prerequisites

- Java 17+ ([Download](https://adoptium.net/))
- Maven 3.9+ ([Download](https://maven.apache.org/))
- MySQL 8.0 ([Download](https://dev.mysql.com/downloads/))
- Git ([Download](https://git-scm.com/))

---

## Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/SasandinieJayarathna/Ocean-View-Resort-project.git
cd Ocean-View-Resort-project
```

### 2. Setup the database
```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

### 3. Configure database credentials

Create `src/main/resources/db.properties` with your MySQL credentials:
```properties
db.url=jdbc:mysql://localhost:3306/oceanview_resort?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=YOUR_MYSQL_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver
```

### 4. Build the project
```bash
mvn clean compile
```

### 5. Run tests
```bash
mvn test
mvn jacoco:report  # Coverage at target/site/jacoco/index.html
```

### 6. Run the application

> **Note:** The application must be running before you open the URL in the browser.
> If you see "404 Not Found" or "ERR_CONNECTION_REFUSED", the server is not started yet.

**Option A — Maven embedded Tomcat (recommended):**
```bash
mvn tomcat7:run
```
Wait for `INFO: Starting ProtocolHandler ["http-bio-8081"]` in the console, then open your browser and go to:
```
http://localhost:8081/oceanview/
```

**Option B — XAMPP Tomcat (or standalone Tomcat on port 8080):**
```bash
mvn clean package -DskipTests
# Copy target/oceanview-reservation.war to <TOMCAT_HOME>/webapps/
# Start Tomcat
```
Then open your browser and go to:
```
http://localhost:8080/oceanview/
```

> **Note:** These addresses only work on YOUR computer while the server is running.
> If you see a blank page or connection error, run `mvn tomcat7:run` first and wait for it to finish starting.

### 7. Default login credentials

| Username | Password | Role |
|----------|----------|------|
| admin | 123 | ADMIN |
| staff1 | terry | STAFF |

---

## User Interface & Branding

### Visual Design
- **Color Scheme:** Ocean-blue theme (#0077B6, #00B4D8) reflecting the resort's coastal location
- **Typography:** System fonts for cross-platform compatibility
- **Responsive Design:** Works on desktop, tablet, and mobile devices
- **Accessibility:** Semantic HTML5, ARIA labels, and keyboard navigation support

### Key UI Components

#### 1. **Login Page (index.html)**
- Full-screen ocean background image (Ocean resort.webp)
- Professional login card with glassmorphism effect
- OVR Galle logo branding
- Responsive form validation with error handling

#### 2. **Billing Invoice**
- **Header Section:**
  - OVR Galle logo (80x80px)
  - Resort name and Galle location
  - Contact information (landline: +94-91-222-3333, WhatsApp: +94-76-900-1234)
- **Body Section:**
  - Professional reservation details grid
  - Itemized billing table with calculations
  - Multiple billing strategy support (Standard, Seasonal, Loyalty)
- **Footer Section:**
  - Contact information section
  - Payment terms (30-day credit terms)
  - Resort copyright and legal notice
  - Print-friendly layout optimization

#### 3. **Dashboard & Navigation**
- Fixed navigation bar with ocean-themed styling
- Consistent color palette across all pages
- Role-based menu items for Staff and Admin users

### Assets
- **Logo:** `/images/ovr-logo.svg` — Professional OVR Galle branding logo
- **Background:** `/images/Ocean resort.webp` — High-resolution resort image (245KB)
- **Icons:** Embedded in CSS (no external icon library)

### Print Optimization
- Invoice printing with proper page breaks and margins
- Hide navigation and buttons in print view
- Black-on-white contrast for clear printed documents
- Professional invoice formatting suitable for guest records

---

## Project Structure

```
src/
├── main/
│   ├── java/com/oceanview/
│   │   ├── model/          # Domain classes (User, Room, Reservation, Bill)
│   │   ├── dao/            # Data Access Objects (interfaces + JDBC impls)
│   │   ├── service/        # Business logic (Auth, Reservation, Billing, Report)
│   │   ├── servlet/        # HTTP endpoints (Login, Reservation, Billing, etc.)
│   │   ├── filter/         # Authentication filter
│   │   ├── util/           # Utilities (DBManager, Validator, NumberGenerator)
│   │   └── pattern/        # Design patterns (Factory, Strategy, Observer)
│   ├── resources/
│   │   └── db/             # SQL schema
│   └── webapp/             # Frontend (HTML, CSS, JS)
└── test/                   # JUnit 5 + Mockito tests
```

---

## Versioning

This project follows [Semantic Versioning](https://semver.org/):

| Version | Description |
|---------|-------------|
| v1.0.0 | Initial project structure |
| v1.1.0 | Models, utilities, Factory pattern |
| v1.2.0 | Complete DAO layer |
| v1.3.0 | Service layer and all design patterns |
| v1.4.0 | All servlets complete |
| v1.5.0 | Complete frontend UI |
| v1.6.0 | CI/CD pipeline |
| v2.0.0 | Production-ready release |
| v2.1.0 | Enhanced login page, SVG logo, professional branding |
| v2.2.0 | Meal plan pricing (RO/BB/HB/FB), updated Sri Lankan resort room categories |
| v2.3.0 | Fix Java 17 Gson serialization bug, expand to 50 rooms, fix session redirect and view reservations |

---

## Screenshots

*(Add screenshots from the running application here)*

---

## GitHub Repository

**URL:** [https://github.com/SasandinieJayarathna/Ocean-View-Resort-project](https://github.com/SasandinieJayarathna/Ocean-View-Resort-project)

---

## How to Demonstrate the Database During Viva

During your viva examination, you may want to show the database structure and sample data:

### Step 1: Access MySQL Database
```bash
# Open MySQL command line
mysql -u root -p

# Enter password: root

# Select the database
USE oceanview_resort;
```

### Step 2: Show Database Tables
```sql
-- List all tables
SHOW TABLES;

-- Output should show:
-- users, rooms, reservations, bills, audit_log
```

### Step 3: Show Key Data

**View Users Table:**
```sql
SELECT * FROM users;
-- Shows: admin (password: 123) and staff1 (password: terry)
```

**View Rooms Table:**
```sql
SELECT * FROM rooms;
-- Shows: 50 rooms (22 standard, 20 deluxe, 8 suites) with prices

-- Summary by category:
SELECT room_type, COUNT(*) as total FROM rooms GROUP BY room_type;
-- STANDARD: 22  |  DELUXE: 20  |  SUITE: 8
```

**View Reservations (with guest details):**
```sql
SELECT reservation_number, guest_name, contact_number, room_type, check_in_date, check_out_date, status FROM reservations LIMIT 5;
```

**View Bills (with pricing calculations):**
```sql
SELECT b.bill_id, r.reservation_number, b.number_of_nights, b.room_rate, b.subtotal, b.discount_amount, b.tax_amount, b.total_amount, b.billing_strategy
FROM bills b
JOIN reservations r ON b.reservation_id = r.reservation_id;
```

### Step 4: Show Stored Procedures
```sql
-- List stored procedures
SHOW PROCEDURE STATUS WHERE Db = 'oceanview_resort';

-- Call the stored procedure to show available rooms
CALL sp_get_available_rooms('2026-03-10', '2026-03-12', 'DELUXE');
```

### Step 5: Show Audit Log (Triggers)
```sql
-- This table is automatically populated by triggers when reservations change
SELECT * FROM audit_log LIMIT 5;
-- Shows: table_name, action_type (INSERT/UPDATE/DELETE), record_id, old_values, new_values, timestamp
```

### Key Points to Mention:

1. **Password Security**: Passwords are stored as BCrypt hashes (e.g., `$2a$10$...`), never plain text
2. **Data Integrity**: Foreign keys ensure reservations always link to valid rooms
3. **Audit Trail**: Triggers automatically log all changes for compliance
4. **Business Logic**: Stored procedures encapsulate complex queries (available rooms, reports)
5. **Sample Data**: Default admin account created for testing

---

## License

This project is developed for academic purposes as part of the CIS6003 module at Cardiff Metropolitan University.

---

## References

- Gamma, E. et al. (1994) *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
- Freeman, E. and Robson, E. (2020) *Head First Design Patterns*. 2nd edn. O'Reilly Media.
- Bloch, J. (2018) *Effective Java*. 3rd edn. Addison-Wesley.
- Oracle (2024) *Java Servlet Specification*. Available at: https://jakarta.ee/specifications/servlet/
- MySQL (2024) *MySQL 8.0 Reference Manual*. Available at: https://dev.mysql.com/doc/refman/8.0/en/
