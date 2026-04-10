# Synapse Data Platform Based On Pricing Engine

Synapse Data Platform is a highly secure, fine-grained data trading and consent management platform. It bridges the gap between Data Owners (e.g., healthcare providers, users) and Data Consumers (e.g., pharmaceutical companies, research institutions).

The platform features an advanced **Dynamic Pricing Engine** supporting per-field pricing, sensitive data multipliers, purpose-based discounts, and bulk-request tiering, paired with strict **Role-Based Access Control (RBAC)** and **Granular Consent Rules**.

##  Project Structure

The repository is divided into two main parts:
- `/frontend/dataMarketFrondEnd`: The frontend web application built with React, TypeScript, Vite, and TailwindCSS.
- `/backend`: The RESTful API backend service built with Java, Spring Boot, and MyBatis-Plus.

---

## Prerequisites

Make sure you have the following installed on your machine:
- **Node.js** (v16.14+ recommended)
- **pnpm** (Package manager used in the frontend)
- **Java Development Kit (JDK)** (Version 11 or 17 recommended)
- **Maven** (For backend dependency management)
- **MySQL / PostgreSQL** (Depending on your configured JDBC driver)

---

## Installation & Running

### 1. Backend Setup (Spring Boot)

1. Open a terminal and navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Configure the Database (AWS RDS Hosted):
   - The project connects to a cloud-hosted database by default. You do NOT need to set up a local database server to run the application.
   - **Host:** \`datamarket-shared.cfgcuc02as7i.eu-north-1.rds.amazonaws.com\`
   - **Port:** \`3306\`
   - **Database:** \`datamarket\`
   - **Username:** \`admin\`
   - **Password:** \`Zhm1015.\`
   - If you ever need to point it to a different local or cloud database, locate the \`src/main/resources/application.yml\` (or \`.properties\`) file and update the configurations.

3. Install dependencies and run the application:
   ```bash
   mvn clean install
   ```
   ```bash
   mvn spring-boot:run
   ```
   *The backend server will typically start on `http://localhost:8080`.*

### 2. Frontend Setup (React + Vite)

1. Open a new terminal window and navigate to the frontend directory:
   ```bash
   cd frontend/dataMarketFrondEnd
   ```

2. Install the necessary dependencies using `pnpm`:
   ```bash
   pnpm install
   ```

3. Start the development server:
   ```bash
   pnpm run dev
   ```
   *The frontend application will be available at `http://localhost:5173`.*

---

## Core Features

1. **Dataset Schema Definition:** Owners can define complex schemas including `sensitive: true` flags implicitly via JSON upload.
2. **Pricing Configuration:** Setup Base Access Fee, Per-field cost, Sensitive Multipliers, Purpose-based modifications, and Bulk Discounts.
3. **Consent Management:** Owners can whitelist/blacklist specific datasets fields based on roles (e.g., University) and purposes (e.g., Medical Research).
4. **Billing Calculation:** Consumers request access, and the backend engine automatically runs the complex pricing formulation based on selected fields and schemas.

---

## Technology Stack

**Frontend:** React, TypeScript, Vite, TailwindCSS, Lucide-React, Axios
**Backend:** Java, Spring Boot, MyBatis-Plus, Jackson (for JSON TypeHandling), Spring Security, Jwt