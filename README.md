# 📍 Courier Tracking REST API

This is a Spring Boot-based RESTful web application designed to:

- Stream and log **courier geolocations**.
- Calculate and return **total distance traveled** per courier.
- Log **courier entrance** to Migros stores if within a **100-meter** radius (excluding re-entries within 1 minute).
- Utilize key **software design patterns** and maintain clean architecture.

---

## 🧩 Features

- 📡 **Location Logging**: Accept courier location (lat/lng, time).
- 📏 **Distance Calculation**: Haversine algorithm for real-world accuracy.
- 🏪 **Store Proximity Detection**: Detects if a courier is near a Migros store.
- 🧠 **Debounce Logic**: Re-entry within 1 minute is ignored.
- 🎯 **Two Design Patterns**:
  - **Strategy Pattern**: `DistanceCalculator` interface with `HaversineDistanceCalculator` implementation.
  - **Observer Pattern**: Used to automatically update courier's total distance when a new location is recorded:
    - **Subject**: `CourierLocationEntity` with JPA entity lifecycle hooks
    - **Publisher**: `CourierLocationEventPublisher` triggers on `@PrePersist`
    - **Event**: `CourierLocationEvent` contains the location entity
    - **Observer**: `CourierLocationEventListener` processes events and updates distances

---

## 🗂️ Tech Stack

- Java 17+
- Spring Boot 3+
- Maven
- H2 In-Memory Database
- Lombok
- JPA/Hibernate
- SLF4J Logging

---

## 🛠️ Prerequisites

Ensure you have the following installed:

- ✅ Java 17+
- ✅ Maven 3.8+
- ✅ Git (optional)

---

## 🚀 Getting Started

### 📁 Clone the Repository

```bash
git clone https://github.com/mburakaltun/courier-tracking.git
cd courier-tracking
```

### 🛠️ Build the Project

```bash
mvn clean install
```

### 🏃‍♂️ Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

## 📜 API Documentation

Log Courier Location

POST `/api/couriers/log-courier-location`

```json
{
  "courierId": 1,
  "latitude": 40.9923307,
  "longitude": 29.1244229,
  "recordedAt": "2025-07-29T20:00:00"
}
```

Using curl:

```bash
curl -X POST http://localhost:8080/couriers/log-courier-location \
  -H "Content-Type: application/json" \
  -d '{"courierId":1,"latitude":40.9923307,"longitude":29.1244229,"recordedAt":"2025-07-29T20:00:00"}'
```

Query Total Distance

GET `/api/couriers/query-total-distance?courierId=1`

Sample Response:

```json
{
  "totalDistanceInMeters": 150.45
}
```

Using curl:

```bash
curl -X GET "http://localhost:8080/couriers/query-total-distance?courierId=1"
```

## 🗃️ Preloaded Data

Upon application startup, the following Migros store locations are preloaded:

5 Migros stores are automatically loaded from stores.json equivalent.
1 default courier is inserted for quick testing (ID: 1).

## 🧪 H2 Database Console

You can access the H2 database console at:

```
http://localhost:8080/h2-console
```

## 🧾 Sample Configuration (application.yml)

```yaml
spring:
  application:
    name: courier-tracking
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true

  h2:
    console:
      enabled: true


app:
  earth-radius-in-kilometers: 6371
  distance-threshold-in-meters: 100
  time-threshold-in-seconds: 60
```

## 📄 License

```yaml
✅ MIT License — feel free to use, modify and share.
```

