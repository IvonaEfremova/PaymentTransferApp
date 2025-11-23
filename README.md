# Payment Transfer Service Demo

## Project Overview

This project is a **Basic Demo Payment Transfer Service**. It simulates a simple digital banking platform feature,
allowing users to securely transfer funds between accounts. The focus of this project is on **robust functionality**, *
*high code quality**, and **clear auditability**.

Key highlights:

- Secure fund transfers between accounts.
- Idempotency handling to prevent duplicate transactions.
- Detailed balance audit records for each transaction.
- Complete transaction history tracking.

---

## Features

- **Initiate Transfers:** Users can transfer funds between accounts by providing source and destination account IDs and
  the transfer amount.
- **Sufficient Funds Validation:** Transfers are only processed if the source account has enough balance.
- **Error Handling:** Comprehensive error handling ensures reliability and prevents inconsistencies.
- **Transaction Auditing:** Each successful transfer is recorded in the audit logs for tracking and compliance.
- **Idempotency:** Prevents duplicate transactions when the same request is submitted multiple times.

---

## Technology Stack

- **Language:** Java 21
- **Framework:** Spring Boot
- **Database:** PostgreSQL (with Liquibase for schema migrations)
- **Build Tool:** Maven
- **Testing:** JUnit & Mockito
- **Documentation:** Swagger UI

---

## Prerequisites

- **Java 21**
- **PostgreSQL** (can run locally or via Docker Compose)

---

## Setup and Installation

### 1. Clone the Repository

```bash
git clone https://github.com/IvonaEfremova/PaymentTransferApp.git
cd payment-transfer-service
```

### 2. Start PostgreSQL using Docker Compose

```bash
docker-compose up -d
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

### API Documentation

After starting the application, access Swagger UI for API testing and documentation:

http://localhost:8080/swagger-ui.html

### Improvements

- Security: Add authentication & authorization (e.g., JWT or OAuth2) to protect transfer endpoints. Each user should be
  able to see and modify only his balances.
- Rate Limiting: Prevent abuse by limiting requests per user.
- Notifications: Send email or push notifications for successful or failed transfers.
- Anti-fraud limitations: daily limitation, transfer limitation

### Project Structure

- domain – Entity classes for Account, Transaction, BalanceAudit, IdempotencyKey
- repository – Spring Data repositories
- service – Core business logic
- controller – REST controllers for exposing APIs
- dto – Data Transfer Objects
- errors – Custom exceptions


