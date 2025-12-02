# Campus Job Board System

A full-stack web application designed to connect students seeking jobs with employers posting job opportunities on campus. Built with Spring Boot, Spring Security, JPA (MySQL), and Thymeleaf.

**Course:** CPRO 2221 - Programming Java EE  
**Project Type:** Final Project (50% of Final Grade)  
**Term:** Fall 2025

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Database Design](#database-design)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [User Roles & Workflows](#user-roles--workflows)
- [Security Implementation](#security-implementation)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Team Contributions](#team-contributions)

---

## 🎯 Project Overview

The Campus Job Board System provides a secure, user-friendly, and scalable platform where:
- **Employers** can post job openings
- **Students** can browse and apply to jobs
- **Admins** can manage users and approve job posts
- **All users** authenticate securely

---

## ✨ Features

### Authentication & Security
- ✅ User registration and login with Spring Security
- ✅ BCrypt password encryption
- ✅ Role-based authorization (Admin, Employer, Student)
- ✅ Session-based authentication
- ✅ Custom Thymeleaf login page
- ✅ CSRF protection

### Jobs Module
- ✅ Employers create jobs with: Title, Description, Salary, Location, Category, Deadline
- ✅ Jobs remain **Pending** until approved by Admin
- ✅ Admin can **Approve** or **Reject** job posts
- ✅ Students view only **Approved** jobs

### Application Module
- ✅ Students can submit applications (once per job)
- ✅ Employers can view applicants for their posted jobs
- ✅ Duplicate application prevention with unique constraint

### Validation & Error Handling
- ✅ Form validation using `@Valid`, `@NotBlank`, `@Email`, `@Size`
- ✅ Validation error display on Thymeleaf forms
- ✅ Global exception handler (`@ControllerAdvice`)
- ✅ Custom exceptions: `ResourceNotFoundException`, `DuplicateApplicationException`
- ✅ User-friendly error pages

### User Management (Admin)
- ✅ View all user accounts
- ✅ Activate/Deactivate users

---

## 🛠 Technology Stack

| Layer | Technology |
|-------|------------|
| **Backend Framework** | Spring Boot 3.4.12 |
| **Security** | Spring Security 6.x |
| **Data Persistence** | Spring Data JPA / Hibernate |
| **Database** | MySQL 8.0 |
| **Template Engine** | Thymeleaf |
| **Frontend** | Bootstrap 5, Bootstrap Icons |
| **Build Tool** | Maven |
| **Java Version** | Java 21 |
| **Testing** | JUnit 5, Mockito, Spring Security Test |

---

## 🏗 System Architecture

The application follows a **layered architecture** pattern:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│              (Thymeleaf Templates + CSS)                │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                    Controller Layer                      │
│    AuthController, StudentController, EmployerController │
│                    AdminController                       │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                     Service Layer                        │
│   UserService, JobService, ApplicationService,           │
│                    AdminService                          │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   Repository Layer                       │
│   UserRepository, JobRepository, JobApplicationRepository│
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                     Database Layer                       │
│                   MySQL Database                         │
└─────────────────────────────────────────────────────────┘
```

---

## 🗄 Database Design

### Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────┐       ┌───────────────────┐
│     USER     │       │     JOB      │       │  JOB_APPLICATION  │
├──────────────┤       ├──────────────┤       ├───────────────────┤
│ user_id (PK) │◄──┐   │ job_id (PK)  │◄──────│ application_id(PK)│
│ full_name    │   │   │ employer_id  │───┐   │ job_id (FK)       │
│ email        │   │   │ title        │   │   │ student_id (FK)   │
│ password     │   │   │ description  │   │   │ status            │
│ role         │   │   │ location     │   │   │ applied_at        │
│ status       │   └───│ salary       │   │   └───────────────────┘
│ created_at   │       │ category     │   │
└──────────────┘       │ deadline     │   │
        ▲              │ status       │   │
        │              │ created_at   │   │
        │              └──────────────┘   │
        │                                 │
        └─────────────────────────────────┘
```

### Database Tables

**USER Table**
| Column | Type | Constraints |
|--------|------|-------------|
| user_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| full_name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | UNIQUE, NOT NULL |
| password | VARCHAR(255) | NOT NULL |
| role | ENUM('STUDENT','EMPLOYER','ADMIN') | NOT NULL |
| status | ENUM('ACTIVE','INACTIVE') | DEFAULT 'ACTIVE' |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

**JOB Table**
| Column | Type | Constraints |
|--------|------|-------------|
| job_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| employer_id | BIGINT | FOREIGN KEY → USER(user_id) |
| title | VARCHAR(100) | NOT NULL |
| description | TEXT | NOT NULL |
| location | VARCHAR(100) | |
| salary | DECIMAL(10,2) | |
| category | VARCHAR(50) | |
| deadline | DATE | |
| status | ENUM('PENDING','APPROVED','REJECTED') | DEFAULT 'PENDING' |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

**JOB_APPLICATION Table**
| Column | Type | Constraints |
|--------|------|-------------|
| application_id | BIGINT | PRIMARY KEY, AUTO_INCREMENT |
| job_id | BIGINT | FOREIGN KEY → JOB(job_id) |
| student_id | BIGINT | FOREIGN KEY → USER(user_id) |
| status | ENUM('SUBMITTED','ACCEPTED','REJECTED') | DEFAULT 'SUBMITTED' |
| applied_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |
| | | UNIQUE(job_id, student_id) |

---

## ⚙️ Installation & Setup

### Prerequisites
- Java 21 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA recommended)

### Database Setup

1. **Create the MySQL database:**
```sql
CREATE DATABASE campus_job_board;
```

2. **Create a MySQL user (optional):**
```sql
CREATE USER 'jobboard_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON campus_job_board.* TO 'jobboard_user'@'localhost';
FLUSH PRIVILEGES;
```

### Application Configuration

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_job_board
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Build the Project

```bash
# Clone the repository
git clone https://github.com/vancoder1/CampusJobBoardSystem.git
cd CampusJobBoardSystem

# Build with Maven
./mvnw clean install
```

---

## 🚀 Running the Application

### Option 1: Using Maven
```bash
./mvnw spring-boot:run
```

### Option 2: Using IDE
1. Open the project in IntelliJ IDEA
2. Run `CampusJobBoardSystemApplication.java`

### Option 3: Using JAR file
```bash
./mvnw package
java -jar target/CampusJobBoardSystem-0.0.1-SNAPSHOT.jar
```

### Access the Application
Open your browser and navigate to: **http://localhost:8080**

### Default Admin Account
To create an admin account, you can manually insert one into the database:
```sql
INSERT INTO `USER` (full_name, email, password, role, status) 
VALUES ('Admin User', 'admin@campus.edu', 
        '$2a$10$encrypted_password_here', 'ADMIN', 'ACTIVE');
```
> Note: Use BCrypt to encrypt the password before inserting.

---

## 👥 User Roles & Workflows

### Student Workflow
1. **Register** → Create account as Student
2. **Login** → Access student dashboard
3. **Browse Jobs** → View all admin-approved jobs
4. **View Details** → Click on a job for full description
5. **Apply** → Submit application (one per job)
6. **Track Applications** → View "My Applications" list

### Employer Workflow
1. **Register** → Create account as Employer
2. **Login** → Access employer dashboard
3. **Post Job** → Create new job posting
4. **Wait for Approval** → Job status: PENDING
5. **Manage Jobs** → Edit/Update/Delete postings
6. **View Applicants** → See students who applied

### Admin Workflow
1. **Login** → Access admin dashboard
2. **Review Jobs** → View all pending job posts
3. **Approve/Reject** → Change job status
4. **Manage Users** → View all registered users
5. **Activate/Deactivate** → Control user access

---

## 🔐 Security Implementation

### Authentication
- **Spring Security** with form-based login
- **BCryptPasswordEncoder** for password hashing
- **Session-based** authentication management

### Authorization
```java
// URL-based access control
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/employer/**").hasRole("EMPLOYER")
.requestMatchers("/student/**").hasRole("STUDENT")
.requestMatchers("/", "/login", "/register", "/saveUser").permitAll()
```

### Security Features
- CSRF protection enabled
- Session fixation protection
- XSS protection headers
- Secure logout functionality

---

## 🧪 Testing

### Test Structure
```
src/test/java/
└── com/dvlpr/CampusJobBoardSystem/
    ├── CampusJobBoardSystemApplicationTests.java
    ├── controller/
    │   ├── AuthControllerTest.java
    │   ├── StudentControllerTest.java
    │   ├── EmployerControllerTest.java
    │   └── AdminControllerTest.java
    └── service/
        ├── UserServiceTest.java
        └── JobServiceTest.java
```

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthControllerTest

# Run with coverage (in IDE)
Right-click test folder → Run with Coverage
```

### Test Coverage
- **Service Layer Tests**: Business logic validation
- **Controller Tests**: HTTP request/response handling, form validation
- **Integration Tests**: End-to-end workflow testing

---

## 📁 Project Structure

```
CampusJobBoardSystem/
├── src/
│   ├── main/
│   │   ├── java/com/dvlpr/CampusJobBoardSystem/
│   │   │   ├── CampusJobBoardSystemApplication.java
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── StudentController.java
│   │   │   │   ├── EmployerController.java
│   │   │   │   └── AdminController.java
│   │   │   ├── dto/
│   │   │   │   └── UserRegistrationDto.java
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Job.java
│   │   │   │   ├── JobApplication.java
│   │   │   │   ├── UserRole.java
│   │   │   │   ├── UserStatus.java
│   │   │   │   ├── JobStatus.java
│   │   │   │   └── ApplicationStatus.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── DuplicateApplicationException.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── JobRepository.java
│   │   │   │   └── JobApplicationRepository.java
│   │   │   ├── security/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── CustomUserDetails.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java
│   │   │   │   ├── JobService.java
│   │   │   │   ├── ApplicationService.java
│   │   │   │   └── AdminService.java
│   │   │   └── util/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/css/
│   │       │   └── site.css
│   │       └── templates/
│   │           ├── index.html
│   │           ├── login.html
│   │           ├── register.html
│   │           ├── error.html
│   │           ├── fragments/
│   │           │   ├── header.html
│   │           │   └── footer.html
│   │           ├── admin/
│   │           │   ├── dashboard.html
│   │           │   └── users.html
│   │           ├── employer/
│   │           │   ├── dashboard.html
│   │           │   ├── create-job.html
│   │           │   ├── edit-job.html
│   │           │   └── view-applications.html
│   │           └── student/
│   │               ├── dashboard.html
│   │               ├── job-details.html
│   │               └── my-applications.html
│   └── test/
│       └── java/com/dvlpr/CampusJobBoardSystem/
│           ├── controller/
│           └── service/
├── pom.xml
├── README.md
└── LICENSE
```

---

## 👨‍💻 Team Contributions

| Team Member | Role | Responsibilities |
|-------------|------|------------------|
| **Ivan Zaporozhets** | Project Manager / Backend Developer | Project coordination, Entity models, Services, Database setup, Security configuration, Tests |
| **Lukas Dreise** | Frontend Developer | Thymeleaf templates, CSS styling, Form validation, UI/UX design, Features |

### Git Commit History
All team members contributed through feature branches with meaningful commit messages following the convention:
- `feat:` - New features
- `fix:` - Bug fixes
- `docs:` - Documentation
- `test:` - Test additions
- `refactor:` - Code improvements

---

## 📄 License

This project is developed for educational purposes as part of CPRO 2221 - Programming Java EE at Red Deer Polytechnic.

---

## 🙏 Acknowledgments

- Spring Boot Documentation
- Thymeleaf Documentation
- Bootstrap Framework
- Course Instructor and Teaching Assistants

---

**© 2025 Campus Job Board System | CPRO 2221 Final Project**