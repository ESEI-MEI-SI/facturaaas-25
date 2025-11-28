# Controles de Seguridad Basados en Recursos

## 📋 Implementación

Se han añadido controles de seguridad a nivel de método usando `@PreAuthorize` con SpEL (Spring Expression Language) para garantizar que cada usuario solo pueda acceder a sus propios recursos.

## 🔒 ResourceSecurityService

Se ha creado un componente Spring (`@Component("resourceSecurity")`) que centraliza toda la lógica de verificación de propiedad de recursos.

### Métodos Principales

#### Verificación de Usuario
```java
// Verifica si el usuarioId corresponde al usuario autenticado
public boolean isOwner(Long usuarioId)

// Verifica si el usuario es ADMINISTRADOR
public boolean isAdmin()

// Verifica si puede acceder (es propietario o admin)
public boolean canAccess(Long usuarioId)
```

#### Verificación de Cliente
```java
// Verifica si el cliente pertenece al usuario autenticado
public boolean isClienteOwner(Long clienteId)

// Verifica acceso (propietario o admin)
public boolean canAccessCliente(Long clienteId)
```

#### Verificación de Factura
```java
// Verifica si la factura pertenece al usuario autenticado
public boolean isFacturaOwner(Long facturaId)

// Verifica acceso (propietario o admin)
public boolean canAccessFactura(Long facturaId)
```

#### Verificación de FormaPago
```java
// Verifica si la forma de pago pertenece al usuario autenticado
public boolean isFormaPagoOwner(Long formaPagoId)

// Verifica acceso (propietario o admin)
public boolean canAccessFormaPago(Long formaPagoId)
```

#### Verificación de Pago
```java
// Verifica si el pago pertenece al usuario autenticado (vía factura)
public boolean isPagoOwner(Long pagoId)

// Verifica acceso (propietario o admin)
public boolean canAccessPago(Long pagoId)
```

## 🛡️ Anotaciones @PreAuthorize en Controllers

### ClienteController

```java
@RestController
@PreAuthorize("hasRole('USUARIO')")  // Requiere rol USUARIO para toda la clase
public class ClienteController {
    
    // Listar clientes - verifica que usuarioId sea del usuario autenticado
    @GetMapping
    @PreAuthorize("@resourceSecurity.canAccess(#usuarioId)")
    public ResponseEntity<List<ClienteDTO>> listar(@RequestParam Long usuarioId, ...)
    
    // Obtener cliente por ID - verifica que el cliente pertenezca al usuario
    @GetMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessCliente(#id)")
    public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable Long id)
    
    // Crear cliente - verifica que el usuarioId del DTO sea del usuario autenticado
    @PostMapping
    @PreAuthorize("@resourceSecurity.canAccess(#dto.usuarioId)")
    public ResponseEntity<ClienteDTO> crear(@RequestBody ClienteDTO dto)
    
    // Actualizar cliente - verifica que el cliente pertenezca al usuario
    @PutMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessCliente(#id)")
    public ResponseEntity<ClienteDTO> actualizar(@PathVariable Long id, ...)
}
```

### FacturaController

```java
@RestController
@PreAuthorize("hasRole('USUARIO')")
public class FacturaController {
    
    @GetMapping
    @PreAuthorize("@resourceSecurity.canAccess(#usuarioId)")
    public ResponseEntity<List<FacturaDTO>> listar(@RequestParam Long usuarioId, ...)
    
    @GetMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessFactura(#id)")
    public ResponseEntity<FacturaDTO> obtenerPorId(@PathVariable Long id)
    
    @PostMapping
    @PreAuthorize("@resourceSecurity.canAccess(#dto.usuarioId)")
    public ResponseEntity<FacturaDTO> crear(@RequestBody FacturaDTO dto)
    
    @PutMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessFactura(#id)")
    public ResponseEntity<FacturaDTO> actualizar(@PathVariable Long id, ...)
    
    @PostMapping("/{id}/generar-pagos")
    @PreAuthorize("@resourceSecurity.canAccessFactura(#id)")
    public ResponseEntity<Void> generarPagos(@PathVariable Long id)
}
```

### FormaPagoController

```java
@RestController
@PreAuthorize("hasRole('USUARIO')")
public class FormaPagoController {
    
    @GetMapping
    @PreAuthorize("@resourceSecurity.canAccess(#usuarioId)")
    public ResponseEntity<List<FormaPagoDTO>> listar(@RequestParam Long usuarioId, ...)
    
    @GetMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessFormaPago(#id)")
    public ResponseEntity<FormaPagoDTO> obtenerPorId(@PathVariable Long id)
    
    @PostMapping
    @PreAuthorize("@resourceSecurity.canAccess(#dto.usuarioId)")
    public ResponseEntity<FormaPagoDTO> crear(@RequestBody FormaPagoDTO dto)
    
    @PutMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessFormaPago(#id)")
    public ResponseEntity<FormaPagoDTO> actualizar(@PathVariable Long id, ...)
    
    @DeleteMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessFormaPago(#id)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id)
}
```

### PagoController

```java
@RestController
@PreAuthorize("hasRole('USUARIO')")
public class PagoController {
    
    @GetMapping
    @PreAuthorize("@resourceSecurity.canAccess(#usuarioId)")
    public ResponseEntity<List<PagoDTO>> listar(@RequestParam Long usuarioId, ...)
    
    @GetMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessPago(#id)")
    public ResponseEntity<PagoDTO> obtenerPorId(@PathVariable Long id)
    
    @PatchMapping("/{id}/estado")
    @PreAuthorize("@resourceSecurity.canAccessPago(#id)")
    public ResponseEntity<PagoDTO> actualizarEstado(@PathVariable Long id, ...)
}
```

## 🎯 Comportamiento

### Usuario Normal
- ✅ Puede acceder **solo** a sus propios recursos
- ❌ **No puede** acceder a recursos de otros usuarios
- ✅ Puede crear recursos solo para sí mismo

### Administrador
- ✅ Puede acceder a **todos** los recursos de todos los usuarios
- ✅ Bypass completo de las restricciones de propiedad

## 🧪 Ejemplos de Prueba

### Caso 1: Usuario accede a sus propios clientes ✅

```bash
TOKEN_USER2=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"user","password":"user123"}' \
  | jq -r '.token')

# Usuario ID 2 accediendo a sus clientes - PERMITIDO
curl -s "http://localhost:8080/api/clientes?usuarioId=2" \
  -H "Authorization: Bearer $TOKEN_USER2"
```

**Resultado:** ✅ **200 OK** - Lista de clientes

### Caso 2: Usuario intenta acceder a clientes de otro usuario ❌

```bash
# Usuario ID 2 intentando acceder a clientes del usuario ID 1 - DENEGADO
curl -s "http://localhost:8080/api/clientes?usuarioId=1" \
  -H "Authorization: Bearer $TOKEN_USER2"
```

**Resultado:** ❌ **403 Forbidden** - Access Denied

### Caso 3: Usuario intenta acceder a un cliente específico de otro usuario ❌

```bash
# Suponiendo que el cliente ID 5 pertenece al usuario ID 1
# Usuario ID 2 intenta acceder - DENEGADO
curl -s "http://localhost:8080/api/clientes/5" \
  -H "Authorization: Bearer $TOKEN_USER2"
```

**Resultado:** ❌ **403 Forbidden** - Access Denied

### Caso 4: Usuario intenta crear un cliente para otro usuario ❌

```bash
# Usuario ID 2 intenta crear un cliente para el usuario ID 1 - DENEGADO
curl -s -X POST http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN_USER2" \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "nombre": "Cliente Malicioso",
    "nif": "B99999999",
    ...
  }'
```

**Resultado:** ❌ **403 Forbidden** - Access Denied

### Caso 5: Administrador accede a cualquier recurso ✅

```bash
TOKEN_ADMIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"admin123"}' \
  | jq -r '.token')

# Admin accediendo a clientes de cualquier usuario - PERMITIDO
curl -s "http://localhost:8080/api/clientes?usuarioId=1" \
  -H "Authorization: Bearer $TOKEN_ADMIN"

curl -s "http://localhost:8080/api/clientes?usuarioId=2" \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

**Resultado:** ✅ **200 OK** - Lista de clientes (ambos casos)

## 🔐 Ventajas de esta Implementación

### 1. **Centralización**
- Toda la lógica de seguridad en un solo lugar (`ResourceSecurityService`)
- Fácil mantenimiento y actualización

### 2. **Mínimas Modificaciones en Controllers**
- Solo se añadieron anotaciones `@PreAuthorize`
- No se modificó la lógica de negocio
- Separación clara de responsabilidades

### 3. **Expresividad**
- Las anotaciones son auto-documentadas
- Fácil de entender qué protege cada endpoint

### 4. **Reutilización**
- Los métodos de verificación se pueden usar en otros lugares
- Consistencia en toda la aplicación

### 5. **Extensibilidad**
- Fácil añadir nuevas verificaciones
- Fácil cambiar reglas de negocio

### 6. **Performance**
- Las verificaciones son rápidas (solo consultas por ID)
- Uso de `FetchType.LAZY` evita cargas innecesarias

## 📊 Flujo de Seguridad

```
1. Usuario hace request
   ↓
2. JwtAuthenticationFilter valida token
   ↓
3. @PreAuthorize evalúa expresión SpEL
   ↓
4. ResourceSecurityService verifica propiedad
   ↓
5a. Si OK → Controller ejecuta lógica
5b. Si NO → 403 Forbidden
```

## 🔍 Verificación en Base de Datos

Las verificaciones realizan consultas eficientes:

```java
// Para Cliente
SELECT c FROM Cliente c WHERE c.id = :id
→ Verifica: c.usuario.id == authenticatedUserId

// Para Factura
SELECT f FROM Factura f WHERE f.id = :id
→ Verifica: f.usuario.id == authenticatedUserId

// Para FormaPago
SELECT fp FROM FormaPago fp WHERE fp.id = :id
→ Verifica: fp.usuario.id == authenticatedUserId

// Para Pago
SELECT p FROM Pago p WHERE p.id = :id
→ Verifica: p.factura.usuario.id == authenticatedUserId
```

## ✅ Resumen

- ✅ **Seguridad implementada** a nivel de Spring Security
- ✅ **Mínimas modificaciones** en controllers (solo anotaciones)
- ✅ **Centralización** de lógica de seguridad
- ✅ **Protección completa** de Cliente, Factura, FormaPago y Pago
- ✅ **Soporte para Administradores** (bypass de restricciones)
- ✅ **Compilación exitosa** sin errores

---

**Fecha:** 30 de octubre de 2025  
**Estado:** ✅ Implementación completada y verificada
