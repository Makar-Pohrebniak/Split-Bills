# 💸 SplitBills API

**SplitBills** is a high-performance RESTful service designed to manage group expenses and automate debt settlement. The project is built with a focus on **Clean Architecture**, security, and high scalability.

---

## 🛠 Tech Stack
* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 3.x
* **Security:** Spring Security, Stateless JWT Authentication
* **Persistence:** Spring Data JPA (Hibernate 6)
* **Database:** MySQL 8.x (Optimized for UUID storage)
* **Testing:** JUnit 5, Mockito, AssertJ
* **Migrations:** Liquibase
* **Containerization** Docker

---

## 🏗 Phase 1: Authentication & Security (Current Progress)
The core security foundation has been successfully implemented, covering:

* **US-1: User Registration**: Secure account creation with data validation and password hashing (BCrypt).
* **US-2: User Login**: Secure user authentication and credential verification.
* **US-3: JWT Issuance**: Generation of secure, signed tokens for stateless request authorization.
* **US-4: Secure endpoints**: Add authorization with Bearer token to all endpoints except for authentication layer.
* **US-0: Infrastructure and Database Versioning**: Containerization of the application and database using Docker Compose; automated schema management and migrations via Liquibase.

### 🛡 Key Architectural Decisions
1. **UUID as Primary Key (`sub_id`)**:
    - Instead of standard auto-incremented `Long` IDs, I use **UUIDs** for user identification within JWTs and the system core.
    - **Impact:** This prevents **IDOR (Insecure Direct Object Reference)** attacks — unauthorized users cannot guess or iterate through other users' IDs.
2. **Automated ID Generation**:
    - Leveraging Hibernate's UUID generation strategies to ensure unique, non-sequential identifiers for every user entity automatically upon persistence.
    - **Impact:** Ensures consistent and automated ID creation during the persistence layer, simplifying the entity lifecycle management.
3. **Stateless Architecture**:
    - By utilizing JWTs, the service remains entirely stateless, making it ready for horizontal scaling in a `microservices` environment.
4. **Authorization on all layers except for auth layer**:
    - Auth layer: login, register have to be available and other layers only with proper authorization.
5. **Infrastructure & Migrations (Docker & Liquibase)**:
   - The application is fully containerized with **Docker Compose**, and the database schema is managed via **Liquibase** migrations.

---

## 🧪 Testing Strategy
Code quality is enforced through a rigorous unit testing suite:
* **`AuthServiceTest`**: Full coverage of registration and login business logic using **Mockito**.
* **`JwtUtilsTest`**: Verification of the token lifecycle (generation/validation) using **`ReflectionTestUtils`** to simulate the Spring environment without the overhead of a full application context.
* **`AuthControllerTest`**: Full coverage of registration and login endpoints using **WebMvcTest**.
* **`UserControllerTest`**: Full coverage of get user endpoints using **WebMvcTest**.
* **`UserServiceTest`**: Full coverage of getting user with different ways business logic using **Mockito**.

---

## 🗺 Roadmap
- [x] **Phase 1: Foundation** (Auth, JWT, Security Config)
- [x] **Phase 0: Containerization** (Liquibase, Docker compose)
- [ ] **Phase 2: Social** (User Profiles, Search, Friend System)
- [ ] **Phase 3: Core Logic** (Groups Management, Participant Roles)
- [ ] **Phase 4: Finance** (Expense Tracking, Equal/Unequal Split Algorithms)
- [ ] **Phase 5: Optimization** (Debt Settlement Algorithm - Minimizing Transactions)
- [ ] **Phase 6: DevOps** (Dockerization, CI/CD, Global Exception Handling)

---

## 🚀 Getting Started
1. Clone the repository.
2. Configure your MySQL credentials in `src/main/resources/application.yml`.
3. Run the application: `./mvnw spring-boot:run` and `docker-compose up -d`.

---

## P.S from author
1. This is my pet project, here I want to show you how I can code because I want to be master of coding one day :)
2. I use AI like an assistant, like Tony Stark used Jarvis, but I'm not a millionaire yet :)
3. I believe that AI is our future, but to uncover full potential of AI is in the hands of smart and lazy developers.
3. I don't know if I will be able to finish this project, but I'll do my best.
4. So enjoy and don't let people use you :) (for free)
