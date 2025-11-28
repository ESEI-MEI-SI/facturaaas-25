# FACTURAaaS Backend

Backend desarrollado en Spring Boot para la gestión de facturas FACTURAaaS.

## Tecnologías

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA** - Persistencia de datos
- **Spring Security** - Autenticación y autorización
- **JWT** - Tokens de autenticación
- **H2 Database** - Base de datos en memoria (desarrollo)
- **MySQL** - Base de datos (producción)
- **Lombok** - Reducción de código boilerplate
- **Maven** - Gestión de dependencias

## Arquitectura

El proyecto sigue una arquitectura en capas:

```
├── controller/     → Controladores REST (API endpoints)
├── service/        → Lógica de negocio
├── repository/     → Acceso a datos (Spring Data JPA)
├── model/          → Entidades JPA
├── dto/            → Data Transfer Objects
├── security/       → Configuración de seguridad y JWT
├── config/         → Configuración de Spring
└── exception/      → Manejo global de excepciones
```

## Características Implementadas

### Módulo de Administración (Rol: ADMINISTRADOR)
- ✅ H1-H4: Gestión de usuarios
- ✅ H5-H6: Gestión de tipos de IVA
- ✅ H7-H8: Gestión de formas de pago

### Módulo de Usuario (Rol: USUARIO)
- ✅ H9-H11: Home y perfil de usuario
- ✅ H12-H13: Gestión de clientes
- ✅ H14-H15: Gestión de facturas (Sprint 2)
- ✅ H16: Gestión de pagos (Sprint 2)

## API Endpoints

### Autenticación
- `POST /api/auth/login` - Login de usuario

### Usuarios (ADMIN)
- `GET /api/usuarios` - Listar todos
- `GET /api/usuarios/{id}` - Obtener por ID
- `POST /api/usuarios` - Crear usuario
- `PUT /api/usuarios/{id}` - Actualizar usuario
- `DELETE /api/usuarios/{id}` - Eliminar (lógico)

### Tipos de IVA (ADMIN)
- `GET /api/tipos-iva` - Listar todos
- `GET /api/tipos-iva/activos` - Listar activos
- `POST /api/tipos-iva` - Crear
- `PUT /api/tipos-iva/{id}` - Actualizar
- `DELETE /api/tipos-iva/{id}` - Eliminar (lógico)

### Formas de Pago (ADMIN)
- `GET /api/formas-pago` - Listar todas
- `GET /api/formas-pago/activas` - Listar activas
- `POST /api/formas-pago` - Crear
- `PUT /api/formas-pago/{id}` - Actualizar
- `DELETE /api/formas-pago/{id}` - Eliminar (lógico)

### Clientes (USER)
- `GET /api/clientes/usuario/{usuarioId}` - Listar por usuario
- `GET /api/clientes/usuario/{usuarioId}/buscar?patron={texto}` - Buscar
- `POST /api/clientes` - Crear
- `PUT /api/clientes/{id}` - Actualizar

### Facturas (USER)
- `GET /api/facturas/usuario/{usuarioId}` - Listar por usuario
- `GET /api/facturas/usuario/{usuarioId}/cliente/{clienteId}` - Filtrar por cliente
- `POST /api/facturas` - Crear factura
- `PUT /api/facturas/{id}` - Actualizar
- `POST /api/facturas/{id}/generar-pagos` - Generar pagos

### Pagos (USER)
- `GET /api/pagos/usuario/{usuarioId}` - Listar por usuario
- `PATCH /api/pagos/{id}/estado?estado={PENDIENTE|PAGADO|ANULADO}` - Cambiar estado

## Ejecución

### Requisitos
- Java 17 o superior
- Maven 3.6+

### Compilar y ejecutar

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

### Consola H2
Para acceder a la consola de H2 (desarrollo):
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:facturaaas`
- Usuario: `sa`
- Password: (dejar vacío)

## Usuarios de Prueba

Al iniciar la aplicación se crean automáticamente:

| Usuario | Password | Rol |
|---------|----------|-----|
| admin | admin123 | ADMINISTRADOR |
| user | user123 | USUARIO |

## Seguridad

- Autenticación mediante **JWT**
- Autorización basada en **roles** (ADMINISTRADOR, USUARIO)
- Contraseñas encriptadas con **BCrypt**
- CORS configurado para desarrollo

## Modelo de Datos

### Entidades principales:
- **Usuario** - Datos de usuario y credenciales
- **TipoIVA** - Tipos de IVA (21%, 10%, 4%, 0%)
- **FormaPago** - Formas de pago configurables
- **DatosFacturacion** - Datos fiscales del usuario
- **Cliente** - Clientes del usuario
- **Factura** - Facturas emitidas
- **LineaFactura** - Líneas de detalle de factura
- **Pago** - Vencimientos de pago

## Configuración

Editar `src/main/resources/application.properties` para:
- Cambiar puerto del servidor
- Configurar base de datos
- Ajustar configuración JWT
- Modificar niveles de logging

## Próximos Pasos

- [ ] Implementar DatosFacturacionService y Controller
- [ ] Añadir generación de PDF para facturas
- [ ] Implementar envío de emails
- [ ] Añadir tests unitarios e integración
- [ ] Documentación con Swagger/OpenAPI
- [ ] Dockerización

## Autor

Proyecto académico - FACTURAaaS
