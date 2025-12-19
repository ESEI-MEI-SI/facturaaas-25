# AGENTS - Arquitectura y Componentes del Backend FACTURAaaS

## Descripción General

Este documento describe la **arquitectura, componentes principales y decisiones técnicas** del sistema backend **FACTURAaaS**, una aplicación SaaS para la gestión de facturas desarrollada con **Spring Boot 3.5.8** y **Java 17**.

### Tecnologías Principales

* **Spring Boot 3.5.8**: Framework principal
* **Spring Core**: Gestión de dependencias e inversión de control (IoC)
* **Spring MVC**: Exposición de endpoints RESTful
* **Spring Data JPA**: Acceso a datos y persistencia
* **Spring Security**: Autenticación JWT y autorización basada en roles y recursos
* **JPA/Hibernate**: Mapeo objeto-relacional (ORM)
* **H2 Database**: Base de datos en memoria para desarrollo
* **MySQL**: Base de datos de producción (configurada pero no activa)
* **Maven**: Gestión de dependencias y construcción

---

## Arquitectura del Proyecto

### Estructura en Capas

```
com.facturaaas
│
├── config/              → Configuración de Spring Boot, Security, CORS, inicialización
├── controller/          → Controladores REST (Spring MVC)
├── service/             → Lógica de negocio y reglas del dominio
├── repository/          → Interfaces de acceso a datos (Spring Data JPA)
├── model/               → Entidades JPA (modelo de dominio)
├── dto/                 → Objetos de transferencia de datos (DTOs)
├── security/            → Componentes de seguridad (JWT, filtros, etc.)
└── resources/
    ├── application.properties
    └── application-prod.properties
```

### Patrón de Diseño: MVC + Repository Pattern

```
HTTP Request → Controller → Service → Repository → Database
                    ↓
                   DTO
```

---

## Seguridad

### Autenticación: JWT (JSON Web Tokens)

* **Librería**: `io.jsonwebtoken:jjwt-api:0.12.3`
* **Token expiration**: 24 horas
* **Secret key**: Configurada en `application.properties` (256-bit)
* **Endpoints públicos**: `/`, `/health`, `/api/auth/**`, `/h2-console/**`

### Autorización: Basada en Roles + Recursos

#### 1. Autorización por Roles

* **Roles disponibles**: `ADMINISTRADOR`, `USUARIO`
* **Implementación**: `@PreAuthorize("hasRole('ADMIN')")` a nivel de clase/método

#### 2. Autorización por Propiedad de Recursos (Resource-Based)

* **Servicio**: `ResourceSecurityService` (componente `@Component("resourceSecurity")`)
* **Implementación**: `@PreAuthorize` con expresiones SpEL
* **Principio**: Un usuario solo puede acceder a sus propios recursos

**Ejemplo de uso**:
```java
@PreAuthorize("@resourceSecurity.canAccessCliente(#id)")
public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable Long id) {
    // ...
}
```

**Métodos de verificación**:
- `canAccess(Long usuarioId)`: Verifica si el usuarioId es del usuario autenticado o es admin
- `canAccessCliente(Long clienteId)`: Verifica propiedad de cliente
- `canAccessFactura(Long facturaId)`: Verifica propiedad de factura
- `canAccessFormaPago(Long formaPagoId)`: Verifica propiedad de forma de pago
- `canAccessPago(Long pagoId)`: Verifica propiedad de pago (vía factura)

**Bypass para administradores**: Los usuarios con rol `ADMINISTRADOR` pueden acceder a todos los recursos.

---

## Modelo de Datos

### Entidades JPA

#### 1. **Usuario** (`usuario`)
- **Descripción**: Usuarios del sistema (administradores o usuarios normales)
- **Campos clave**: login, password (BCrypt), email, nombre, rol, activo, fechaCreacion, fechaUltimoAcceso
- **Relaciones**: 
  - `OneToOne` con `DatosFacturacion`
  - `OneToMany` con `Cliente`, `Factura`, `FormaPago` (implícitas)
- **Roles**: `ADMINISTRADOR`, `USUARIO`

#### 2. **TipoIVA** (`tipo_iva`)
- **Descripción**: Tipos de IVA disponibles (entidad global del sistema)
- **Campos clave**: descripcion, porcentaje, activo
- **Gestión**: Solo ADMINISTRADOR
- **Ejemplos**: IVA normal (21%), reducido (10%), superreducido (4%), Sin IVA (0%)

#### 3. **FormaPago** (`forma_pago`)
- **Descripción**: Formas de pago específicas de cada usuario
- **Campos clave**: descripcion, numeroPagos, periodicidadDias, activa
- **Relaciones**: `ManyToOne` con `Usuario` (columna `usuario_id`)
- **Gestión**: Cada USUARIO gestiona sus propias formas de pago
- **Decisión arquitectónica**: Inicialmente era entidad global, cambiada a específica de usuario

#### 4. **DatosFacturacion** (`datos_facturacion`)
- **Descripción**: Datos fiscales y comerciales del usuario
- **Campos clave**: nombreComercial, nif, dirección, telefono, emailContacto, cuentaBancaria, tipo
- **Relaciones**: 
  - `OneToOne` con `Usuario`
  - `ManyToOne` con `TipoIVA` (tipoIVADefecto)
  - `ManyToOne` con `FormaPago` (formaPagoDefecto, debe ser del mismo usuario)
- **Tipos de entidad**: PARTICULAR, AUTONOMO, SOCIEDAD_LIMITADA, SOCIEDAD_ANONIMA, COOPERATIVA, ONG, OTRO

#### 5. **Cliente** (`cliente`)
- **Descripción**: Clientes de cada usuario
- **Campos clave**: nombre, nif, dirección (domicilio, localidad, codigoPostal, provincia), email, telefono, cuentaBancaria
- **Relaciones**: `ManyToOne` con `Usuario` (columna `usuario_id`)
- **Seguridad**: Acceso restringido al propietario (usuario que lo creó)

#### 6. **Factura** (`factura`)
- **Descripción**: Facturas emitidas por usuarios
- **Campos clave**: numeroFactura (auto-generado), ejercicio, fechaEmision, estado, comentarios, importeTotal, ivaTotal, sumaTotal
- **Relaciones**:
  - `ManyToOne` con `Usuario` (columna `usuario_id`)
  - `ManyToOne` con `Cliente` (debe ser del mismo usuario)
  - `ManyToOne` con `FormaPago` (debe ser del mismo usuario)
  - `OneToMany` con `LineaFactura` (cascade ALL, orphanRemoval)
  - `OneToMany` con `Pago` (cascade ALL, orphanRemoval)
- **Estados**: EMITIDA, ANULADA, PAGADA, RECLAMADA, ABONADA
- **Generación de número**: Formato `YYYY-NNNN` (año + secuencial)

#### 7. **LineaFactura** (`linea_factura`)
- **Descripción**: Líneas de detalle de una factura
- **Campos clave**: numeroLinea, concepto, cantidad, precioUnitario, porcentajeDescuento, importeTotal
- **Relaciones**:
  - `ManyToOne` con `Factura`
  - `ManyToOne` con `TipoIVA`
- **Cálculo automático**: El importeTotal se calcula en base a cantidad, precio y descuento

#### 8. **Pago** (`pago`)
- **Descripción**: Vencimientos de pago de una factura
- **Campos clave**: numeroPago, fechaVencimiento, importe, estado, fechaPago
- **Relaciones**: `ManyToOne` con `Factura`
- **Estados**: PENDIENTE, PAGADO, ANULADO
- **Generación**: Automática desde factura según su FormaPago

### Convenciones de Nombres

**Decisión arquitectónica**: Se utilizan **nombres de tabla en singular** (no plural)
- ✅ `usuario`, `cliente`, `factura`, `pago`
- ❌ ~~`usuarios`, `clientes`, `facturas`, `pagos`~~

---

## Componentes por Capa

### 1. Controladores (Controllers)

**Responsabilidad**: Manejar peticiones HTTP, validar entrada, invocar servicios y retornar respuestas.

**Tecnologías**: `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.

**Seguridad**: 
- `@PreAuthorize` a nivel de clase (rol requerido)
- `@PreAuthorize` a nivel de método (verificación de propiedad de recursos)

**Controladores implementados**:

#### AuthController (`/api/auth`)
- `POST /login`: Autenticación y generación de token JWT
- `POST /register`: Registro de nuevos usuarios
- **Acceso**: Público

#### UsuarioController (`/api/usuarios`) - ADMIN
- `GET ?patron={patron}`: Listar usuarios con filtro opcional
- `GET /{id}`: Obtener usuario por ID
- `POST`: Crear nuevo usuario
- `PUT /{id}`: Actualizar usuario
- `DELETE /{id}`: Eliminar lógicamente usuario (activo=false)
- **Acceso**: Solo ADMINISTRADOR

#### TipoIVAController (`/api/tipos-iva`) - ADMIN
- `GET`: Listar tipos de IVA
- `GET /activos`: Listar solo activos
- `GET /{id}`: Obtener por ID
- `POST`: Crear nuevo tipo
- `PUT /{id}`: Actualizar tipo
- `DELETE /{id}`: Eliminar lógicamente (activo=false)
- **Acceso**: Solo ADMINISTRADOR

#### FormaPagoController (`/api/formas-pago`) - USER
- `GET ?usuarioId={id}&soloActivas={bool}`: Listar formas de pago del usuario
- `GET /{id}`: Obtener por ID (solo propias)
- `POST`: Crear nueva forma de pago
- `PUT /{id}`: Actualizar forma de pago (solo propia)
- `DELETE /{id}`: Eliminar lógicamente (solo propia)
- **Acceso**: USUARIO (solo sus propias formas de pago)

#### ClienteController (`/api/clientes`) - USER
- `GET ?usuarioId={id}&patron={patron}`: Listar clientes del usuario con filtro opcional
- `GET /{id}`: Obtener por ID (solo propios)
- `POST`: Crear nuevo cliente
- `PUT /{id}`: Actualizar cliente (solo propio)
- **Acceso**: USUARIO (solo sus propios clientes)

#### FacturaController (`/api/facturas`) - USER
- `GET ?usuarioId={id}&clienteId={id}`: Listar facturas con filtros
- `GET /{id}`: Obtener por ID (solo propias)
- `POST`: Crear nueva factura
- `PUT /{id}`: Actualizar factura (solo propia)
- `POST /{id}/generar-pagos`: Generar pagos según forma de pago
- **Acceso**: USUARIO (solo sus propias facturas)

#### PagoController (`/api/pagos`) - USER
- `GET ?usuarioId={id}&clienteId={id}`: Listar pagos con filtros
- `GET /{id}`: Obtener por ID (solo propios vía factura)
- `PATCH /{id}/estado?estado={estado}`: Actualizar estado del pago
- **Acceso**: USUARIO (solo sus propios pagos)

**Decisión arquitectónica**: Los endpoints utilizan **query parameters** en lugar de path variables para filtros
- ✅ `GET /api/clientes?usuarioId=2&patron=empresa`
- ❌ ~~`GET /api/usuarios/2/clientes?patron=empresa`~~

### 2. Servicios (Services)

**Responsabilidad**: Contener la lógica de negocio, validaciones, transformaciones DTO↔Entity.

**Tecnología**: `@Service`, `@Transactional`

**Servicios implementados**:
- `AuthService`: Autenticación, registro, generación de tokens
- `UsuarioService`: CRUD de usuarios
- `TipoIVAService`: CRUD de tipos de IVA
- `FormaPagoService`: CRUD de formas de pago (filtrado por usuario)
- `ClienteService`: CRUD de clientes (filtrado por usuario)
- `FacturaService`: CRUD de facturas, generación de número, cálculo de totales
- `PagoService`: CRUD de pagos, generación desde factura
- `ResourceSecurityService`: Verificación de propiedad de recursos

**Patrón de conversión**:
```java
// Entity → DTO
private ClienteDTO convertirADTO(Cliente entity) { ... }

// DTO → Entity (para creación/actualización)
Cliente entity = new Cliente();
entity.setNombre(dto.getNombre());
// ...
```

### 3. Repositorios (Repositories)

**Responsabilidad**: Acceso a la base de datos mediante Spring Data JPA.

**Tecnología**: Interfaces que extienden `JpaRepository<Entity, ID>`

**Métodos personalizados** (ejemplos):
```java
// UsuarioRepository
Optional<Usuario> findByLogin(String login);
List<Usuario> findByNombreContainingIgnoreCase(String patron);

// ClienteRepository
List<Cliente> findByUsuarioId(Long usuarioId);
List<Cliente> findByUsuarioIdAndNombreContainingIgnoreCase(Long usuarioId, String patron);

// FacturaRepository
List<Factura> findByUsuarioId(Long usuarioId);
List<Factura> findByUsuarioIdAndClienteId(Long usuarioId, Long clienteId);
Optional<Factura> findTopByEjercicioOrderByNumeroFacturaDesc(Integer ejercicio);

// FormaPagoRepository
List<FormaPago> findByUsuarioId(Long usuarioId);
List<FormaPago> findByUsuarioIdAndActivaTrue(Long usuarioId);
```

### 4. DTOs (Data Transfer Objects)

**Responsabilidad**: Transferir datos entre capas sin exponer entidades JPA directamente.

**Ventajas**:
- Desacoplamiento del modelo de dominio
- Control sobre qué datos se exponen en la API
- Validaciones específicas para la capa de presentación

**Anotaciones de validación**:
- `@NotNull`, `@NotBlank`, `@NotEmpty`
- `@Size(min=, max=)`, `@Min`, `@Max`
- `@Email`, `@Pattern`
- `@DecimalMin`, `@DecimalMax`

**DTOs implementados**:
- `LoginRequest`, `AuthResponse`, `RegisterRequest`
- `UsuarioDTO`, `TipoIVADTO`, `FormaPagoDTO`
- `DatosFacturacionDTO`, `ClienteDTO`
- `FacturaDTO`, `LineaFacturaDTO`, `PagoDTO`

### 5. Configuración (Config)

**Responsabilidad**: Configuración de beans, seguridad, CORS, inicialización.

#### SecurityConfig
- Configuración de Spring Security
- Filtro JWT (`JwtAuthenticationFilter`)
- CORS habilitado para desarrollo (`http://localhost:3000`, `http://localhost:5173`)
- Sesiones stateless
- Endpoints públicos configurados

#### DataInitializer
- Implementa `CommandLineRunner`
- Inicializa datos de prueba al arrancar la aplicación:
  - 1 Admin (login: `admin`, password: `admin123`)
  - 1 Usuario (login: `user`, password: `user123`)
  - 4 Tipos de IVA (21%, 10%, 4%, 0%)
  - 2 Formas de pago por usuario

### 6. Seguridad (Security)

#### JwtTokenProvider
- Generación de tokens JWT
- Validación de tokens
- Extracción de claims (login, rol)
- Configuración de secret key y expiración

#### JwtAuthenticationFilter
- Filtro de Spring Security (`OncePerRequestFilter`)
- Intercepta requests HTTP
- Extrae y valida token del header `Authorization: Bearer <token>`
- Establece autenticación en `SecurityContext`

#### ResourceSecurityService
- Componente central para verificación de propiedad de recursos
- Métodos para verificar si un usuario puede acceder a un recurso específico
- Bypass automático para administradores
- Integrado con `@PreAuthorize` mediante expresiones SpEL

---

## Flujos de Datos Principales

### 1. Autenticación (Login)

```
Cliente → POST /api/auth/login {login, password}
   ↓
AuthController
   ↓
AuthService.login()
   ↓
UsuarioRepository.findByLogin()
   ↓
PasswordEncoder.matches() ✓
   ↓
JwtTokenProvider.generateToken()
   ↓
← AuthResponse {token, login, rol}
```

### 2. Acceso a Recurso Protegido

```
Cliente → GET /api/clientes/{id}
          Header: Authorization: Bearer <token>
   ↓
JwtAuthenticationFilter
   ↓ valida token
   ↓ establece SecurityContext
   ↓
@PreAuthorize("@resourceSecurity.canAccessCliente(#id)")
   ↓ evalúa expresión SpEL
   ↓
ResourceSecurityService.canAccessCliente(id)
   ↓ verifica propiedad
   ↓ ✓ OK o ✗ 403 Forbidden
   ↓
ClienteController.obtenerPorId()
   ↓
ClienteService.obtenerPorId()
   ↓
ClienteRepository.findById()
   ↓
← ClienteDTO
```

### 3. Creación de Factura con Generación de Pagos

```
Cliente → POST /api/facturas
   ↓
FacturaController.crear(dto)
   ↓ validación @PreAuthorize
   ↓
FacturaService.crear(dto)
   ↓ generar número factura
   ↓ validar cliente pertenece al usuario
   ↓ validar forma pago pertenece al usuario
   ↓ crear entidad Factura
   ↓ crear LineaFactura entities
   ↓ calcular totales
   ↓
FacturaRepository.save()
   ↓
← FacturaDTO
   ↓
Cliente → POST /api/facturas/{id}/generar-pagos
   ↓
FacturaService.generarPagos(id)
   ↓ obtener factura y forma de pago
   ↓ eliminar pagos existentes
   ↓ crear N pagos según numeroPagos
   ↓ calcular fechas según periodicidadDias
   ↓ distribuir importe total
   ↓
PagoRepository.saveAll()
```

---

## Decisiones Arquitectónicas

### 1. FormaPago: De Global a Específica de Usuario

**Decisión inicial**: FormaPago era una entidad global gestionada por administradores.

**Problema identificado**: Los usuarios necesitan personalizar sus propias formas de pago.

**Solución adoptada**:
- Añadida relación `@ManyToOne` con `Usuario`
- Cada usuario gestiona sus propias formas de pago
- Filtrado automático por usuario en todas las operaciones
- Validación en `DatosFacturacion` y `Factura` para asegurar que la forma de pago pertenezca al usuario

**Impacto**:
- H7 y H8 cambiadas de ADMIN a USER
- H9 actualizada (menú de usuario incluye "Formas de Pago")
- FormaPagoController usa seguridad basada en recursos

### 2. Seguridad Basada en Recursos

**Requisito**: Un usuario no debe poder acceder a recursos de otro usuario.

**Solución adoptada**:
- Creación de `ResourceSecurityService`
- Uso de `@PreAuthorize` con SpEL a nivel de método
- Verificación automática antes de ejecutar operaciones
- Mínimos cambios en controllers (solo anotaciones)

**Ventajas**:
- Centralización de lógica de seguridad
- Fácil mantenimiento y extensión
- Expresiones auto-documentadas
- Bypass automático para administradores

### 3. Nombres de Tabla en Singular

**Decisión**: Usar nombres de tabla en singular en vez de plural.

**Razón**: Consistencia con convenciones de naming en algunos estándares y preferencia del proyecto.

**Implementación**: 
```java
@Table(name = "usuario")  // no "usuarios"
@Table(name = "cliente")  // no "clientes"
```

### 4. Query Parameters vs Path Variables

**Decisión**: Usar query parameters para filtros en lugar de path variables.

**Razón**: Mayor flexibilidad y claridad en endpoints de listado con múltiples filtros.

**Ejemplos**:
```
GET /api/clientes?usuarioId=2&patron=empresa
GET /api/facturas?usuarioId=2&clienteId=5
GET /api/pagos?usuarioId=2&clienteId=3
```

### 5. Generación Automática de Número de Factura

**Formato**: `YYYY-NNNN` (año + secuencial de 4 dígitos)

**Implementación**:
```java
private String generarNumeroFactura(Integer ejercicio) {
    Optional<Factura> ultimaFactura = facturaRepository
        .findTopByEjercicioOrderByNumeroFacturaDesc(ejercicio);
    
    int siguienteNumero = ultimaFactura
        .map(f -> extraerNumeroSecuencial(f.getNumeroFactura()) + 1)
        .orElse(1);
    
    return String.format("%d-%04d", ejercicio, siguienteNumero);
}
```

**Ejemplo**: `2025-0001`, `2025-0002`, ..., `2025-9999`

---

## Base de Datos

### Desarrollo: H2 In-Memory

**Configuración** (`application.properties`):
```properties
spring.datasource.url=jdbc:h2:mem:facturaaas
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
```

**Acceso consola H2**: `http://localhost:8080/h2-console`

### Producción: MySQL

**Configuración** (`application-prod.properties`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/facturaaas
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

**Activación**: `--spring.profiles.active=prod`

---

## Ejecución y Despliegue

### Desarrollo

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# Ejecutar con perfil específico
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=prod
```

### Producción

```bash
# Empaquetar
mvn clean package

# Ejecutar JAR
java -jar target/facturaaas-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Puerto

- **Desarrollo**: `8080`
- **Configuración**: `server.port=8080` en `application.properties`

---

## Testing

### Usuarios de Prueba

Inicializados automáticamente por `DataInitializer`:

```
Administrador:
  login: admin
  password: admin123
  rol: ADMINISTRADOR

Usuario Normal:
  login: user
  password: user123
  rol: USUARIO
```

### Endpoints de Prueba

```bash
# Obtener token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"user","password":"user123"}' \
  | jq -r '.token')

# Usar token en requests
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/clientes?usuarioId=2
```

---

## Documentación Relacionada

- **`README.md`**: Guía de inicio rápido y configuración
- **`historias.md`**: Historias de usuario del proyecto
- **`API_TESTS.md`**: Scripts de prueba con curl
- **`test_api.sh`**: Script automatizado de pruebas manuales

---

## Funcionalidades Pendientes

### Sprint 2 (Propuestas)

1. **DatosFacturacion Service y Controller**
   - Actualmente solo tiene Repository
   - Implementar CRUD completo

2. **Generación de PDF**
   - Generar PDF de facturas
   - Endpoint: `GET /api/facturas/{id}/pdf`

3. **Notificaciones por Email**
   - Envío de facturas por email
   - Recordatorios de pagos pendientes

4. **Exportación de Datos**
   - Exportar facturas a Excel/CSV
   - Exportar informes de pagos

5. **Dashboard/Estadísticas**
   - Total facturado por período
   - Pagos pendientes/cobrados
   - Gráficos de evolución

6. **Tests Unitarios y de Integración**
   - JUnit 5 + Mockito
   - Spring Boot Test
   - Coverage objetivo: >80%

7. **Documentación API con Swagger/OpenAPI**
   - SpringDoc OpenAPI
   - Interfaz interactiva de pruebas

---

## Métricas del Proyecto

**Estado actual** (30 de octubre de 2025):

- **Entidades JPA**: 8
- **Controllers**: 7
- **Services**: 8
- **Repositories**: 8
- **DTOs**: 11+
- **Endpoints REST**: ~35
- **Líneas de código**: ~5000+
- **Compilación**: Exitosa
- **Historias implementadas**: H1-H17

---

## Contribución y Mantenimiento

### Extensión del Sistema

Para añadir una nueva entidad con seguridad basada en recursos:

1. **Crear entidad JPA** con relación `@ManyToOne` a `Usuario`
2. **Crear DTO** con validaciones apropiadas
3. **Crear Repository** con métodos de filtrado por usuario
4. **Crear Service** con conversiones DTO↔Entity
5. **Añadir método en `ResourceSecurityService`**:
   ```java
   public boolean canAccessNuevaEntidad(Long id) {
       return isAdmin() || isNuevaEntidadOwner(id);
   }
   ```
6. **Crear Controller** con `@PreAuthorize`:
   ```java
   @PreAuthorize("@resourceSecurity.canAccessNuevaEntidad(#id)")
   public ResponseEntity<NuevaEntidadDTO> obtenerPorId(@PathVariable Long id)
   ```

### Buenas Prácticas

- Usar DTOs para exponer datos (no entidades directamente)
- Validar entrada con anotaciones de Bean Validation
- Aplicar `@Transactional` en métodos de servicio que modifican datos
- Usar `ResponseEntity<T>` para control explícito de respuestas HTTP
- Documentar decisiones arquitectónicas importantes
- Mantener consistencia en naming conventions
- Aplicar seguridad a todos los endpoints sensibles

---

**Documento actualizado**: 30 de octubre de 2025  
**Versión del proyecto**: 1.0.0-SNAPSHOT  
**Estado**: Producción-ready (falta testing exhaustivo)
