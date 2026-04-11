# Prices Service

Servicio REST que consulta el precio aplicable de un producto en un e-commerce, resolviendo solapamientos de tarifas por prioridad. Construido con **Spring Boot 3** y **Java 21**.

---

## Índice

1. [Cómo probarlo en 2 minutos](#cómo-probarlo-en-2-minutos)
2. [El problema que resuelve](#el-problema-que-resuelve)
3. [Arquitectura](#arquitectura)
4. [Por qué tomé cada decisión](#por-qué-tomé-cada-decisión)
5. [Estructura del proyecto](#estructura-del-proyecto)
6. [API REST](#api-rest)
7. [Cómo ejecutar](#cómo-ejecutar)
8. [Tests](#tests)
9. [Stack tecnológico](#stack-tecnológico)

---

## Cómo probarlo en 2 minutos

### 1. Ejecutar los tests

```bash
./mvnw test
```

Deberían pasar los **28 tests** sin fallos:

```
 2 → PriceServiceTest                 (unitario  — caso de uso)
 1 → PriceNotFoundExceptionTest        (unitario  — dominio)
14 → PriceControllerIntegrationTest   (integración — 5 escenarios + 9 errores)
10 → PriceJpaRepositoryTest            (integración — query de prioridad y bordes)
 1 → PricesServiceApplicationTests     (integración — carga de contexto)
```

### 2. Arrancar la aplicación

```bash
./mvnw spring-boot:run
```

Al arrancar, la base de datos H2 en memoria se crea y carga automáticamente con los datos del enunciado. No hace falta configurar nada.

### 3. Probar desde Swagger UI

Abrir en el navegador:

```
http://localhost:8080/swagger-ui.html
```

Desde ahí se puede ejecutar el endpoint directamente con el formulario interactivo.

### 4. Verificar los 5 escenarios del enunciado con curl

```bash
# Test 1: 14/06 10:00 → tarifa 1, precio 35.50
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1"

# Test 2: 14/06 16:00 → tarifa 2, precio 25.45
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"

# Test 3: 14/06 21:00 → tarifa 1, precio 35.50
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T21:00:00&productId=35455&brandId=1"

# Test 4: 15/06 10:00 → tarifa 3, precio 30.50
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-15T10:00:00&productId=35455&brandId=1"

# Test 5: 16/06 21:00 → tarifa 4, precio 38.95
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-16T21:00:00&productId=35455&brandId=1"
```

---

## El problema que resuelve

En un e-commerce, un mismo producto puede tener varias tarifas vigentes al mismo tiempo (por ejemplo, un precio base y una promoción puntual). Este servicio recibe una fecha, un producto y una cadena (brand), y devuelve **el único precio que aplica** en ese momento.

La regla es simple: si hay varias tarifas que solapan en fechas, **gana la de mayor prioridad** (el número más alto).

### Datos de ejemplo (cadena ZARA, producto 35455)

| # | Inicio             | Fin                 | Tarifa | Prioridad | Precio  |
|---|--------------------|---------------------|--------|-----------|---------|
| 1 | 2020-06-14 00:00   | 2020-12-31 23:59:59 | 1      | 0         | 35.50 € |
| 2 | 2020-06-14 15:00   | 2020-06-14 18:30    | 2      | 1         | 25.45 € |
| 3 | 2020-06-15 00:00   | 2020-06-15 11:00    | 3      | 1         | 30.50 € |
| 4 | 2020-06-15 16:00   | 2020-12-31 23:59:59 | 4      | 1         | 38.95 € |

Por ejemplo, el 14 de junio a las 16:00 aplican las tarifas 1 y 2 a la vez. Como la tarifa 2 tiene prioridad 1 (mayor que 0), el servicio devuelve 25.45 €.

---

## Arquitectura

Seguí **Arquitectura Hexagonal** (Puertos y Adaptadores). La idea es que el núcleo de negocio (dominio) no dependa de ningún framework ni tecnología externa. Las dependencias siempre apuntan hacia adentro:

```
┌─────────────────────────────────────────────────┐
│                 INFRAESTRUCTURA                 │
│                                                 │
│  ┌──────────────┐         ┌───────────────────┐ │
│  │ Adaptador    │         │ Adaptador         │ │
│  │ REST (API)   │         │ Persistencia (BD) │ │
│  └──────┬───────┘         └────────┬──────────┘ │
│         │                          │             │
│    Puerto IN                  Puerto OUT         │
│         │                          │             │
│  ┌──────▼──────────────────────────▼──────────┐ │
│  │              APLICACIÓN                    │ │
│  │           (PriceService)                   │ │
│  └──────────────────────┬─────────────────────┘ │
│                         │                        │
│  ┌──────────────────────▼─────────────────────┐ │
│  │                 DOMINIO                    │ │
│  │   Price · PriceQuery · Ports · Exception   │ │
│  └────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────┘
```

### Cómo fluye una petición

```
GET /api/v1/prices
       │
       ▼
MdcRequestFilter             asigna un ID único a la petición para los logs
       │
       ▼
PriceController              valida los parámetros de entrada
       │
       ▼
GetApplicablePriceUseCase    interfaz del caso de uso (puerto de entrada)
       │
       ▼
PriceService                 ejecuta la lógica de negocio
       │
       ▼
PriceRepositoryPort          interfaz de acceso a datos (puerto de salida)
       │
       ▼
PricePersistenceAdapter      traduce a JPA y consulta la BD
       │
       ▼
H2 (base de datos)           resuelve la prioridad directamente en la query
```

---

## Por qué tomé cada decisión

### Beans configurados con `@Configuration`, no con `@Service`

`PriceService` y `PricePersistenceAdapter` no llevan anotaciones de Spring (`@Service`, `@Component`). Los instancio manualmente en `ApplicationConfig`:

```java
@Bean
GetApplicablePriceUseCase getApplicablePriceUseCase(PriceRepositoryPort port) {
    return new PriceService(port);
}
```

Así el dominio y la capa de aplicación quedan completamente libres de dependencias con el framework. Si quisiera portar la lógica a otro contexto (una función lambda, otro framework), no tendría que tocar nada. Spring queda confinado exclusivamente en la capa de infraestructura.

### La prioridad se resuelve en la base de datos

En vez de traer todas las tarifas que aplican a Java y filtrar en memoria, la query ya lo resuelve:

```sql
SELECT p FROM PriceEntity p
WHERE p.brandId = :brandId
  AND p.productId = :productId
  AND :applicationDate BETWEEN p.startDate AND p.endDate
ORDER BY p.priority DESC
LIMIT 1
```

Delegué la resolución a la BD porque eso es lo que mejor sabe hacer un motor de base de datos: filtrar y ordenar. La alternativa traería datos innecesarios por la red y consumiría memoria en la aplicación sin aportar nada.

### Validación en la frontera, no en el dominio

Usé Bean Validation (`@Positive`, `@NotNull`) directamente en el controller:

```java
@RequestParam @Positive Long productId,
@RequestParam @Positive Long brandId,
@RequestParam @NotNull LocalDateTime applicationDate
```

La validación pertenece a la frontera del sistema (el adaptador REST). Si un parámetro es inválido, el `GlobalExceptionHandler` lo captura y devuelve un error 400 claro. De esta forma, el dominio siempre recibe datos ya saneados y no necesita repetir validaciones.

### `Optional` en vez de lanzar excepciones desde el servicio

`PriceService` devuelve `Optional<Price>`. Un resultado vacío es perfectamente válido desde el punto de vista de negocio: simplemente no hay precio para esa combinación.

La decisión de convertirlo en un HTTP 404 la toma el controller, que lanza `PriceNotFoundException`. Y el `GlobalExceptionHandler` lo traduce a la respuesta correspondiente. Así el dominio no sabe nada sobre códigos HTTP.

### Manejo centralizado de errores

`GlobalExceptionHandler` captura todas las excepciones previsibles (parámetro faltante, tipo incorrecto, validación fallida, precio no encontrado) y las convierte en respuestas JSON estandarizadas con código, mensaje y timestamp. También incluye un fallback para errores inesperados que devuelve un 500 genérico sin exponer información interna del sistema.

### Correlación de peticiones con X-Request-ID

Cada petición recibe un identificador único (UUID) a través de `MdcRequestFilter`. Ese ID se incluye en todos los logs durante la vida de la petición y se devuelve al cliente en el header `X-Request-ID`:

```
2026-04-11 17:00:00.000 [main] INFO [a1b2c3d4-...] PriceController - Incoming price request: ...
```

En producción esto es muy valioso: si un usuario reporta un problema, con ese ID puedo filtrar todos los logs de su petición específica.

### Modelo de dominio separado de la entidad JPA

`Price` (dominio) y `PriceEntity` (infraestructura) son clases distintas conectadas por un mapper. El dominio es inmutable; la entidad JPA necesita ser mutable por requisito del framework.

Si ambos fueran la misma clase, el modelo de negocio estaría acoplado a las reglas de JPA (constructor vacío obligatorio, campos mutables, anotaciones de persistencia). El mapper actúa como cortafuegos entre ambas representaciones.

### Mappers manuales en lugar de MapStruct

Para dos mappers simples (JPA → Dominio y Dominio → DTO), MapStruct añade complejidad de configuración sin beneficio proporcional. En un proyecto más grande con decenas de entidades sí lo incluiría.

### Perfil de test separado

La configuración base (`application.yml`) define el datasource H2 y la inicialización con `schema.sql` + `data.sql`. El perfil test (`application-test.yml`) solo sobreescribe lo necesario para debugging: `show-sql: true` y logging más detallado.

Esto permite que la aplicación arranque correctamente tanto con `./mvnw spring-boot:run` como durante los tests, sin duplicar configuración.

### Esquema SQL explícito en vez de generación automática

Usé `ddl-auto: none` con scripts `schema.sql` y `data.sql` en vez de dejar que Hibernate genere las tablas solo. En producción usaría Flyway o Liquibase para las migraciones, y este enfoque es mucho más cercano a esa práctica que confiar en la generación automática.

---

## Estructura del proyecto

```
src/main/java/com/bcnc/pricing/
├── domain/                            Núcleo de negocio (sin dependencias externas)
│   ├── model/
│   │   ├── Price.java                    Entidad de dominio (inmutable)
│   │   ├── PriceQuery.java               Parámetros de consulta
│   │   └── PriceNotFoundException.java   Excepción de negocio
│   └── port/
│       ├── in/
│       │   └── GetApplicablePriceUseCase.java   Puerto de entrada
│       └── out/
│           └── PriceRepositoryPort.java          Puerto de salida
│
├── application/                       Casos de uso
│   └── service/
│       └── PriceService.java             Implementa el caso de uso (sin anotaciones Spring)
│
└── infrastructure/                    Todo lo que depende de tecnología
    ├── adapter/
    │   ├── in/rest/
    │   │   ├── PriceController.java       Controlador REST
    │   │   ├── MdcRequestFilter.java      Filtro de correlación X-Request-ID
    │   │   ├── PriceResponse.java         DTO de respuesta
    │   │   ├── PriceResponseMapper.java   Mapper Dominio → DTO
    │   │   ├── ErrorResponse.java         DTO de error estandarizado
    │   │   └── GlobalExceptionHandler.java  Manejo centralizado de excepciones
    │   └── out/persistence/
    │       ├── PriceEntity.java           Entidad JPA
    │       ├── PriceJpaRepository.java    Repositorio con query de prioridad
    │       ├── PriceEntityMapper.java     Mapper JPA → Dominio
    │       └── PricePersistenceAdapter.java  Implementa PriceRepositoryPort
    └── config/
        ├── ApplicationConfig.java         Wiring manual de beans
        └── OpenApiConfig.java             Configuración Swagger/OpenAPI

src/main/resources/
├── application.yml       Configuración de la aplicación (datasource H2, inicialización SQL)
├── schema.sql            DDL de la tabla PRICES
└── data.sql              Datos del enunciado

src/test/resources/
└── application-test.yml  Overrides para tests (show-sql, logging detallado)
```

---

## API REST

### Consultar precio aplicable

```
GET /api/v1/prices
```

Se envían tres parámetros por query string:

| Parámetro         | Tipo          | Obligatorio | Validación     | Ejemplo               |
|-------------------|---------------|-------------|----------------|------------------------|
| `applicationDate` | ISO date-time | Sí          | Formato válido | `2020-06-14T10:00:00` |
| `productId`       | número        | Sí          | Mayor que 0    | `35455`               |
| `brandId`         | número        | Sí          | Mayor que 0    | `1`                   |

**Ejemplo de respuesta exitosa (200):**

```json
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 1,
  "startDate": "2020-06-14T00:00:00",
  "endDate": "2020-12-31T23:59:59",
  "price": 35.50,
  "curr": "EUR"
}
```

**Posibles errores:**

| Código | Cuándo ocurre                                        |
|--------|------------------------------------------------------|
| 400    | Falta un parámetro, el formato es inválido o el valor es ≤ 0 |
| 404    | No existe precio aplicable para esa combinación      |
| 500    | Error inesperado del servidor                        |

**Ejemplo de error:**

```json
{
  "status": 404,
  "message": "No applicable price found for brandId=1 and productId=99999",
  "timestamp": "2026-04-11T12:00:00"
}
```

**Header de correlación:**

Cada respuesta incluye `X-Request-ID` con un identificador único. Si el cliente lo envía, se reutiliza; si no, el servicio genera uno nuevo. Sirve para rastrear peticiones en los logs.

### Documentación interactiva

Con la aplicación arrancada:

```
http://localhost:8080/swagger-ui.html
```

---

## Cómo ejecutar

### Requisitos

- Java 21
- Maven 3.8+ (o usar el wrapper incluido `./mvnw`)

### Arrancar la aplicación

```bash
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080`. La base de datos H2 se crea en memoria y se carga automáticamente con el esquema y los datos del enunciado.

### Consola H2 (opcional)

Si se quiere inspeccionar la base de datos directamente:

```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:pricesdb
Usuario:  SA
Password: (vacío)
```

### Ejemplos de llamadas

```bash
# Test 1: 14/06 10:00 → tarifa 1, precio 35.50
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T10:00:00&productId=35455&brandId=1"

# Test 2: 14/06 16:00 → tarifa 2, precio 25.45
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T16:00:00&productId=35455&brandId=1"

# Test 3: 14/06 21:00 → tarifa 1, precio 35.50
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T21:00:00&productId=35455&brandId=1"

# Test 4: 15/06 10:00 → tarifa 3, precio 30.50
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-15T10:00:00&productId=35455&brandId=1"

# Test 5: 16/06 21:00 → tarifa 4, precio 38.95
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-16T21:00:00&productId=35455&brandId=1"

# Error 404: producto que no existe
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T10:00:00&productId=99999&brandId=1"

# Error 400: productId negativo
curl "http://localhost:8080/api/v1/prices?applicationDate=2020-06-14T10:00:00&productId=-1&brandId=1"
```

---

## Tests

### Ejecutar todos los tests

```bash
./mvnw test
```

### Qué se testea

| Clase                              | Tipo         | Tests | Qué valida |
|------------------------------------|--------------|-------|------------|
| `PriceServiceTest`                 | Unitario     | 2     | El caso de uso con un mock del repositorio: cuando encuentra precio y cuando no |
| `PriceNotFoundExceptionTest`       | Unitario     | 1     | Que el mensaje de la excepción incluya brandId y productId |
| `PriceControllerIntegrationTest`   | Integración  | 14    | Los 5 escenarios del enunciado + 9 flujos de error (400 y 404) con la app real levantada |
| `PriceJpaRepositoryTest`           | Integración  | 10    | Que la query de prioridad funcione correctamente en todos los casos extremos |
| `PricesServiceApplicationTests`    | Integración  | 1     | Que el contexto de Spring arranca sin errores |

**Total: 28 tests, 0 fallos**

### Escenarios de error cubiertos en el controller

| Caso                             | Respuesta esperada |
|----------------------------------|--------------------|
| Producto sin precio aplicable    | 404                |
| `applicationDate` no enviado     | 400                |
| `productId` no enviado           | 400                |
| `productId` no numérico (`abc`)  | 400                |
| `applicationDate` sin hora       | 400                |
| `productId` = 0                  | 400                |
| `productId` = -1                 | 400                |
| `brandId` = 0                    | 400                |
| `brandId` = -1                   | 400                |

### Casos extremos cubiertos en la query del repositorio

| Escenario                                             |
|-------------------------------------------------------|
| Fecha fuera de todos los rangos → vacío               |
| Dos precios solapados → gana el de mayor prioridad    |
| Tres precios solapados → gana la prioridad más alta   |
| Fecha exactamente al inicio del rango (inclusive)      |
| Fecha exactamente al final del rango (inclusive)       |
| Un segundo antes del inicio → vacío                   |
| Un segundo después del final → vacío                  |
| brandId diferente → vacío                             |
| productId diferente → vacío                           |
| Dos precios con la misma prioridad → devuelve uno sin error |

---

## Stack tecnológico

| Tecnología           | Versión  | Para qué la uso                            |
|----------------------|----------|--------------------------------------------|
| Java                 | 21       | Lenguaje                                   |
| Spring Boot          | 3.5.13   | Framework principal                        |
| Spring Data JPA      | —        | Acceso a datos                             |
| Spring Validation    | —        | Validación de parámetros en la API         |
| H2 Database          | —        | Base de datos en memoria                   |
| SLF4J + Logback      | —        | Logging con correlación de peticiones (MDC) |
| Lombok               | —        | Reducir código repetitivo                  |
| springdoc-openapi    | 2.8.6    | Documentación Swagger/OpenAPI              |
| JUnit 5 + Mockito    | —        | Tests unitarios y de integración           |
