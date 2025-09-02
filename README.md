Hotelix - Backend System (Spring Boot)
📌 Overview

Hotelix is a hotel/restaurant management system backend developed with Spring Boot and MySQL.
It provides RESTful APIs to manage tables, orders, dishes, payments, expenses, and reports.

The system ensures:

✅ Data validation on insert/update

✅ Safe deletion with dependency checks

✅ Prevention of data duplication

✅ Support for split payments & dues

✅ Date-based reporting

🛠️ Tech Stack

Backend Framework: Spring Boot (Java)

Database: MySQL

Build Tool: Maven

Validation: Spring Validation & custom business rules

API Style: RESTful APIs

📂 Project Structure
hotel-management/
├── src/
│   ├── main/
│   │   ├── java/com/hotelix/hotel_management/
│   │   │   ├── common/     # Shared utilities, constants
│   │   │   ├── config/     # Configuration (DB, security, etc.)
│   │   │   ├── dish/       # Dish-related controllers, services, repositories
│   │   │   ├── expense/    # Expense & income management
│   │   │   ├── order/      # Orders & order items
│   │   │   ├── table/      # Hotel table management
│   │   │   └── HotelManagementApplication.java  # Entry point
│   │   └── resources/      # application.properties, static files
│   ├── test/               # Unit & integration tests
├── target/                 # Build output
├── pom.xml                 # Maven dependencies
└── README.md

📊 Diagrams
🔹 Activity Diagram (Admin Flow)
<img width="526" height="951" alt="Action Diagram" src="https://github.com/user-attachments/assets/73a50697-23d2-49b0-bc2c-185770bbc8e2" />

🔹 ER Diagram
<img width="1401" height="721" alt="ERDiagram" src="https://github.com/user-attachments/assets/8acea475-d4f3-422b-88c6-34d4f9ba312c" />


🚀 Features

Table Management → Create, update, delete, and fetch hotel tables.

Order Management → Place orders, add dishes, calculate bill amounts.

Payment Handling → Supports Cash, UPI, Card, and Due payments with split payment functionality.

Due Tracking → Manage dues with customer details.

Expense Recording → Record hotel income and expenses.

Reports → Generate reports with date-based filtering.

Validations →

Prevent duplicate entries

Restrict deletion if references exist

Validate due amounts
