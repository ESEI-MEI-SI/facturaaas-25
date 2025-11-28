# AGENTS - Arquitectura y Componentes de Backend Spring Boot

## 📋 Descripción General

Este documento describe la **arquitectura, componentes principales y decisiones técnicas** para desarrollar un backend con **Spring Boot 3.x** y **Java 17+**.

### Tecnologías Principales

* **Spring Boot 3.x**: Framework principal
* **Spring Core**: Gestión de dependencias e inversión de control (IoC)
* **Spring MVC**: Exposición de endpoints RESTful
* **Spring Data JPA**: Acceso a datos y persistencia
* **Spring Security**: Autenticación JWT y autorización basada en roles y recursos
* **JPA/Hibernate**: Mapeo objeto-relacional (ORM)
* **H2 Database**: Base de datos en memoria para desarrollo
* **MySQL/PostgreSQL**: Base de datos de producción
* **Maven**: Gestión de dependencias y construcción

---

## 🏗️ Arquitectura del Proyecto

### Estructura en Capas

```
com.example.proyecto
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

## 🔐 Seguridad

### Autenticación: JWT (JSON Web Tokens)

* **Librería recomendada**: `io.jsonwebtoken:jjwt-api:0.12.3`
* **Token expiration**: Configurable (típicamente 24 horas)
* **Secret key**: Configurada en `application.properties` (mínimo 256-bit)
* **Endpoints públicos**: `/`, `/health`, `/api/auth/**`

**Dependencias Maven**:
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

### Autorización: Basada en Roles + Recursos

#### 1. Autorización por Roles

* **Roles típicos**: `ADMIN`, `USER`, `MANAGER`, etc.
* **Implementación**: `@PreAuthorize("hasRole('ADMIN')")` a nivel de clase/método
* **Configuración**: En `SecurityConfig` con Spring Security

**Ejemplo**:
```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    // Endpoints solo para administradores
}
```

#### 2. Autorización por Propiedad de Recursos (Resource-Based)

* **Componente**: `ResourceSecurityService` (componente `@Component("resourceSecurity")`)
* **Implementación**: `@PreAuthorize` con expresiones SpEL
* **Principio**: Un usuario solo puede acceder a sus propios recursos

**Ejemplo de uso**:
```java
@PreAuthorize("@resourceSecurity.canAccessResource(#id)")
public ResponseEntity<ResourceDTO> obtenerPorId(@PathVariable Long id) {
    // ...
}
```

**Métodos típicos de verificación**:
```java
@Component("resourceSecurity")
public class ResourceSecurityService {
    
    // Verifica si el usuarioId es del usuario autenticado o es admin
    public boolean canAccess(Long usuarioId) {
        return isAdmin() || isOwner(usuarioId);
    }
    
    // Verifica si el recurso pertenece al usuario
    public boolean canAccessResource(Long resourceId) {
        return isAdmin() || isResourceOwner(resourceId);
    }
    
    // Verifica si el usuario es administrador
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
```

**Bypass para administradores**: Los usuarios con rol `ADMIN` pueden acceder a todos los recursos.

---

## 📦 Modelo de Datos

### Convenciones de Nombres

**Recomendación**: Usar **nombres de tabla en singular** o **plural** según convención del equipo.

**Opción 1 - Singular** (recomendado por algunos estándares):
```java
@Table(name = "usuario")  // singular
@Table(name = "producto") // singular
```

**Opción 2 - Plural** (más común en Rails y otros frameworks):
```java
@Table(name = "usuarios")  // plural
@Table(name = "productos") // plural
```

### Estructura Típica de Entidades

```java
@Entity
@Table(name = "entidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entidad {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    // Relación Many-to-One (muchas entidades pertenecen a un usuario)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    // Campos de auditoría
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    private LocalDateTime fechaActualizacion;
    
    // Estados con enumeración
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO;
    
    public enum Estado {
        ACTIVO,
        INACTIVO,
        ELIMINADO
    }
}
```

### Relaciones Comunes

**One-to-One**:
```java
@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
private Perfil perfil;
```

**One-to-Many**:
```java
@OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Recurso> recursos = new ArrayList<>();
```

**Many-to-One**:
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "categoria_id")
private Categoria categoria;
```

**Many-to-Many**:
```java
@ManyToMany
@JoinTable(
    name = "usuario_rol",
    joinColumns = @JoinColumn(name = "usuario_id"),
    inverseJoinColumns = @JoinColumn(name = "rol_id")
)
private Set<Rol> roles = new HashSet<>();
```

---

## 🎯 Componentes por Capa

### 1. Controladores (Controllers)

**Responsabilidad**: Manejar peticiones HTTP, validar entrada, invocar servicios y retornar respuestas.

**Tecnologías**: `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.

**Estructura típica**:
```java
@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {
    
    private final RecursoService recursoService;
    
    @GetMapping
    public ResponseEntity<List<RecursoDTO>> listar(
            @RequestParam Long usuarioId,
            @RequestParam(required = false) String filtro) {
        return ResponseEntity.ok(recursoService.obtenerPorUsuario(usuarioId, filtro));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessRecurso(#id)")
    public ResponseEntity<RecursoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(recursoService.obtenerPorId(id));
    }
    
    @PostMapping
    @PreAuthorize("@resourceSecurity.canAccess(#dto.usuarioId)")
    public ResponseEntity<RecursoDTO> crear(@Valid @RequestBody RecursoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(recursoService.crear(dto));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessRecurso(#id)")
    public ResponseEntity<RecursoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody RecursoDTO dto) {
        return ResponseEntity.ok(recursoService.actualizar(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessRecurso(#id)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
```

**Decisión arquitectónica**: Los endpoints utilizan **query parameters** para filtros en lugar de path variables:
- ✅ `GET /api/recursos?usuarioId=2&filtro=activos`
- ❌ ~~`GET /api/usuarios/2/recursos?filtro=activos`~~

### 2. Servicios (Services)

**Responsabilidad**: Contener la lógica de negocio, validaciones, transformaciones DTO↔Entity.

**Tecnología**: `@Service`, `@Transactional`

**Estructura típica**:
```java
@Service
@RequiredArgsConstructor
@Transactional
public class RecursoService {
    
    private final RecursoRepository recursoRepository;
    private final UsuarioRepository usuarioRepository;
    
    public List<RecursoDTO> obtenerPorUsuario(Long usuarioId, String filtro) {
        List<Recurso> recursos;
        if (filtro != null && !filtro.isEmpty()) {
            recursos = recursoRepository
                .findByUsuarioIdAndNombreContainingIgnoreCase(usuarioId, filtro);
        } else {
            recursos = recursoRepository.findByUsuarioId(usuarioId);
        }
        return recursos.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public RecursoDTO obtenerPorId(Long id) {
        Recurso recurso = recursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        return convertirADTO(recurso);
    }
    
    public RecursoDTO crear(RecursoDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Recurso recurso = new Recurso();
        recurso.setUsuario(usuario);
        recurso.setNombre(dto.getNombre());
        // ... otros campos
        
        Recurso guardado = recursoRepository.save(recurso);
        return convertirADTO(guardado);
    }
    
    public RecursoDTO actualizar(Long id, RecursoDTO dto) {
        Recurso recurso = recursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        
        recurso.setNombre(dto.getNombre());
        // ... actualizar otros campos
        recurso.setFechaActualizacion(LocalDateTime.now());
        
        Recurso actualizado = recursoRepository.save(recurso);
        return convertirADTO(actualizado);
    }
    
    public void eliminar(Long id) {
        Recurso recurso = recursoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Recurso no encontrado"));
        recursoRepository.delete(recurso);
    }
    
    // Conversión Entity → DTO
    private RecursoDTO convertirADTO(Recurso recurso) {
        RecursoDTO dto = new RecursoDTO();
        dto.setId(recurso.getId());
        dto.setNombre(recurso.getNombre());
        dto.setUsuarioId(recurso.getUsuario().getId());
        dto.setFechaCreacion(recurso.getFechaCreacion());
        return dto;
    }
}
```

### 3. Repositorios (Repositories)

**Responsabilidad**: Acceso a la base de datos mediante Spring Data JPA.

**Tecnología**: Interfaces que extienden `JpaRepository<Entity, ID>`

**Estructura típica**:
```java
@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {
    
    // Métodos de consulta derivados del nombre
    List<Recurso> findByUsuarioId(Long usuarioId);
    
    List<Recurso> findByUsuarioIdAndEstado(Long usuarioId, Recurso.Estado estado);
    
    List<Recurso> findByUsuarioIdAndNombreContainingIgnoreCase(
        Long usuarioId, String nombre);
    
    Optional<Recurso> findByIdAndUsuarioId(Long id, Long usuarioId);
    
    // Consultas personalizadas con @Query
    @Query("SELECT r FROM Recurso r WHERE r.usuario.id = :usuarioId " +
           "AND r.estado = :estado ORDER BY r.fechaCreacion DESC")
    List<Recurso> buscarPorUsuarioYEstado(
        @Param("usuarioId") Long usuarioId, 
        @Param("estado") Recurso.Estado estado);
    
    // Consultas nativas
    @Query(value = "SELECT * FROM recurso WHERE usuario_id = ?1 " +
                   "AND YEAR(fecha_creacion) = ?2", 
           nativeQuery = true)
    List<Recurso> buscarPorUsuarioYAnio(Long usuarioId, Integer anio);
}
```

### 4. DTOs (Data Transfer Objects)

**Responsabilidad**: Transferir datos entre capas sin exponer entidades JPA directamente.

**Ventajas**:
- Desacoplamiento del modelo de dominio
- Control sobre qué datos se exponen en la API
- Validaciones específicas para la capa de presentación

**Anotaciones de validación**:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecursoDTO {
    
    private Long id;
    
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;
    
    @NotNull(message = "El estado es obligatorio")
    private String estado;
    
    @Email(message = "El email debe ser válido")
    private String email;
    
    @Min(value = 0, message = "El valor debe ser mayor o igual a 0")
    @Max(value = 100, message = "El valor no puede exceder 100")
    private Integer valor;
    
    @DecimalMin(value = "0.0", message = "El precio debe ser positivo")
    @DecimalMax(value = "999999.99", message = "El precio es demasiado alto")
    private BigDecimal precio;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "El teléfono debe tener 10 dígitos")
    private String telefono;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaCreacion;
}
```

### 5. Configuración (Config)

**Responsabilidad**: Configuración de beans, seguridad, CORS, inicialización.

#### SecurityConfig
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/health", "/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

#### DataInitializer (Opcional)
```java
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Inicializar datos de prueba solo si no existen
        if (usuarioRepository.count() == 0) {
            // Crear usuario administrador
            Usuario admin = new Usuario();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setNombre("Administrador");
            admin.setRol(Usuario.Rol.ADMIN);
            usuarioRepository.save(admin);
            
            // Crear usuario normal
            Usuario user = new Usuario();
            user.setLogin("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setNombre("Usuario Normal");
            user.setRol(Usuario.Rol.USER);
            usuarioRepository.save(user);
        }
    }
}
```

### 6. Seguridad (Security)

#### JwtTokenProvider
```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private long jwtExpirationMs;
    
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        return claims.getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

#### JwtAuthenticationFilter
```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (jwt != null && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

#### ResourceSecurityService
```java
@Component("resourceSecurity")
@RequiredArgsConstructor
public class ResourceSecurityService {
    
    private final RecursoRepository recursoRepository;
    private final UsuarioRepository usuarioRepository;
    
    public boolean canAccess(Long usuarioId) {
        return isAdmin() || isOwner(usuarioId);
    }
    
    public boolean canAccessRecurso(Long recursoId) {
        return isAdmin() || isRecursoOwner(recursoId);
    }
    
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
    
    private boolean isOwner(Long usuarioId) {
        String currentUsername = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElse(null);
        
        return usuario != null && usuario.getLogin().equals(currentUsername);
    }
    
    private boolean isRecursoOwner(Long recursoId) {
        String currentUsername = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        
        Recurso recurso = recursoRepository.findById(recursoId)
            .orElse(null);
        
        return recurso != null && 
               recurso.getUsuario().getLogin().equals(currentUsername);
    }
    
    private Long getAuthenticatedUserId() {
        String currentUsername = SecurityContextHolder.getContext()
            .getAuthentication()
            .getName();
        
        return usuarioRepository.findByLogin(currentUsername)
            .map(Usuario::getId)
            .orElse(null);
    }
}
```

---

## 🔄 Flujos de Datos Principales

### 1. Autenticación (Login)

```
Cliente → POST /api/auth/login {username, password}
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
← AuthResponse {token, username, roles}
```

### 2. Acceso a Recurso Protegido

```
Cliente → GET /api/recursos/{id}
          Header: Authorization: Bearer <token>
   ↓
JwtAuthenticationFilter
   ↓ valida token
   ↓ establece SecurityContext
   ↓
@PreAuthorize("@resourceSecurity.canAccessRecurso(#id)")
   ↓ evalúa expresión SpEL
   ↓
ResourceSecurityService.canAccessRecurso(id)
   ↓ verifica propiedad
   ↓ ✓ OK o ✗ 403 Forbidden
   ↓
RecursoController.obtenerPorId()
   ↓
RecursoService.obtenerPorId()
   ↓
RecursoRepository.findById()
   ↓
← RecursoDTO
```

---

## 📝 Decisiones Arquitectónicas Comunes

### 1. Seguridad Basada en Recursos

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

### 2. Nombres de Tabla

**Opciones**:
- **Singular**: `usuario`, `producto`, `pedido`
- **Plural**: `usuarios`, `productos`, `pedidos`

**Decisión**: Elegir una convención y mantenerla consistente en todo el proyecto.

### 3. Query Parameters vs Path Variables

**Decisión recomendada**: Usar query parameters para filtros en lugar de path variables.

**Razón**: Mayor flexibilidad y claridad en endpoints de listado con múltiples filtros.

**Ejemplos**:
```
GET /api/recursos?usuarioId=2&estado=activo&desde=2024-01-01
GET /api/productos?categoriaId=5&precioMin=10&precioMax=100
```

### 4. Manejo de Errores Global

**Implementación con @ControllerAdvice**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(
            EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Errores de validación",
            errors,
            LocalDateTime.now()
        );
        return ResponseEntity.badRequest().body(error);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Acceso denegado",
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
```

---

## 🗄️ Base de Datos

### Desarrollo: H2 In-Memory

**Configuración** (`application.properties`):
```properties
# H2 Database (Desarrollo)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT Configuration
jwt.secret=your-256-bit-secret-key-here-base64-encoded
jwt.expiration=86400000
```

**Acceso consola H2**: `http://localhost:8080/h2-console`

### Producción: MySQL

**Configuración** (`application-prod.properties`):
```properties
# MySQL Database (Producción)
spring.datasource.url=jdbc:mysql://localhost:3306/nombre_bd
spring.datasource.username=usuario
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

### Producción: PostgreSQL

**Configuración** (`application-prod.properties`):
```properties
# PostgreSQL Database (Producción)
spring.datasource.url=jdbc:postgresql://localhost:5432/nombre_bd
spring.datasource.username=usuario
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

**Activación**: `--spring.profiles.active=prod`

---

## 🚀 Ejecución y Despliegue

### Desarrollo

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# Ejecutar con perfil específico
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=prod

# Ejecutar tests
mvn test
```

### Producción

```bash
# Empaquetar
mvn clean package

# Ejecutar JAR
java -jar target/aplicacion-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# Con variables de entorno
JWT_SECRET=your-secret DB_PASSWORD=password \
  java -jar target/aplicacion-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Puerto

- **Por defecto**: `8080`
- **Configuración**: `server.port=8080` en `application.properties`

---

## 🧪 Testing

### Test de Integración con Spring Boot Test

```java
@SpringBootTest
@AutoConfigureMockMvc
class RecursoControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void debeCrearRecurso() throws Exception {
        RecursoDTO dto = new RecursoDTO();
        dto.setNombre("Test");
        dto.setUsuarioId(1L);
        
        mockMvc.perform(post("/api/recursos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Test"));
    }
}
```

### Test Unitario de Servicio

```java
@ExtendWith(MockitoExtension.class)
class RecursoServiceTest {
    
    @Mock
    private RecursoRepository recursoRepository;
    
    @Mock
    private UsuarioRepository usuarioRepository;
    
    @InjectMocks
    private RecursoService recursoService;
    
    @Test
    void debeObtenerRecursoPorId() {
        // Given
        Recurso recurso = new Recurso();
        recurso.setId(1L);
        recurso.setNombre("Test");
        
        when(recursoRepository.findById(1L))
            .thenReturn(Optional.of(recurso));
        
        // When
        RecursoDTO resultado = recursoService.obtenerPorId(1L);
        
        // Then
        assertNotNull(resultado);
        assertEquals("Test", resultado.getNombre());
        verify(recursoRepository, times(1)).findById(1L);
    }
}
```

---

## 🤝 Extensión del Sistema

### Añadir Nueva Entidad con Seguridad

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

---

## 📚 Buenas Prácticas

- ✅ Usar DTOs para exponer datos (no entidades directamente)
- ✅ Validar entrada con anotaciones de Bean Validation
- ✅ Aplicar `@Transactional` en métodos de servicio que modifican datos
- ✅ Usar `ResponseEntity<T>` para control explícito de respuestas HTTP
- ✅ Documentar decisiones arquitectónicas importantes
- ✅ Mantener consistencia en naming conventions
- ✅ Aplicar seguridad a todos los endpoints sensibles
- ✅ Usar Lombok para reducir boilerplate
- ✅ Implementar manejo global de excepciones
- ✅ Separar configuraciones por perfil (dev, prod)
- ✅ No exponer información sensible en logs de producción
- ✅ Usar variables de entorno para secretos en producción

---

**Documento**: Plantilla genérica para proyectos Spring Boot  
**Versión**: 1.0  
**Última actualización**: 30 de octubre de 2025
