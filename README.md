# Inventory Management Service

This module implements the inventory microservice for the order-management system.

## Features
- Create and query inventory items
- Deduct stock atomically with validation
- Low-stock warning logging when the threshold is breached
- Configurable threshold through a Kubernetes ConfigMap
- Docker image and Kubernetes manifests included

## Endpoints
- POST /api/inventory
- GET /api/inventory/{sku}
- POST /api/inventory/deduct

## Run locally
```bash
./mvnw spring-boot:run
```

## Build image
```bash
docker build -t inventory-service:latest .
```

## Deploy to Kubernetes
```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```
