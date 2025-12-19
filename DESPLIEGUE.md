# DESPLIEGUE - FACTURAaaS en Render.com

Este documento detalla los pasos necesarios para desplegar el backend FACTURAaaS junto con un frontend React en la plataforma PaaS **Render.com**, utilizando **PostgreSQL** como base de datos.

---

## Índice

1. [Arquitectura de Despliegue](#arquitectura-de-despliegue)
2. [Cambios en el Proyecto](#cambios-en-el-proyecto)
3. [Variables de Entorno](#variables-de-entorno)
4. [Preparación de Artefactos](#preparación-de-artefactos)
5. [Despliegue en Render.com](#despliegue-en-rendercom)
6. [Verificación del Despliegue](#verificación-del-despliegue)

---

## Arquitectura de Despliegue

```
┌─────────────────────────────────────────────────────────────┐
│                      Render.com                              │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────┐     ┌──────────────────┐              │
│  │   Static Site    │────▶│   Web Service    │              │
│  │  (React SPA)     │     │ (Spring Boot)    │              │
│  │                  │     │                  │              │
│  │  Puerto: 443     │     │  Puerto: 8080    │              │
│  └──────────────────┘     └────────┬─────────┘              │
│                                    │                         │
│                           ┌────────▼─────────┐              │
│                           │   PostgreSQL     │              │
│                           │   (Managed DB)   │              │
│                           └──────────────────┘              │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Componentes

| Componente | Tipo en Render | Descripción |
|------------|----------------|-------------|
| Frontend React | Static Site | SPA servida como contenido estático |
| Backend Spring Boot | Web Service | API REST con Java 17 |
| Base de Datos | PostgreSQL | BD gestionada por Render |

---

## Cambios en el Proyecto

### 1. Dependencias Maven (`pom.xml`)

Añadir el driver de PostgreSQL para producción y Spring Boot Actuator:

```xml
<!-- PostgreSQL Driver (para producción) -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Spring Boot Actuator (monitorización) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> **Nota**: H2 se mantiene para desarrollo. El driver a usar se determina por el perfil activo.

### 2. Perfiles de Configuración

#### Desarrollo (por defecto): `application.properties`

Se mantiene la configuración actual con H2. Se añaden propiedades para CORS parametrizable:

```properties
# CORS (desarrollo)
cors.allowed-origins=http://localhost:3000,http://localhost:5173

# Spring Boot Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when_authorized
management.endpoint.health.probes.enabled=true
```

#### Producción: `application-prod.properties`

Crear nuevo archivo con configuración para PostgreSQL y producción:

```properties
# Database Configuration (PostgreSQL para producción)
spring.datasource.url=${DATABASE_URL}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate (producción - solo validación, NO auto-crear)
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Inicialización de BD con scripts SQL
spring.sql.init.mode=always
spring.sql.init.platform=postgresql
spring.sql.init.continue-on-error=true

# H2 Console deshabilitada en producción
spring.h2.console.enabled=false

# JWT (desde variable de entorno)
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}

# CORS (desde variable de entorno)
cors.allowed-origins=${CORS_ALLOWED_ORIGINS}

# Logging (menos verboso en producción)
logging.level.es.uvigo.mei.facturaaas=INFO
logging.level.org.springframework.security=WARN

# Admin inicial (desde variables de entorno)
admin.password=${ADMIN_PASSWORD}

# Actuator (restringido en producción)
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=when_authorized
```

> **Importante**: Se usa `ddl-auto=validate` en lugar de `update` para evitar modificaciones automáticas del esquema. El esquema se crea mediante scripts SQL (`schema-postgresql.sql`).

### 3. Configuración CORS Parametrizable

Modificar `SecurityConfig.java` para leer orígenes permitidos desde propiedades:

```java
@Value("${cors.allowed-origins:http://localhost:3000}")
private String allowedOrigins;

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### 4. Inicializadores Condicionales

#### DataInitializer.java (solo desarrollo)

Añadir anotación `@Profile` para que solo se ejecute en desarrollo:

```java
@Component
@Profile("!prod")  // Solo se ejecuta si NO está activo el perfil "prod"
public class DataInitializer implements CommandLineRunner {
    // ... código existente ...
}
```

#### AdminInitializer.java (solo producción)

Crear nuevo inicializador para producción que crea solo el usuario admin:

```java
@Component
@Profile("prod")  // Solo se ejecuta con perfil "prod"
public class AdminInitializer implements CommandLineRunner {
    
    @Value("${admin.password}")
    private String adminPassword;
    
    // Inyectar UsuarioRepository y PasswordEncoder
    // Crear usuario admin si no existe
}
```

### 5. HomeController.java (solo desarrollo)

El controlador `HomeController` proporciona endpoints informativos (`/` y `/health`) útiles durante el desarrollo, pero innecesarios en producción (donde Actuator proporciona `/actuator/health`).

Añadir anotación `@Profile` para deshabilitarlo en producción:

```java
@RestController
@RequestMapping("/")
@Profile("!prod")  // Solo activo en desarrollo, deshabilitado en producción
public class HomeController {
    // ... código existente ...
}
```

> **Nota**: En producción, usar `/actuator/health` en lugar de `/health` para health checks.

---

## Variables de Entorno

### Variables Requeridas en Producción

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DATABASE_URL` | URL de conexión PostgreSQL (sin credenciales) | `jdbc:postgresql://host:5432/facturaaas` |
| `DATABASE_USERNAME` | Usuario de la base de datos | `facturaaas_user` |
| `DATABASE_PASSWORD` | Contraseña de la base de datos | `password123` |
| `JWT_SECRET` | Clave secreta para tokens JWT (mín. 256 bits) | `MiClaveSecretaMuyLargaYSegura...` |
| `ADMIN_PASSWORD` | Contraseña inicial del admin | `AdminPassword123!` |
| `CORS_ALLOWED_ORIGINS` | URLs del frontend permitidas | `https://mi-app.onrender.com` |
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring Boot | `prod` |

### Configuración de BD en Render

Render proporciona las credenciales de PostgreSQL de dos formas:

1. **Internal Database URL**: URL completa con credenciales embebidas
   ```
   postgres://user:password@host:5432/database
   ```
   
2. **Credenciales separadas**: En el panel de la BD, sección "Connections"
   - **Host**: `dpg-xxx.render.com`
   - **Database**: `facturaaas`
   - **Username**: `facturaaas_user`
   - **Password**: `xxxxxxxx`

Para Spring Boot, convertir la URL de Render al formato JDBC:
```
# Formato Render (postgres://)
postgres://user:pass@host:5432/db

# Formato JDBC (jdbc:postgresql://)
jdbc:postgresql://host:5432/db
```

### Variables de Entorno en Render

```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://dpg-xxx.render.com:5432/facturaaas
DATABASE_USERNAME=facturaaas_user
DATABASE_PASSWORD=<contraseña-de-render>
JWT_SECRET=<tu-clave-secreta-256-bits>
ADMIN_PASSWORD=<contraseña-segura-admin>
CORS_ALLOWED_ORIGINS=https://tu-frontend.onrender.com
```

---

## Preparación de Artefactos

### Backend (Spring Boot)

#### Opción A: Despliegue desde código fuente (recomendado en Render)

Render puede construir directamente desde el repositorio Git.

1. **Dockerfile** (opcional, si se prefiere Docker):

```dockerfile
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

2. **Build Command** para Render (sin Docker):
```bash
./mvnw clean package -DskipTests -Dspring.profiles.active=prod
```

3. **Start Command**:
```bash
java -jar target/facturaaas-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

#### Opción B: Subir JAR precompilado

```bash
# Compilar localmente
mvn clean package -DskipTests

# El artefacto estará en:
# target/facturaaas-0.0.1-SNAPSHOT.jar
```

### Frontend (React)

```bash
# En el directorio del proyecto React
npm install
npm run build

# Los archivos estáticos estarán en:
# dist/ o build/ (según configuración)
```

---

## Despliegue en Render.com

### Paso 1: Crear Base de Datos PostgreSQL

1. Ir a **Dashboard → New → PostgreSQL**
2. Configurar:
   - **Name**: `facturaaas-db`
   - **Database**: `facturaaas`
   - **User**: (generado automáticamente)
   - **Region**: (valor por defecto)
   - **Plan**: Free (para pruebas) o según necesidades
3. Crear y esperar aprovisionamiento
4. Copiar **Internal Database URL** para el backend

### Paso 2: Desplegar Backend (Web Service)

1. Ir a **Dashboard → New → Web Service**
2. Conectar repositorio Git o subir código
3. Configurar:
   - **Name**: `facturaaas-api`
   - **Region**: Misma que la BD
   - **Branch**: `main` o `master`
   - **Runtime**: `Java` o `Docker`
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar target/facturaaas-0.0.1-SNAPSHOT.jar`

4. **Variables de Entorno** (Settings → Environment):

```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://dpg-xxx.render.com:5432/facturaaas
DATABASE_USERNAME=facturaaas_user
DATABASE_PASSWORD=<contraseña-de-render>
JWT_SECRET=<tu-clave-secreta-256-bits>
ADMIN_PASSWORD=<contraseña-segura-admin>
CORS_ALLOWED_ORIGINS=https://tu-frontend.onrender.com
```

5. Desplegar

### Paso 3: Desplegar Frontend (Static Site)

1. Ir a **Dashboard → New → Static Site**
2. Conectar repositorio del frontend React
3. Configurar:
   - **Name**: `facturaaas-web`
   - **Branch**: `main`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `dist` (Vite) o `build` (CRA)

4. **Variables de Entorno** (para el build de React):

```
VITE_API_URL=https://facturaaas-api.onrender.com
# o para Create React App:
REACT_APP_API_URL=https://facturaaas-api.onrender.com
```

5. **Configurar Redirecciones para SPA** (crear archivo `_redirects` en `public/`):

```
/*    /index.html   200
```

O en Render, configurar **Rewrite Rules**:
- Source: `/*`
- Destination: `/index.html`
- Action: Rewrite

6. Desplegar

### Paso 4: Actualizar CORS del Backend

Una vez desplegado el frontend, actualizar la variable `CORS_ALLOWED_ORIGINS` del backend con la URL real del frontend:

```
CORS_ALLOWED_ORIGINS=https://facturaaas-web.onrender.com
```

---

## Verificación del Despliegue

### 1. Verificar Backend

```bash
# Health check
curl https://facturaaas-api.onrender.com/actuator/health

# Respuesta esperada:
# {"status":"UP","components":{"db":{"status":"UP"},...}}
```

### 2. Verificar Autenticación

```bash
# Login como admin
curl -X POST https://facturaaas-api.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"<ADMIN_PASSWORD>"}'

# Respuesta esperada:
# {"token":"eyJ...","login":"admin","rol":"ADMINISTRADOR"}
```

### 3. Verificar Frontend

1. Acceder a `https://facturaaas-web.onrender.com`
2. Verificar que carga la aplicación
3. Probar login con credenciales de admin

### 4. Endpoints de Actuator Disponibles

| Endpoint | URL | Descripción |
|----------|-----|-------------|
| Health | `/actuator/health` | Estado de la aplicación |
| Info | `/actuator/info` | Información de la aplicación |

---

## Actualizaciones y Mantenimiento

### Redespliegue Automático

Si conectaste un repositorio Git, Render redesplegará automáticamente con cada push a la rama configurada.

### Redespliegue Manual

1. Ir al servicio en Dashboard
2. Click en **Manual Deploy → Deploy latest commit**

### Logs

- Acceder desde Dashboard → Servicio → Logs
- O usar Render CLI: `render logs`

### Escalado

- **Vertical**: Cambiar plan del servicio
- **Horizontal**: No disponible en plan gratuito

---

## Consideraciones de Seguridad

1. **Nunca** commitear secretos (JWT_SECRET, passwords) al repositorio
2. Usar contraseñas **fuertes** para admin y BD
3. Revisar que `spring.jpa.hibernate.ddl-auto=validate` no cause problemas
4. En producción real, considerar migraciones con Flyway/Liquibase
5. Configurar **HTTPS** (Render lo proporciona automáticamente)
6. Limitar endpoints de Actuator expuestos

---

## Spring Boot Actuator

Spring Boot Actuator proporciona endpoints para monitorización y gestión de la aplicación en producción.

### Configuración por Entorno

| Entorno | Endpoints Expuestos | Detalles de Health |
|---------|---------------------|-------------------|
| Desarrollo | `health`, `info`, `metrics`, `env` | Siempre visibles |
| Producción | `health`, `info` | Solo autenticados |

### Endpoints Disponibles

| Endpoint | URL | Descripción |
|----------|-----|-------------|
| Health | `/actuator/health` | Estado de la aplicación y componentes (BD, disco, etc.) |
| Info | `/actuator/info` | Información de la aplicación (versión, descripción) |
| Metrics | `/actuator/metrics` | Métricas de rendimiento (solo desarrollo) |
| Env | `/actuator/env` | Variables de entorno (solo desarrollo) |

### Ejemplo de Respuesta Health

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

### Uso en Render.com

Render utiliza el endpoint `/actuator/health` para:
- **Health Checks**: Verificar que la aplicación está funcionando
- **Readiness Probes**: Determinar cuándo la aplicación está lista para recibir tráfico
- **Liveness Probes**: Detectar si la aplicación necesita reiniciarse

Configurar en Render (Settings → Health Check Path):
```
/actuator/health
```

### Seguridad

En producción, los endpoints de Actuator están restringidos:
- Solo `health` e `info` están expuestos
- Detalles de health requieren autenticación (`show-details=when_authorized`)
- Endpoints sensibles (`env`, `metrics`) están deshabilitados

---

## Estructura de Archivos Modificados/Nuevos

```
facturaaas-25/
├── pom.xml                                    # + dependencia PostgreSQL
├── Dockerfile                                 # (nuevo, opcional)
├── src/main/
│   ├── java/.../config/
│   │   ├── SecurityConfig.java               # + CORS parametrizable
│   │   ├── DataInitializer.java              # + @Profile("!prod")
│   │   ├── AdminInitializer.java             # (nuevo)
│   │   └── HomeController.java               # + @Profile("!prod")
│   └── resources/
│       ├── application.properties            # + cors.allowed-origins
│       ├── application-prod.properties       # (nuevo)
│       ├── schema-postgresql.sql             # (nuevo) - DDL
│       └── data-postgresql.sql               # (nuevo) - Datos iniciales
└── DESPLIEGUE.md                             # (este documento)
```

---

## Troubleshooting

### Error: "Connection refused" a PostgreSQL

- Verificar que se usa **Internal Database URL** (no External)
- Verificar que BD y Web Service están en la misma región

### Error: "CORS policy blocked"

- Verificar que `CORS_ALLOWED_ORIGINS` incluye la URL exacta del frontend
- No incluir `/` al final de las URLs

### Error: "Table doesn't exist" o "relation does not exist"

- Verificar que `spring.sql.init.mode=always` está configurado
- Revisar logs para errores en `schema-postgresql.sql`
- Ejecutar manualmente el script si es necesario:
  ```bash
  psql $DATABASE_URL -f schema-postgresql.sql
  ```

### Error: "Schema validation failed"

- El esquema de la BD no coincide con las entidades JPA
- Revisar que `schema-postgresql.sql` está actualizado
- Comparar estructura de tablas con las entidades

### Aplicación muy lenta o se "duerme"

- Plan gratuito de Render "duerme" servicios inactivos después de 15 min
- Primera request después de inactividad tarda ~30s en despertar
- Considerar plan pago para evitar esto

---

**Documento creado**: Diciembre 2025  
**Versión**: 1.0
