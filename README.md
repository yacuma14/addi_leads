 # CRM Leads Demo 

Dependencia proyecto, uso de **Spring Boot**, **Kafka**, **H2**, 
para implementar la validación de leads y convertirlos en prospectos.

---

## 1. Patrón Saga (Orchestration)

El **Patrón Saga** se utiliza para manejar **transacciones distribuidas** en sistemas basados en microservicios.  
ya que  múltiples servicios se podria simular la atomicidad

### **Por qué se usó**
En este proyecto, el flujo de negocio consta de varios pasos:

1. Validar identidad del cliente.  
2. Verificar antecedentes criminales.  
3. Ejecutar un scoring de crédito.  
4. Aprobar o rechazar el lead.

Dado que cada paso se ejecuta en **microservicios distintos(Stubs)**, se debe de orquestar los estados que se apliquen al lead.  

### **Beneficios**
- Permite **desacoplar servicios**, donde cada uno es responsable de su propio estado.
- Manejo robusto de errores:
  - Si la validación criminal falla, se publica el evento y el lead se marca como **REJECTED**, sin afectar a otros servicios.
---

## 2. Arquitectura Hexagonal (Ports & Adapters)

La **Arquitectura Hexagonal** separa la **lógica de negocio** del **código de infraestructura** (bases de datos, REST APIs, etc.) 
mediante el uso de **puertos (ports)** e **implementaciones concretas (adapters)**.

### **Por qué se usó**
- Mantener el **dominio de negocio independiente** de frameworks y tecnologías externas.  


### **Componentes en este proyecto**
- **Puertos (`EventPublisherPort`)** → Interfaces que definen contratos para publicar eventos.  
- **Adaptadores** → Implementaciones concretas, como `KafkaEventPublisher`.  
- **Dominio (`Lead`, `SagaState`)** → Contiene la lógica pura de negocio.  
- **Aplicación (`OrchestratorService`)** → Coordina los casos de uso y orquesta el flujo.

---

## 3. Event-Driven Architecture (EDA)

se basa en la **comunicación asíncrona** entre microservicios mediante **eventos**.
Cada servicio **publica eventos** cuando ocurre algo relevante y **otros servicios se suscriben** a ellos para reaccionar.

### **Por qué se usó**
- Los pasos del flujo (identidad, criminal, scoring) no deben estar **acoplados directamente** entre sí.
- Con eventos:
  - Cada servicio **escucha solo los eventos que necesita**.

## **Requisitos previos**
- **Java 17** (o superior)
- **Maven 3.9+**
- **Docker** y **Docker Compose**
- **IntelliJ IDEA** (Community o Ultimate)

---

## **1. Clonar o importar el proyecto**
- En IntelliJ:  
  `File > New > Project from Existing Sources` → Selecciona la carpeta del proyecto.

---

## **2. Configurar Lombok en IntelliJ**
1. `File > Settings > Plugins > Marketplace` → busca **Lombok** → instalar.
2. `File > Settings > Build, Execution, Deployment > Compiler > Annotation Processors`  
   → activa **Enable annotation processing**.

---

## **3. Levantar Kafka y Zookeeper**
En la raíz del proyecto:
bash
docker compose up -d

## **4. Monitorear eventos en Kafka**
Entra a Kafka UI: http://localhost:8081

## **5. Consola H2**
Entra a H2 Console: http://localhost:8080/h2-console
