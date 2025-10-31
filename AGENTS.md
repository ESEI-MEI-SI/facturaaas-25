# AGENTS - Arquitectura Backend Spring Boot [Versión Abreviada]

## 📋 Descripción General

Guía arquitectónica para desarrollar backends con **Spring Boot 3.x** y **Java 17+**, enfocada en patrones de diseño, seguridad JWT y autorización basada en recursos.

### Stack Tecnológico

* **Spring Boot 3.x** - Framework principal
* **Spring Data JPA** - Persistencia con Hibernate
* **Spring Security** - Autenticación JWT + autorización por roles y recursos
* **H2/MySQL/PostgreSQL** - Bases de datos
* **Maven** - Gestión de dependencias

---

## 🏗️ Arquitectura en Capas

### Estructura de Paquetes

```
com.example.proyecto
├── config/         → Configuración (Security, CORS, inicialización)
├── controller/     → Endpoints REST
├── service/        → Lógica de negocio
├── repository/     → Acceso a datos (Spring Data JPA)
├── model/          → Entidades JPA
├── dto/            → Data Transfer Objects
└── security/       → JWT, filtros, verificación de recursos
```

### Patrón MVC + Repository

```
HTTP Request → Controller → Service → Repository → Database
                    ↓
                   DTO
```

---

## 🔐 Seguridad

### 1. Autenticación JWT

**Librería**: `io.jsonwebtoken:jjwt-api:0.12.3`

**Componentes**:
* `JwtTokenProvider` - Generación y validación de tokens
* `JwtAuthenticationFilter` - Filtro que intercepta requests y valida tokens
* Token en header: `Authorization: Bearer <token>`

**Configuración típica**:
```properties
jwt.secret=your-256-bit-secret-key-base64-encoded
jwt.expiration=86400000  # 24 horas
```

### 2. Autorización por Roles

```java
@PreAuthorize("hasRole('ADMIN')")
public class AdminController { }
```

**Roles típicos**: `ADMIN`, `USER`, `MANAGER`

### 3. Autorización por Recursos (Resource-Based)

**Componente central**: `ResourceSecurityService`

**Principio**: Un usuario solo puede acceder a sus propios recursos.

**Ejemplo de uso**:
```java
@PreAuthorize("@resourceSecurity.canAccessRecurso(#id)")
public ResponseEntity<RecursoDTO> obtenerPorId(@PathVariable Long id) { }
```

**Métodos típicos en ResourceSecurityService**:
* `canAccess(Long usuarioId)` - Verifica propiedad de usuario
* `canAccessRecurso(Long id)` - Verifica propiedad de recurso
* `isAdmin()` - Bypass para administradores

---

## 📦 Modelo de Datos

### Entidad JPA Típica

```java
@Entity
@Table(name = "recurso")  // Usar singular o plural consistentemente
public class Recurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;  // Relación con propietario
    
    @Column(nullable = false)
    private String nombre;
    
    private LocalDateTime fechaCreacion;
}
```

### Relaciones Comunes

* **`@OneToOne`** - Relación 1:1 (ej: Usuario ↔ Perfil)
* **`@OneToMany`** - Relación 1:N (ej: Usuario → Recursos)
* **`@ManyToOne`** - Relación N:1 (ej: Recurso → Usuario)
* **`@ManyToMany`** - Relación N:M (ej: Usuario ↔ Roles)

---

## 🎯 Componentes por Capa

### Controllers

**Responsabilidad**: Manejar HTTP, validar, invocar servicios

**Anotaciones clave**:
* `@RestController` - Marca la clase como controlador REST
* `@RequestMapping("/api/recursos")` - Base path
* `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
* `@PreAuthorize` - Seguridad a nivel de método
* `@Valid` - Validación de DTOs

**Operaciones CRUD típicas**:
* `GET /api/recursos?usuarioId=X&filtro=Y` - Listar
* `GET /api/recursos/{id}` - Obtener por ID
* `POST /api/recursos` - Crear
* `PUT /api/recursos/{id}` - Actualizar
* `DELETE /api/recursos/{id}` - Eliminar

### Services

**Responsabilidad**: Lógica de negocio, validaciones, conversiones DTO↔Entity

**Anotaciones clave**:
* `@Service` - Marca la clase como servicio
* `@Transactional` - Gestión de transacciones

**Tareas típicas**:
* Validar reglas de negocio
* Convertir entre DTOs y Entidades
* Coordinar operaciones en múltiples repositorios
* Calcular valores derivados

### Repositories

**Responsabilidad**: Acceso a datos

```java
@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {
    List<Recurso> findByUsuarioId(Long usuarioId);
    List<Recurso> findByUsuarioIdAndNombreContaining(Long usuarioId, String nombre);
    Optional<Recurso> findByIdAndUsuarioId(Long id, Long usuarioId);
}
```

**Métodos automáticos**: Spring Data JPA genera implementación desde el nombre del método.

### DTOs

**Responsabilidad**: Transferir datos sin exponer entidades

**Validaciones comunes**:
* `@NotNull`, `@NotBlank`, `@NotEmpty`
* `@Size(min=, max=)`, `@Min`, `@Max`
* `@Email`, `@Pattern`
* `@DecimalMin`, `@DecimalMax`

**Ventajas**:
* Desacoplamiento del modelo de dominio
* Control de datos expuestos en API
* Validaciones específicas por endpoint

---

## ⚙️ Configuración

### SecurityConfig

**Elementos clave**:
* `SecurityFilterChain` - Configura seguridad HTTP
* `JwtAuthenticationFilter` - Filtro personalizado para JWT
* CORS - Configuración de orígenes permitidos
* Endpoints públicos - `/api/auth/**`, `/health`
* `PasswordEncoder` - BCrypt para passwords
* `@EnableMethodSecurity` - Habilita `@PreAuthorize`

### DataInitializer (Opcional)

**Propósito**: Inicializar datos de prueba al arrancar

```java
@Component
public class DataInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) {
        // Crear usuarios de prueba, datos maestros, etc.
    }
}
```

---

## 🔄 Flujos Principales

### Flujo de Autenticación

```
1. Cliente → POST /api/auth/login {username, password}
2. AuthService valida credenciales
3. JwtTokenProvider genera token
4. ← AuthResponse {token, username, roles}
```

### Flujo de Acceso a Recurso Protegido

```
1. Cliente → GET /api/recursos/{id}
            Header: Authorization: Bearer <token>
2. JwtAuthenticationFilter valida token
3. @PreAuthorize evalúa expresión SpEL
4. ResourceSecurityService verifica propiedad
5. ✓ OK → Controller → Service → Repository → DTO
   ✗ 403 Forbidden
```

---

## 📝 Decisiones Arquitectónicas

### 1. Seguridad Basada en Recursos

**Implementación**:
* Componente `ResourceSecurityService` centralizado
* `@PreAuthorize` con SpEL en controllers
* Verificación antes de cada operación
* Bypass automático para administradores

**Ventaja**: Seguridad declarativa, fácil de mantener y auditar

### 2. Convención de Nombres de Tabla

**Opciones**:
* Singular: `usuario`, `producto`, `pedido`
* Plural: `usuarios`, `productos`, `pedidos`

**Decisión**: Elegir una y mantener consistencia en todo el proyecto

### 3. Filtros con Query Parameters

**Recomendación**: Usar query parameters para filtros

```
✅ GET /api/recursos?usuarioId=2&estado=activo
❌ GET /api/usuarios/2/recursos?estado=activo
```

**Razón**: Mayor flexibilidad con múltiples filtros opcionales

### 4. Manejo Global de Errores

**Implementación**: `@RestControllerAdvice`

**Excepciones típicas**:
* `EntityNotFoundException` → 404 Not Found
* `MethodArgumentNotValidException` → 400 Bad Request (validación)
* `AccessDeniedException` → 403 Forbidden
* `Exception` → 500 Internal Server Error

---

## 🗄️ Bases de Datos

### Desarrollo - H2

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
```

### Producción - MySQL

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_name
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

### Producción - PostgreSQL

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/db_name
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
```

**Perfiles**: Usar `--spring.profiles.active=prod` para activar configuración de producción

---

## 🚀 Ejecución

### Desarrollo

```bash
mvn spring-boot:run
```

### Producción

```bash
mvn clean package
java -jar target/app.jar --spring.profiles.active=prod
```

**Puerto por defecto**: 8080 (configurable con `server.port`)

---

## 🧪 Testing

### Test de Integración

```java
@SpringBootTest
@AutoConfigureMockMvc
class ControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void debeCrearRecurso() throws Exception {
        mockMvc.perform(post("/api/recursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{...}"))
                .andExpect(status().isCreated());
    }
}
```

### Test Unitario

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    @Test
    void debeObtenerRecurso() {
        // Given, When, Then con Mockito
    }
}
```

---

## 🤝 Extensión del Sistema

### Añadir Nueva Entidad con Seguridad

1. **Entidad JPA** con `@ManyToOne` a `Usuario`
2. **DTO** con validaciones
3. **Repository** con métodos de filtrado por usuario
4. **Service** con lógica y conversiones
5. **ResourceSecurityService**: añadir `canAccessNuevaEntidad(Long id)`
6. **Controller** con `@PreAuthorize("@resourceSecurity.canAccessNuevaEntidad(#id)")`

---

## 📚 Buenas Prácticas

### Código
* ✅ Usar DTOs, no exponer entidades directamente
* ✅ Validar con Bean Validation (`@Valid`, `@NotNull`, etc.)
* ✅ `@Transactional` en servicios que modifican datos
* ✅ `ResponseEntity<T>` para control de respuestas HTTP
* ✅ Usar Lombok para reducir boilerplate

### Seguridad
* ✅ Aplicar `@PreAuthorize` a todos los endpoints sensibles
* ✅ Usar variables de entorno para secretos en producción
* ✅ No exponer información sensible en logs
* ✅ Validar propiedad de recursos antes de operaciones

### Arquitectura
* ✅ Mantener consistencia en nombres (singular/plural)
* ✅ Documentar decisiones arquitectónicas
* ✅ Separar configuraciones por perfil (dev/prod)
* ✅ Implementar manejo global de excepciones
* ✅ Un controlador por entidad principal

---

## 📊 Componentes de Seguridad JWT

### JwtTokenProvider
* Genera tokens JWT
* Valida tokens
* Extrae claims (username, roles)

### JwtAuthenticationFilter
* Extiende `OncePerRequestFilter`
* Intercepta requests HTTP
* Extrae token del header `Authorization`
* Valida y establece autenticación en `SecurityContext`

### ResourceSecurityService
* Verifica propiedad de recursos
* Integrado con `@PreAuthorize` mediante SpEL
* Bypass para administradores
* Métodos reutilizables para diferentes entidades

---

## 🔑 Dependencias Maven Esenciales

### Spring Boot
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

### Starters
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### JWT
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<!-- jjwt-impl y jjwt-jackson con scope runtime -->
```

### Base de Datos
```xml
<!-- H2 para desarrollo -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- MySQL para producción -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Utilidades
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 📖 Resumen de Anotaciones Clave

### Spring Core
* `@Component`, `@Service`, `@Repository`, `@Controller`
* `@Autowired`, `@RequiredArgsConstructor` (Lombok)
* `@Configuration`, `@Bean`
* `@Value` - Inyectar propiedades

### Spring MVC
* `@RestController`, `@RequestMapping`
* `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`
* `@PathVariable`, `@RequestParam`, `@RequestBody`
* `@Valid` - Activar validación

### Spring Data JPA
* `@Entity`, `@Table`, `@Id`
* `@GeneratedValue`, `@Column`
* `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`
* `@JoinColumn`, `@JoinTable`

### Spring Security
* `@EnableWebSecurity`, `@EnableMethodSecurity`
* `@PreAuthorize`, `@PostAuthorize`
* `@Secured`, `@RolesAllowed`

### Validación
* `@NotNull`, `@NotBlank`, `@NotEmpty`
* `@Size`, `@Min`, `@Max`
* `@Email`, `@Pattern`
* `@DecimalMin`, `@DecimalMax`

### Transacciones
* `@Transactional` - Gestión automática de transacciones

### Testing
* `@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`
* `@Mock`, `@InjectMocks` (Mockito)
* `@AutoConfigureMockMvc`

---

**Documento**: Guía abreviada de arquitectura Spring Boot  
**Versión**: 1.0  
**Fecha**: 30 de octubre de 2025
