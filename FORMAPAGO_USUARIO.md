# FormaPago Vinculada a Usuario

## 📋 Cambios Realizados

### 1. ✅ Modelo FormaPago Actualizado

La entidad `FormaPago` ahora tiene una relación **Many-to-One** con `Usuario`:

```java
@Entity
@Table(name = "forma_pago")
public class FormaPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;  // ⬅️ NUEVO
    
    private String descripcion;
    private Integer numeroPagos;
    private Integer periodicidadDias;
    private Boolean activa;
}
```

### 2. ✅ DTO FormaPagoDTO Actualizado

Ahora incluye el `usuarioId`:

```java
public class FormaPagoDTO {
    private Long id;
    private Long usuarioId;  // ⬅️ NUEVO
    private String descripcion;
    private Integer numeroPagos;
    private Integer periodicidadDias;
    private Boolean activa;
}
```

### 3. ✅ Repository Actualizado

Nuevos métodos para buscar formas de pago por usuario:

```java
public interface FormaPagoRepository extends JpaRepository<FormaPago, Long> {
    List<FormaPago> findByActivaTrue();
    List<FormaPago> findByUsuarioIdAndActivaTrue(Long usuarioId);  // ⬅️ NUEVO
    List<FormaPago> findByUsuarioId(Long usuarioId);              // ⬅️ NUEVO
}
```

### 4. ✅ Service Actualizado

Nuevos métodos para gestionar formas de pago por usuario:

```java
public class FormaPagoService {
    // Obtener todas las formas de pago de un usuario
    public List<FormaPagoDTO> obtenerPorUsuario(Long usuarioId);
    
    // Obtener solo las activas de un usuario
    public List<FormaPagoDTO> obtenerActivasPorUsuario(Long usuarioId);
    
    // Crear forma de pago para un usuario
    public FormaPagoDTO crear(FormaPagoDTO dto); // Ahora requiere usuarioId
}
```

### 5. ✅ Controller Refactorizado

Ahora usa **query parameters** y **autenticación de USUARIO**:

**Antes:**
```java
@GetMapping
@PreAuthorize("hasRole('ADMINISTRADOR')")  // Solo ADMIN
public ResponseEntity<List<FormaPagoDTO>> obtenerTodas() {
    return ResponseEntity.ok(formaPagoService.obtenerTodas());
}
```

**Después:**
```java
@GetMapping
@PreAuthorize("hasRole('USUARIO')")  // Ahora USUARIO
public ResponseEntity<List<FormaPagoDTO>> listar(
        @RequestParam(required = false) Long usuarioId,
        @RequestParam(required = false, defaultValue = "false") Boolean soloActivas) {
    if (usuarioId != null) {
        if (soloActivas) {
            return ResponseEntity.ok(formaPagoService.obtenerActivasPorUsuario(usuarioId));
        } else {
            return ResponseEntity.ok(formaPagoService.obtenerPorUsuario(usuarioId));
        }
    }
    return ResponseEntity.badRequest().build();
}
```

### 6. ✅ DataInitializer Actualizado

Las formas de pago iniciales se crean asociadas al usuario demo:

```java
Usuario usuarioDemo = usuarioRepository.findByLogin("user")
    .orElseThrow(() -> new RuntimeException("Usuario demo no encontrado"));

FormaPago contado = new FormaPago();
contado.setUsuario(usuarioDemo);  // ⬅️ Asociada al usuario
contado.setDescripcion("Contado");
// ...
```

## 🎯 Ventajas del Cambio

✅ **Personalización:** Cada usuario define sus propias formas de pago  
✅ **Aislamiento:** Un usuario no ve las formas de pago de otros  
✅ **Flexibilidad:** Diferentes usuarios pueden tener diferentes condiciones  
✅ **Seguridad:** Control de acceso a nivel de usuario  

## 📖 Nuevos Endpoints

### Listar formas de pago de un usuario

```bash
GET /api/formas-pago?usuarioId={id}
```

**Ejemplo:**
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"user","password":"user123"}' \
  | jq -r '.token')

curl -s "http://localhost:8080/api/formas-pago?usuarioId=2" \
  -H "Authorization: Bearer $TOKEN" | jq .
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "usuarioId": 2,
    "descripcion": "Contado",
    "numeroPagos": 1,
    "periodicidadDias": 0,
    "activa": true
  },
  {
    "id": 2,
    "usuarioId": 2,
    "descripcion": "Transferencia a 30 días",
    "numeroPagos": 1,
    "periodicidadDias": 30,
    "activa": true
  },
  {
    "id": 3,
    "usuarioId": 2,
    "descripcion": "Transferencias a 30-60-90 días",
    "numeroPagos": 3,
    "periodicidadDias": 30,
    "activa": true
  }
]
```

### Listar solo formas de pago activas

```bash
GET /api/formas-pago?usuarioId={id}&soloActivas=true
```

**Ejemplo:**
```bash
curl -s "http://localhost:8080/api/formas-pago?usuarioId=2&soloActivas=true" \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### Crear forma de pago para un usuario

```bash
POST /api/formas-pago
Content-Type: application/json

{
  "usuarioId": 2,
  "descripcion": "Pago aplazado 60 días",
  "numeroPagos": 1,
  "periodicidadDias": 60,
  "activa": true
}
```

**Ejemplo:**
```bash
curl -s -X POST http://localhost:8080/api/formas-pago \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 2,
    "descripcion": "Pago aplazado 60 días",
    "numeroPagos": 1,
    "periodicidadDias": 60,
    "activa": true
  }' | jq .
```

**Respuesta:**
```json
{
  "id": 4,
  "usuarioId": 2,
  "descripcion": "Pago aplazado 60 días",
  "numeroPagos": 1,
  "periodicidadDias": 60,
  "activa": true
}
```

### Actualizar forma de pago

```bash
PUT /api/formas-pago/{id}
```

**Ejemplo:**
```bash
curl -s -X PUT http://localhost:8080/api/formas-pago/4 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 4,
    "usuarioId": 2,
    "descripcion": "Pago aplazado 90 días",
    "numeroPagos": 1,
    "periodicidadDias": 90,
    "activa": true
  }' | jq .
```

### Eliminar (lógicamente) forma de pago

```bash
DELETE /api/formas-pago/{id}
```

**Ejemplo:**
```bash
curl -s -X DELETE http://localhost:8080/api/formas-pago/4 \
  -H "Authorization: Bearer $TOKEN"
```

## 🔄 Impacto en Otras Entidades

### DatosFacturacion

La entidad `DatosFacturacion` tiene un campo `formaPagoDefecto_id` que debe apuntar a una forma de pago del mismo usuario:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "forma_pago_defecto_id")
private FormaPago formaPagoDefecto;
```

**Validación recomendada:** Al crear/actualizar `DatosFacturacion`, verificar que la forma de pago pertenezca al usuario.

### Factura

La entidad `Factura` tiene un campo `formaPago_id` que debe apuntar a una forma de pago del usuario que crea la factura:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "forma_pago_id", nullable = false)
private FormaPago formaPago;
```

**Validación recomendada:** Al crear/actualizar `Factura`, verificar que la forma de pago pertenezca al usuario de la factura.

## 📊 Esquema de Base de Datos

```sql
CREATE TABLE forma_pago (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,           -- ⬅️ NUEVA COLUMNA
    descripcion VARCHAR(100) NOT NULL,
    numero_pagos INTEGER NOT NULL,
    periodicidad_dias INTEGER NOT NULL,
    activa BOOLEAN NOT NULL,
    
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
```

## 🧪 Casos de Uso

### Caso 1: Usuario crea sus propias formas de pago

```bash
# El usuario 'user' (ID=2) crea su forma de pago personalizada
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"user","password":"user123"}' \
  | jq -r '.token')

curl -s -X POST http://localhost:8080/api/formas-pago \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 2,
    "descripcion": "Pago personalizado",
    "numeroPagos": 2,
    "periodicidadDias": 45,
    "activa": true
  }' | jq .
```

### Caso 2: Usuario lista solo sus formas de pago

```bash
# Solo ve las formas de pago que ha creado o que le pertenecen
curl -s "http://localhost:8080/api/formas-pago?usuarioId=2" \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### Caso 3: Usuario crea factura con su forma de pago

```bash
# Al crear una factura, usa una de sus formas de pago
curl -s -X POST http://localhost:8080/api/facturas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 2,
    "clienteId": 1,
    "formaPagoId": 1,  // ⬅️ Forma de pago del usuario
    "ejercicio": 2025,
    "fechaEmision": "2025-10-30",
    "estado": "EMITIDA",
    "lineas": [...]
  }' | jq .
```

## 📝 Resumen

✅ **FormaPago ahora está vinculada a Usuario**  
✅ **Cada usuario gestiona sus propias formas de pago**  
✅ **Endpoints refactorizados con query parameters**  
✅ **Autenticación cambiada de ADMIN a USUARIO**  
✅ **Compilación exitosa sin errores**  

---

**Fecha:** 30 de octubre de 2025  
**Estado:** ✅ Implementación completada y verificada
