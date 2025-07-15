# PaymentNotificationAvro

A sample microservices project demonstrating Avro-based messaging between a payment service and a notification service using Kafka and Schema Registry.

## Project Structure

- `payment-service/` - Handles payment processing and produces Avro messages to Kafka
- `notification-service/` - Consumes Avro messages and simulates notification delivery
- `infra-docker-compose.yml` - Infrastructure (Kafka, Zookeeper, Schema Registry)
- `services-docker-compose.yml` - Services (payment-service, notification-service)

## Running the Project

1. **Start Infrastructure (Kafka, Zookeeper, Schema Registry):**
   ```powershell
   docker-compose -f infra-docker-compose.yml up --build
   ```

2. **Start Services:**
   ```powershell
   docker-compose -f services-docker-compose.yml up --build
   ```

3. **Check Services:**
   ```powershell
   docker-compose ps
   ```
   - `payment-service` runs on port **8080**
   - `notification-service` runs on port **8082**