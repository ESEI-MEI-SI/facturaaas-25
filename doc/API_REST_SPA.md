# API REST - FACTURAaaS (Guía Frontend SPA)

Referencia técnica agnóstica para consumo de API REST (independiente del cliente HTTP: fetch, axios, etc.)

---

## 🌍 Información de Conexión

| Parámetro | Valor |
|-----------|-------|
| **URL Base** | `http://localhost:8080` |
| **Protocolo** | HTTP REST |
| **Content-Type** | `application/json` |
| **Autenticación** | JWT Bearer Token |
| **Durabilidad Token** | 24 horas |

---

## 🔑 Autenticación

### POST /api/auth/login

**Acceso**: Público

**Request**:
```json
{
  "login": "string",
  "password": "string"
}
```

**Response** (200 OK):
```json
{
  "token": "eyJ...",
  "usuario": {
    "id": 0,
    "login": "string",
    "nombre": "string",
    "rol": "ADMINISTRADOR | USUARIO",
    "email": "string",
    "activo": true
  }
}
```

**Usuarios de prueba**:

| Login | Password | Rol |
|-------|----------|-----|
| `admin` | `admin123` | ADMINISTRADOR |
| `user` | `user123` | USUARIO |

**Gestión del token**:
- Token se retorna en respuesta de login
- Almacenar en cliente (localStorage, sessionStorage, o memoria)
- Incluir en header: `Authorization: Bearer <token>` en solicitudes posteriores
- Válido 24 horas desde emisión

---

## 📊 Endpoints por Rol

### ADMINISTRADOR

| Método | Endpoint | Descripción |
|--------|----------|------------|
| GET | `/api/usuarios` | Listar usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario |
| POST | `/api/usuarios` | Crear usuario |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |
| GET | `/api/tipos-iva` | Listar tipos IVA |
| GET | `/api/tipos-iva/{id}` | Obtener tipo IVA |
| POST | `/api/tipos-iva` | Crear tipo IVA |
| PUT | `/api/tipos-iva/{id}` | Actualizar tipo IVA |
| DELETE | `/api/tipos-iva/{id}` | Eliminar tipo IVA |

### USUARIO (acceso a recursos propios)

| Método | Endpoint | Descripción |
|--------|----------|------------|
| GET | `/api/clientes?usuarioId={id}` | Listar clientes |
| GET | `/api/clientes/{id}` | Obtener cliente |
| POST | `/api/clientes` | Crear cliente |
| PUT | `/api/clientes/{id}` | Actualizar cliente |
| GET | `/api/formas-pago?usuarioId={id}` | Listar formas pago |
| GET | `/api/formas-pago/{id}` | Obtener forma pago |
| POST | `/api/formas-pago` | Crear forma pago |
| PUT | `/api/formas-pago/{id}` | Actualizar forma pago |
| DELETE | `/api/formas-pago/{id}` | Eliminar forma pago |
| GET | `/api/tipos-iva/activos` | Listar IVA activos |
| GET | `/api/facturas?usuarioId={id}` | Listar facturas |
| GET | `/api/facturas/{id}` | Obtener factura |
| POST | `/api/facturas` | Crear factura |
| PUT | `/api/facturas/{id}` | Actualizar factura |
| POST | `/api/facturas/{id}/generar-pagos` | Generar pagos |
| GET | `/api/pagos?usuarioId={id}` | Listar pagos |
| GET | `/api/pagos/{id}` | Obtener pago |
| PATCH | `/api/pagos/{id}/estado` | Actualizar estado pago |

---

## 🔄 Flujos Principales

### 1. Autenticación

1. Enviar `POST /api/auth/login` con credenciales (`login`, `password`)
2. Recibir token JWT en respuesta
3. Almacenar token en cliente
4. Incluir token en header `Authorization: Bearer <token>` en solicitudes posteriores

### 2. Listar Recursos de Usuario

- Endpoint: `GET /api/clientes?usuarioId={id}` (y similares)
- Parámetro `usuarioId` requerido = ID del usuario autenticado
- Parámetro `patron` opcional = búsqueda por nombre (case-insensitive)
- El backend valida automáticamente que el usuario sea propietario

### 3. Crear Recurso

- Endpoint: `POST /api/clientes` (y similares)
- Incluir DTO con datos del nuevo recurso
- Campo `usuarioId` en body debe coincidir con usuario autenticado
- Response retorna recurso con ID asignado

### 4. Generar Pagos de Factura

- Endpoint: `POST /api/facturas/{id}/generar-pagos`
- Precondición: Factura debe existir y pertenecer al usuario
- Backend calcula vencimientos según FormaPago
- Crea N pagos distribuidos en el tiempo según periodicidad

### 5. Actualizar Estado de Pago

- Endpoint: `PATCH /api/pagos/{id}/estado?estado=PAGADO`
- Query parameter `estado` puede ser: `PAGADO`, `PENDIENTE`, `ANULADO`
- Response retorna Pago DTO actualizado

---

## 📦 Estructuras de Datos (DTOs)

### LoginDTO

| Campo | Tipo | Requerido |
|-------|------|-----------|
| `login` | String | Sí |
| `password` | String | Sí |

### ClienteDTO

| Campo | Tipo | Requerido | Notas |
|-------|------|-----------|-------|
| `id` | Long | No | Solo respuesta |
| `nombre` | String | Sí | |
| `nif` | String | Sí | |
| `domicilio` | String | Sí | |
| `localidad` | String | Sí | |
| `codigoPostal` | String | Sí | |
| `provincia` | String | Sí | |
| `email` | String | Sí | |
| `telefono` | String | Sí | |
| `cuentaBancaria` | String | No | |
| `usuarioId` | Long | Sí | Propietario |

### FormaPagoDTO

| Campo | Tipo | Requerido | Notas |
|-------|------|-----------|-------|
| `id` | Long | No | Solo respuesta |
| `descripcion` | String | Sí | |
| `numeroPagos` | Int | Sí | Cantidad de pagos |
| `periodicidadDias` | Int | Sí | Días entre pagos |
| `activa` | Boolean | Sí | |
| `usuarioId` | Long | Sí | Propietario |

### LineaFacturaDTO

| Campo | Tipo | Requerido | Notas |
|-------|------|-----------|-------|
| `numeroLinea` | Int | No | Solo respuesta |
| `concepto` | String | Sí | Descripción |
| `cantidad` | Decimal | Sí | > 0 |
| `precioUnitario` | Decimal | Sí | > 0, sin IVA |
| `porcentajeDescuento` | Decimal | No | 0-100, default 0 |
| `tipoIVAId` | Long | Sí | |
| `importeTotal` | Decimal | No | Calculado |

### FacturaDTO

| Campo | Tipo | Requerido | Notas |
|-------|------|-----------|-------|
| `id` | Long | No | Solo respuesta |
| `numeroFactura` | String | No | Auto-generado: YYYY-NNNN |
| `ejercicio` | Int | Sí | Año |
| `fechaEmision` | Date | Sí | YYYY-MM-DD |
| `estado` | String | Sí | EstadoFactura enum |
| `comentarios` | String | No | |
| `usuarioId` | Long | Sí | Propietario |
| `clienteId` | Long | Sí | Cliente facturado (del usuario) |
| `formaPagoId` | Long | Sí | Forma pago (del usuario) |
| `lineas` | LineaFacturaDTO[] | Sí | Array, mínimo 1 |
| `pagos` | PagoDTO[] | No | Solo respuesta |
| `importeTotal` | Decimal | No | Calculado, sin IVA |
| `ivaTotal` | Decimal | No | Calculado |
| `sumaTotal` | Decimal | No | Calculado, con IVA |

### PagoDTO

| Campo | Tipo | Requerido | Notas |
|-------|------|-----------|-------|
| `id` | Long | No | Solo respuesta |
| `numeroPago` | Int | No | Solo respuesta, secuencia 1..N |
| `fechaVencimiento` | Date | No | Calculado, YYYY-MM-DD |
| `importe` | Decimal | No | Calculado |
| `estado` | String | Sí | EstadoPago enum, default PENDIENTE |
| `fechaPago` | Date | No | Cuando se pagó |
| `facturaId` | Long | No | Solo respuesta |

### TipoIVADTO

| Campo | Tipo | Requerido |
|-------|------|-----------|
| `id` | Long | No |
| `descripcion` | String | Sí |
| `porcentaje` | Decimal | Sí |
| `activo` | Boolean | Sí |

---

## 🛡️ Códigos HTTP

| Código | Significado | Acción |
|--------|-----------|--------|
| **200** | OK | Éxito, procesar respuesta |
| **201** | Created | Recurso creado |
| **204** | No Content | Éxito, sin contenido |
| **400** | Bad Request | Validar datos enviados |
| **401** | Unauthorized | Token inválido/expirado → login requerido |
| **403** | Forbidden | Sin permisos → usuario no propietario o no es admin |
| **404** | Not Found | Recurso no existe |
| **500** | Server Error | Error del servidor |

---

## ✅ Validaciones

| Campo | Validación |
|-------|-----------|
| `email` | Formato válido |
| `telefono` | Dígitos solo |
| `nif` | Formato letra+números |
| `porcentajeDescuento` | 0 ≤ x ≤ 100 |
| `cantidad` | > 0 |
| `precioUnitario` | > 0 |
| `nombre` | No vacío |
| `descripcion` | No vacío |

---

## 🔌 Patrón de Interacción

### Estructura de Solicitud

```
{método} {baseURL}{endpoint}{?parámetros}
Headers:
  - Content-Type: application/json
  - Authorization: Bearer {token}
Body (si aplica):
  {JSON serializado}
```

### Headers Esenciales

| Header | Uso | Valor |
|--------|-----|-------|
| `Content-Type` | Solicitudes con body | `application/json` |
| `Authorization` | Endpoints protegidos | `Bearer <token_jwt>` |

### Construcción de Query Parameters

- Formato: `?param1=valor1&param2=valor2`
- Parámetros opcionales se omiten si no aplican
- Valores deben estar URL-encoded

---

## 🎯 Estados y Enumeraciones

### EstadoFactura

Valores válidos: `EMITIDA`, `ANULADA`, `PAGADA`, `RECLAMADA`, `ABONADA`

Transiciones recomendadas:
- `EMITIDA` (inicial) → `PAGADA` (pagos cobrados)
- `EMITIDA` → `ANULADA` (cancelada)
- `PAGADA` → `RECLAMADA` (impagos)
- Cualquiera → `ABONADA` (créditos/devoluciones)

### EstadoPago

Valores válidos: `PENDIENTE`, `PAGADO`, `ANULADO`

Transiciones:
- `PENDIENTE` (inicial) → `PAGADO` (cobrado)
- `PENDIENTE` → `ANULADO` (cancelado)

### TipoEntidad

Valores válidos: `PARTICULAR`, `AUTONOMO`, `SOCIEDAD_LIMITADA`, `SOCIEDAD_ANONIMA`, `COOPERATIVA`, `ONG`, `OTRO`

---

## 💡 Referencia Rápida

| Símbolo | Significado | Ejemplo |
|---------|-----------|---------|
| 🟢 | Público, sin token | POST /api/auth/login |
| 🟡 | Requiere token | GET /api/usuarios (admin) |
| 🔒 | Token + verificación recurso | GET /api/clientes/{id} (usuario) |
| 🔴 | Token + rol admin | GET /api/tipos-iva |

**Regla de oro**: Error 403 = usuario sin permisos o no propietario del recurso

---

**Actualización**: 5 de diciembre de 2025  
**Versión**: 1.0.0 | **Spring Boot**: 3.5.8 | **Java**: 17
