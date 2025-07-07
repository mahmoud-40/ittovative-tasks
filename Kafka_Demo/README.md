# Kafka Demo with Avro Schema Registry

A complete Apache Kafka messaging system with Avro serialization and Schema Registry integration.

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    Producer     │───▶│     Kafka       │───▶│    Consumer     │
│  (Java App)     │    │   + Zookeeper   │    │  (Java App)     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │ Schema Registry │
                       └─────────────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   Kafka UI      │
                       │ (Web Interface) │
                       └─────────────────┘
```

## 🚀 Quick Start

### 1. Start Docker Services

```bash
# Start all services (Zookeeper, Kafka, Schema Registry, Kafka UI)
docker-compose up -d

# Check services are running
docker-compose ps
```

### 2. Run the Producer

```bash
# Navigate to producer directory
cd producer

# Compile and copy dependencies
mvn compile
mvn dependency:copy-dependencies

# Run producer
java -cp "target/classes;target/dependency/*" com.mahmoud.producer.ProducerApplication
```

### 3. Run the Consumer

```bash
# Navigate to consumer directory (in new terminal)
cd consumer

# Compile and copy dependencies
mvn compile
mvn dependency:copy-dependencies

# Run consumer
java -cp "target/classes;target/dependency/*" com.mahmoud.consumer.ConsumerApplication

# When prompted, press 1 to start consuming
```

## 📊 Avro Schema

The system uses a User schema defined in `schema/user.avsc`:

```json
{
  "type": "record",
  "name": "User",
  "namespace": "com.mahmoud.avro",
  "fields": [
    { "name": "id", "type": "int" },
    { "name": "name", "type": "string" },
    { "name": "email", "type": "string" }
  ]
}
```

## 🔄 Message Flow

1. **Producer Application**:
   - Creates User objects using Avro builder pattern
   - Serializes to Avro binary format
   - Registers schema with Schema Registry
   - Publishes to `users-avro` topic

2. **Schema Registry**:
   - Stores and validates schemas
   - Provides schema evolution capabilities
   - Ensures compatibility between producers/consumers

3. **Consumer Application**:
   - Subscribes to `users-avro` topic
   - Retrieves schema from Schema Registry
   - Deserializes Avro messages back to User objects
   - Displays structured data
