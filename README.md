# Inventory Management Service

This module implements the inventory microservice for the order-management system.

## 1. Architecture Overview

The inventory service is responsible for managing stock information for products, validating stock availability, and applying stock deduction requests from the order service. It acts as the source of truth for inventory data in the distributed system.

### Core responsibilities
- Store product inventory records by SKU
- Track available quantity and low-stock state
- Handle stock deduction requests safely
- Emit warning logs when stock falls below a configured threshold
- Expose REST endpoints for inventory queries and updates

## 2. High-Level Component Diagram

```mermaid
flowchart LR
    Client[Order Service / API Gateway / Client] -->|HTTP| API[Inventory Controller]
    API --> Service[Inventory Service]
    Service --> Repo[Inventory Repository]
    Service --> DB[(Inventory Database)]
    Service --> Logs[Warning Logs / Monitoring]
```

## 3. Detailed Internal Architecture

```mermaid
flowchart TD
    A[Incoming Request] --> B[Controller Layer]
    B --> C[Validation Layer]
    C --> D[Inventory Service]
    D --> E[Inventory Domain Model]
    D --> F[Repository / JPA]
    F --> G[(Database)]
    D --> H[Low-Stock Rule Engine]
    H --> I[Warning Logger]
    D --> J[Transaction Manager]
    J --> G
```

## 4. End-to-End Request Flow (Request to Response)

```mermaid
sequenceDiagram
    participant C as Client / Order Service
    participant CT as Controller
    participant S as Inventory Service
    participant R as Repository / JPA
    participant DB as Inventory Database
    participant L as Logger / Monitoring

    C->>CT: HTTP request (create / read / deduct)
    CT->>S: Pass request payload
    S->>S: Validate input and business rules
    S->>R: Read or lock inventory record
    R->>DB: Query / update stock data
    DB-->>R: Return persisted inventory state
    R-->>S: Return entity data
    S->>S: Apply low-stock threshold logic
    S->>L: Emit warning log if threshold breached
    S-->>CT: Return response DTO / entity
    CT-->>C: HTTP response with status and payload
```

### Detailed flow for stock deduction

```mermaid
flowchart TD
    A[Incoming stock deduction request] --> B[InventoryController]
    B --> C[InventoryService.deductStock]
    C --> D{Validate quantity > 0}
    D -- No --> E[Bad Request Error]
    D -- Yes --> F[Load inventory record by SKU]
    F --> G{Sufficient stock available?}
    G -- No --> H[InsufficientInventoryException]
    G -- Yes --> I[Lock row / begin transaction]
    I --> J[Decrease available quantity]
    J --> K[Evaluate low-stock threshold]
    K --> L{Below threshold?}
    L -- Yes --> M[Log warning]
    L -- No --> N[No warning]
    M --> O[Commit transaction]
    N --> O
    O --> P[Return updated inventory item]
    P --> Q[HTTP 200 OK response]
```

## 5. Main Components

### Controller Layer
- Handles create, read, and deduct endpoints
- Provides a clean REST interface for other services

### Service Layer
- Implements business logic for stock updates
- Ensures inventory is not deducted below zero
- Applies low-stock threshold evaluation

### Repository Layer
- Stores inventory records in a relational database
- Uses transactional database operations for consistency

### Domain Model
- Inventory item contains:
  - SKU
  - product name
  - available quantity
  - low-stock flag
  - timestamps

## 6. Deployment Architecture

```mermaid
flowchart LR
    User[Client] --> LB[Ingress / Load Balancer]
    LB --> POD1[Inventory Pod]
    LB --> POD2[Inventory Pod]
    POD1 --> DB[(H2 / MySQL / PostgreSQL)]
    POD2 --> DB
```

## 7. Design Considerations

- Horizontal scaling with multiple replicas
- Transactional updates for consistency
- Low-stock threshold controlled through configuration
- Containerization with Docker and Kubernetes
- Future readiness for API gateway and service discovery

## 8. Current Implementation Notes

- Built with Spring Boot and Spring Data JPA
- Default local profile uses H2 in-memory database
- Low-stock threshold is configurable via application configuration
- Kubernetes ConfigMap is included for deployment-time property injection

## 9. API Endpoints

- POST /api/inventory
- GET /api/inventory/{sku}
- POST /api/inventory/deduct

## 10. Run and Deploy

### Run locally
```bash
./mvnw spring-boot:run
```

### Build image
```bash
docker build -t inventory-service:latest .
```

### Deploy to Kubernetes
```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```
