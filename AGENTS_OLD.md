## Descripción General

Este documento describe los **agentes principales (componentes)** del sistema backend desarrollado en **Spring Boot**, utilizando:

* **Spring Core**: para la gestión de dependencias e inversión de control (IoC).
* **Spring MVC**: para exponer endpoints RESTful.
* **Spring Data JPA**: para acceso a datos y persistencia usando JPA (Hibernate como proveedor por defecto).
* **JPA (Java Persistence API)**: para el mapeo objeto-relacional (ORM).

---

## Estructura del Proyecto

El proyecto sigue una arquitectura en capas estándar:

```
com.example.project
│
├── controller         → Controladores REST (Spring MVC)
├── service            → Lógica de negocio
├── repository         → Interfaces de acceso a datos (Spring Data JPA)
├── model              → Entidades JPA
├── dto                → Objetos de transferencia de datos (Data Transfer Objects)
└── config             → Configuración de Spring Boot y beans personalizados
```

---

## Elementos Clave

### 1. **Controladores (Controllers)**

* **Responsabilidad**: manejar peticiones HTTP, mapear rutas y delegar la lógica al servicio correspondiente.
* **Tecnología**: `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc.

**Ejemplo**:

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
```

---

### 2. **Servicios (Services)**

* **Responsabilidad**: contener la lógica de negocio, validaciones y reglas del dominio.
* **Tecnología**: anotación `@Service`.

**Ejemplo**:

```java
@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return UserMapper.toDto(user);
    }
}
```

---

### 3. **Repositorios (Repositories)**

* **Responsabilidad**: acceso a la base de datos usando Spring Data JPA.
* **Tecnología**: interfaces que extienden `JpaRepository`, `CrudRepository` o `PagingAndSortingRepository`.

**Ejemplo**:

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

---

### 4. **Entidades (Entities)**

* **Responsabilidad**: representar las tablas de la base de datos en forma de clases Java.
* **Tecnología**: anotaciones JPA como `@Entity`, `@Table`, `@Id`, `@ManyToOne`, etc.

**Ejemplo**:

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
}
```

---

### 5. **DTOs (Data Transfer Objects)**

* **Responsabilidad**: transportar datos entre capas sin exponer directamente las entidades JPA.
* **Ventaja**: desacopla el modelo de dominio de la interfaz pública (API).

**Ejemplo**:

```java
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
```

---

### 6. **Mappers (opcional)**

* **Responsabilidad**: convertir entre entidades y DTOs.
* **Tecnología**: manuales o automáticos (MapStruct, ModelMapper, etc.)

**Ejemplo**:

```java
public class UserMapper {
    public static UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
```

---

### 7. **Configuración (Configuration)**

* **Responsabilidad**: configuración personalizada de beans, CORS, seguridad, etc.
* **Tecnología**: clases anotadas con `@Configuration`, `@Bean`, `@Enable...`

**Ejemplo**:

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return registry -> registry.addMapping("/**").allowedOrigins("*");
    }
}
```

---

## Flujo de Datos Típico

```
HTTP Request
   ↓
Controller
   ↓
Service
   ↓
Repository → Base de Datos
   ↑
Service (conversión a DTO)
   ↑
Controller (ResponseEntity)
   ↑
HTTP Response
```

---

## Dependencias Principales (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Base de datos (ej: H2, PostgreSQL, MySQL) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok (opcional) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## Conclusión

Este archivo resume los eleemntos clave que componen el backend basado en Spring Boot. Cada componente está diseñado para cumplir una función específica dentro de una arquitectura desacoplada, mantenible y escalable.

Para más detalles técnicos, consulta la documentación del proyecto o el fichero `README.md`.

