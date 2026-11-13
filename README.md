# Microservicio CRUD de Inventario (HU-01) — UTCH

Microservicio RESTful desarrollado con **Java 21** y **Spring Boot** conectado a **MongoDB**, con manejo global de excepciones personalizadas, validaciones de entrada, pruebas unitarias y de integración (**JUnit 5 + Mockito + MockMvc**), reporte de cobertura con **JaCoCo (100%)** y análisis estático con **SonarQube**.

---

## Stack Tecnológico
* **Lenguaje:** Java 21 (OpenJDK Temurin LTS)
* **Framework:** Spring Boot (`Web`, `Data MongoDB`, `Validation`)
* **Base de Datos NoSQL:** MongoDB 7.0 (vía Docker Compose)
* **Documentación Interactiva:** SpringDoc OpenAPI / Swagger UI
* **Pruebas y Calidad:** JUnit 5, Mockito, MockMvc, JaCoCo (`0.8.11`), SonarQube Community

---

## Estructura del Proyecto

```text
src/
├── main/
│   ├── java/com/utch/devqa/
│   │   ├── controller/ProductoController.java           # Endpoints REST (/api/productos)
│   │   ├── dto/ProductoRequest.java                     # DTO de entrada con validaciones
│   │   ├── dto/ErrorRespuesta.java                      # Formato JSON de errores
│   │   ├── exception/RecursoNoEncontradoException.java  # Excepción 404
│   │   ├── exception/DatoDuplicadoException.java        # Excepción 409
│   │   ├── exception/ManejadorGlobalExcepciones.java    # @RestControllerAdvice global
│   │   ├── model/Producto.java                          # Documento MongoDB (@Document)
│   │   ├── repository/ProductoRepository.java           # MongoRepository
│   │   └── service/ProductoService.java                 # Lógica de negocio CRUD
│   └── resources/
│       ├── application.properties                       # Perfil activo (dev)
│       └── application-dev.properties                   # Configuración MongoDB y puerto
└── test/
    └── java/com/utch/devqa/
        ├── controller/ProductoControllerIntegrationTest.java # Pruebas de integración web
        └── service/ProductoServiceTest.java                  # Pruebas unitarias con Mockito
```

---

## Instrucciones de Ejecución

### 1. Levantar MongoDB y SonarQube con Docker
En la raíz del proyecto ejecuta:
```powershell
docker compose up -d
```
* **MongoDB:** `mongodb://localhost:27017/inventario_db`
* **Panel de SonarQube:** `http://localhost:9000` (credenciales iniciales: `admin` / `admin`)

### 2. Ejecutar Pruebas y Reporte de Cobertura (JaCoCo)
```powershell
.\mvnw.cmd clean test
```
* El reporte HTML interactivo se genera en: `target/site/jacoco/index.html`

### 3. Ejecutar el Microservicio
```powershell
.\mvnw.cmd spring-boot:run
```
* **API Base:** `http://localhost:8081/api/productos`
* **Swagger UI:** `http://localhost:8081/swagger-ui/index.html`

### 4. Ejecutar Análisis Estático en SonarQube
Una vez que SonarQube esté activo en `http://localhost:9000` y hayas generado un token (o usando las credenciales locales):
```powershell
.\mvnw.cmd sonar:sonar "-Dsonar.host.url=http://localhost:9000" "-Dsonar.token=TU_TOKEN_SONAR"
```

---

## Endpoints Disponibles (`/api/productos`)

| Método | Ruta | Descripción | Códigos HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/productos` | Consultar catálogo completo | `200 OK` |
| `GET` | `/api/productos/{id}` | Buscar producto por ID | `200 OK`, `404 Not Found` |
| `POST` | `/api/productos` | Registrar nuevo producto | `201 Created`, `400 Bad Request`, `409 Conflict` |
| `PUT` | `/api/productos/{id}` | Actualizar producto existente | `200 OK`, `400 Bad Request`, `404 Not Found`, `409 Conflict` |
| `DELETE` | `/api/productos/{id}` | Eliminar producto por ID | `204 No Content`, `404 Not Found` |

### Ejemplo de Payload (`POST` / `PUT`)
```json
{
  "sku": "MON-24-FHD",
  "nombre": "Monitor LED 24 Pulgadas",
  "descripcion": "Panel IPS Full HD 75Hz",
  "precio": 2850.00,
  "stock": 15
}
```

