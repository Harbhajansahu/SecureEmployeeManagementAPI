# 🔐 Secure Employee Management API

A secure and scalable Employee Management REST API built using **Spring Boot, Spring Security, JWT, JPA/Hibernate, and MySQL**.

The project demonstrates real-world backend security concepts including JWT authentication, refresh token rotation, token revocation, role-based authorization, password management, validation, exception handling, CORS, and Swagger/OpenAPI documentation.

---

## 🚀 Features

- User Registration
- User Login
- BCrypt Password Hashing
- JWT Access Token Authentication
- JWT Refresh Token
- Refresh Token Rotation
- Refresh Token Revocation
- Secure Logout
- Change Password
- Role-Based Authorization
- USER and ADMIN Roles
- Custom 401 Unauthorized Handling
- Custom 403 Forbidden Handling
- Global Exception Handling
- Request Validation
- CORS Configuration
- Employee CRUD Operations
- Swagger/OpenAPI Documentation
- MySQL Database Integration

---

## 🛠️ Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 |
| Spring Security | 7.1.1 |
| Spring Data JPA |  |
| Hibernate | 7.4.5 |
| MySQL | 8.4.5 |
| JWT | JJWT |
| Maven |  |
| Swagger/OpenAPI | SpringDoc 3.1.1 |
| Build Tool | Maven |

---

# 🔐 Security

This application uses **Spring Security with JWT-based authentication and role-based authorization**.

## Authentication Flow

```text
Client
   |
   | Username + Password
   v
Login API
   |
   v
AuthenticationManager
   |
   v
UserDetailsService
   |
   v
Database
   |
   v
Generate JWT Tokens
   |
   +-------------------+
   |                   |
   v                   v
Access Token      Refresh Token