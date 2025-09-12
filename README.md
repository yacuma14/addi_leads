 # CRM Leads Demo 

Project dependency, using **Spring Boot**, **Kafka**, **H2**, 
to implement lead validation and convert them into prospects.

---

## 1. Saga pattern (Orchestration)

The **Saga Pattern** is used to handle **distributed transactions** in microservices-based systems.
Since multiple services can be used, atomicity can be simulated.


### **WHY**

In this project, the business flow consists of several steps:

1. Validate the customer's identity.
2. Perform a criminal background check.
3. Run a credit score.
4. Approve or reject the lead.

Since each step is executed in **different microservices (Stubs)**, the states that apply to the lead must be orchestrated.

### **Benefits**

- Allows **decoupling of services**, with each responsible for its own state.
- Robust error handling:
- If the error validation fails, the event is published and the lead is marked as **REJECTED**, without affecting other services.
---

## 2. Hexagonal Architecture (Ports & Adapters)

The **Hexagonal Architecture** separates **business logic** from **infrastructure code** (databases, REST APIs, etc.)
through the use of **ports** and **specific implementations (adapters)**.

### **WHY**
- Maintain the **business domain independent** of external frameworks and technologies.

### **Components in this project**

- **Ports (`EventPublisherPort`)** → Interfaces that define contracts for publishing events.
- **Adapters** → Concrete implementations, such as `KafkaEventPublisher`.
- **Domain (`Lead`, `SagaState`)** → Contains pure business logic.
- **Application (`OrchestratorService`)** → Coordinates use cases and orchestrates the workflow.
---

## 3. Event-Driven Architecture (EDA)

It is based on **asynchronous communication** between microservices using **events**.
Each service **publishes events** when something relevant happens, and **other services subscribe** to them to react.

### **WHY**

- The workflow steps (identity, criminal, scoring) should not be **directly coupled** to each other.
- With events:
- Each service **listens only to the events it needs**.

## **Prerequisites**
- **Java 17** 
- **Maven 3.9+**
- **Docker** and **Docker Compose**
- **IntelliJ IDEA** (Community o Ultimate)

---

## **1. Clone or export proyecto**
- En IntelliJ:  
  `File > New > Project from Existing Sources` 

---

## **2. Set up Lombok en IntelliJ**
1. `File > Settings > Plugins > Marketplace` 
2. `File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors`  
   → activate **Enable annotation processing**.

---

## **3. Run Kafka and Zookeeper**
At the root of the project:
bash
docker compose up -d

## **4. Monitor events inKafka**
Kafka UI: http://localhost:8081

## **5. Consola H2**
H2 Console: http://localhost:8080/h2-console
