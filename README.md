# 💸 SplitBills API

**SplitBills** is a high-performance RESTful service designed to manage group expenses and automate debt settlement. The project is built with a focus on **Clean Architecture**, security, and high scalability.

---

## 🛠 Tech Stack
* **Language:** Java 21 (LTS)
* **Framework:** Spring Boot 3.x
* **Security:** Spring Security, Stateless JWT Authentication
* **Persistence:** Spring Data JPA (Hibernate 6)
* **Database:** MySQL 8.x (Optimized for UUID storage), Redis
* **Testing:** JUnit 5, Mockito, AssertJ
* **DB versioning:** Liquibase
* **Containerization:** Docker
* **Messaging & Async:** Apache Kafka (Event-driven notifications and payment status processing)

---

## 🏗 Phase 1: Authentication & Security (Current Progress)
The core security foundation has been successfully implemented, covering:

* **US-1: User Registration**: Secure account creation with data validation and password hashing (BCrypt).
* **US-2: User Login**: Secure user authentication and credential verification.
* **US-3: JWT Issuance**: Generation of secure, signed tokens for stateless request authorization.
* **US-4: Secure endpoints**: Add authorization with Bearer token to all endpoints except for authentication layer.
* **US-0: Infrastructure and Database Versioning**: Containerization of the application and database using Docker Compose; automated schema management and migrations via Liquibase.

## 🏗 Phase 2: Searching for users and friends
Searching for users and friends in different ways:
* **US-5 & US-6 Searching for users**: Searching for people with username or email.
* **US-7 Friends list**: Adding, deleting and getting list of friends.

## 🏗 Phase 3: Groups & participants
Managing groups:
* **US-8 & US-10 Create/delete/get groups**: Creating, deleting, getting all the groups.
* **US-9 Add/remove/get members of the group**: Adding, removing, getting members.
* **US-Additional**: Added currency to the groups. Default - UAH. 

## 🏗 Phase 4: Expense management
Managing expenses:
* **US-11 Add Expense**: Implementing CRUD operations for expenses, allowing group members to add, update, delete, and retrieve expense details (amount, description, category, and payer).
* **US-12 Split Equally**: Implementing logic to automatically and evenly distribute the total expense amount among all participants in the group.
* **US-13 Split Unequally**: `REMOVED` trust your friends.
* **US-14 Expense history**: Added endpoint for expense history in the group.

## 🏗 Phase 5: Optimisation
Optimisation of payment/debt:
* **US-15 View personal balance in the group**: Implementing logic for personal balance in the group.
* **US-16 Payment/debt settling**: Implemented payment/debt settling process

## 🏗 Phase 6: DevOps
Optimisation of payment/debt:
* **GlobalExceptionHandler**: already implemented.
* **US-17 Kafka**: IN PROGRESS
* **US-18 CI/CD**: IN PROGRESS

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
6. **Limiter in creating groups**:
   - We should be able to create groups with the same name, but if we do it every second it's bad for the app. So I added limiter.
7. **Big Decimal**:
   - Used BigDecimal for all monetary values instead of double or float. This prevents binary floating-point rounding errors, ensuring that every cent is accounted for and balances remain exact during complex split calculations.
8. **Kafka for payment/debt processing**: 
   - provides a durable, persistent event log that allows us to replay transactions for auditing and ensures high reliability in a decoupled event-driven architecture.
9. **Removed Unequal split**: 
   - real friendship cost more than money.
10. **Redis**:
   - Using Redis for refresh tokens is the right choice because its built-in TTL (Time-To-Live) mechanism and high-speed in-memory storage perfectly handle the ephemeral nature of session tokens while ensuring minimal latency during authentication.


---

## 🧪 Testing Strategy
Code quality is enforced through a rigorous unit testing suite:
* **`AuthServiceDefaultTest`**: Full coverage of registration and login business logic using **Mockito**.
* **`JwtUtilsTest`**: Verification of the token lifecycle (generation/validation) using **`ReflectionTestUtils`** to simulate the Spring environment without the overhead of a full application context.
* **`AuthControllerTest`**: Full coverage of registration and login endpoints using **WebMvcTest**.
* **`UserControllerTest`**: Full coverage of get user endpoints using **WebMvcTest**.
* **`UserServiceDefaultTest`**: Full coverage of getting user with different ways business logic using **Mockito**.
* **`GroupServiceDefaultTest`**: Full coverage of create/delete/get group using **Mockito**.
* **`FriendServiceDefaultTest`**: Full coverage of add/remove/get friend using **Mockito**.
* **`FriendControllerTest`**: Full coverage of add/remove/get friend endpoints using **WebMvcTest**.
* **`GroupControllerTest`**: Full coverage of create/delete/get group using **WebMvcTest**.
* **`GroupMemberServiceDefaultTest`**: Full coverage of add/remove/get member using **Mockito**.
* **`GroupMemberControllerTest`**: Full coverage of add/remove/get member endpoints using **WebMvcTest**.
* **`ExpenseControllerTest`**: Full coverage of add/remove/get expense endpoints using **WebMvcTest**.
* **`ExpenseServiceDefaultTest`**: Full coverage of add/remove/get expense using **Mockito**.
* **`PaymentControllerTest`**: Full coverage of add/remove/get/approve/decline payment endpoints using **WebMvcTest**.
* **`PaymentServiceDefaultTest`**: Full coverage of add/remove/get/approve/decline payment using **Mockito**.

---

## 🗺 Roadmap
- [x] **Phase 0: Containerization** (Liquibase, Docker compose)
- [x] **Phase 1: Foundation** (Auth, JWT, Security Config)
- [x] **Phase 2: Social** (User Profiles, Search, Friend System)
- [x] **Phase 3: Core Logic** (Groups Management, Participant Roles)
- [x] **Phase 4: Finance** (Expense Tracking, Equal/Unequal Split Algorithms)
- [x] **Phase 5: Optimization** (Debt Settlement Algorithm - Minimizing Transactions)
- [`IN PROGRESS`] **Phase 6: DevOps** (Kafka, Dockerization, CI/CD, Global Exception Handling)

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
4. I don't know if I will be able to finish this project, but I'll do my best.
5. So enjoy and don't let people use you :) (for free)
