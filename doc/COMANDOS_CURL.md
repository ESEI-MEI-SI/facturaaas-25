# Comandos curl para probar FACTURAaaS Backend

## 🔓 Endpoints Públicos (Sin autenticación)

### 1. Página de inicio (información de la API)
```bash
curl -X GET http://localhost:8080/
```

### 2. Health check
```bash
curl -X GET http://localhost:8080/health
```

### 3. Login como ADMINISTRADOR
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "admin",
    "password": "admin123"
  }'
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tipo": "Bearer",
  "usuario": {
    "id": 1,
    "login": "admin",
    "nombre": "Administrador",
    "email": "admin@facturaaas.com",
    "rol": "ADMINISTRADOR",
    "activo": true,
    "fechaCreacion": "2025-10-30T...",
    "fechaUltimoAcceso": null
  }
}
```

### 4. Login como USUARIO normal
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "user",
    "password": "user123"
  }'
```

---

## 🔒 Endpoints Protegidos (Requieren autenticación)

### Variable de entorno para el token
Primero, guarda el token en una variable:

```bash
# Login y guardar token de ADMIN
TOKEN_ADMIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"admin123"}' \
  | jq -r '.token')

echo "Token Admin: $TOKEN_ADMIN"

# Login y guardar token de USER
TOKEN_USER=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"user","password":"user123"}' \
  | jq -r '.token')

echo "Token User: $TOKEN_USER"
```

---

## 👥 Endpoints de ADMINISTRADOR

### Listar todos los usuarios
```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### Obtener un usuario por ID
```bash
curl -X GET http://localhost:8080/api/usuarios/1 \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### Crear un nuevo usuario
```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "login": "juan",
    "password": "juan123",
    "nombre": "Juan Pérez",
    "email": "juan@example.com"
  }'
```

### Actualizar un usuario
```bash
curl -X PUT http://localhost:8080/api/usuarios/3 \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 3,
    "login": "juan",
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "rol": "USUARIO",
    "activo": true
  }'
```

### Eliminar (lógicamente) un usuario
```bash
curl -X DELETE http://localhost:8080/api/usuarios/3 \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

---

## 📊 Tipos de IVA (ADMIN)

### Listar todos los tipos de IVA
```bash
curl -X GET http://localhost:8080/api/tipos-iva \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### Listar solo tipos de IVA activos (ADMIN o USER)
```bash
curl -X GET http://localhost:8080/api/tipos-iva/activos \
  -H "Authorization: Bearer $TOKEN_USER"
```

### Crear un tipo de IVA
```bash
curl -X POST http://localhost:8080/api/tipos-iva \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "IVA Especial",
    "porcentaje": 15.00,
    "activo": true
  }'
```

### Actualizar un tipo de IVA
```bash
curl -X PUT http://localhost:8080/api/tipos-iva/5 \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 5,
    "descripcion": "IVA Especial Modificado",
    "porcentaje": 12.50,
    "activo": true
  }'
```

### Eliminar (lógicamente) un tipo de IVA
```bash
curl -X DELETE http://localhost:8080/api/tipos-iva/5 \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

---

## 💳 Formas de Pago (ADMIN)

### Listar todas las formas de pago
```bash
curl -X GET http://localhost:8080/api/formas-pago \
  -H "Authorization: Bearer $TOKEN_ADMIN"
```

### Listar solo formas de pago activas (ADMIN o USER)
```bash
curl -X GET http://localhost:8080/api/formas-pago/activas \
  -H "Authorization: Bearer $TOKEN_USER"
```

### Crear una forma de pago
```bash
curl -X POST http://localhost:8080/api/formas-pago \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Transferencia a 60 días",
    "numeroPagos": 1,
    "periodicidadDias": 60,
    "activa": true
  }'
```

---

## 🏢 Clientes (USUARIO)

### Listar clientes de un usuario
```bash
curl -X GET "http://localhost:8080/api/clientes?usuarioId=2" \
  -H "Authorization: Bearer $TOKEN_USER"
```

### Buscar clientes por patrón
```bash
curl -X GET "http://localhost:8080/api/clientes?usuarioId=2&patron=acme" \
  -H "Authorization: Bearer $TOKEN_USER"
```

### Crear un cliente
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Authorization: Bearer $TOKEN_USER" \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 2,
    "nombre": "ACME Corporation",
    "nif": "B12345678",
    "domicilio": "Calle Principal 123",
    "localidad": "Madrid",
    "codigoPostal": "28001",
    "provincia": "Madrid",
    "email": "contacto@acme.com",
    "telefono": "912345678",
    "cuentaBancaria": "ES1234567890123456789012"
  }'
```

### Actualizar un cliente
```bash
curl -X PUT http://localhost:8080/api/clientes/1 \
  -H "Authorization: Bearer $TOKEN_USER" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "usuarioId": 2,
    "nombre": "ACME Corporation S.A.",
    "nif": "B12345678",
    "domicilio": "Calle Principal 123",
    "localidad": "Madrid",
    "codigoPostal": "28001",
    "provincia": "Madrid",
    "email": "info@acme.com",
    "telefono": "912345678",
    "cuentaBancaria": "ES1234567890123456789012"
  }'
```

---

## 📄 Facturas (USUARIO)

### Listar facturas de un usuario
```bash
curl -X GET "http://localhost:8080/api/facturas?usuarioId=2" \
  -H "Authorization: Bearer $TOKEN_USER"
```

### Filtrar facturas por cliente
```bash
curl -X GET "http://localhost:8080/api/facturas?usuarioId=2&clienteId=1" \
  -H "Authorization: Bearer $TOKEN_USER"
```

### Crear una factura con líneas
```bash
curl -X POST http://localhost:8080/api/facturas \
  -H "Authorization: Bearer $TOKEN_USER" \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 2,
    "clienteId": 1,
    "ejercicio": 2025,
    "fechaEmision": "2025-10-30",
    "formaPagoId": 1,
    "estado": "EMITIDA",
    "comentarios": "Factura de prueba",
    "lineas": [
      {
        "concepto": "Desarrollo web",
        "cantidad": 40,
        "precioUnitario": 50.00,
        "porcentajeDescuento": 0,
        "tipoIVAId": 1
      },
      {
        "concepto": "Consultoría",
        "cantidad": 10,
        "precioUnitario": 80.00,
        "porcentajeDescuento": 10,
        "tipoIVAId": 1
      }
    ]
  }'
```

### Obtener una factura por ID
```bash
curl -X GET http://localhost:8080/api/facturas/1 \
  -H "Authorization: Bearer $TOKEN_USER"
```

### Actualizar una factura
```bash
curl -X PUT http://localhost:8080/api/facturas/1 \
  -H "Authorization: Bearer $TOKEN_USER" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "numeroFactura": "2025/0001",
    "ejercicio": 2025,
    "usuarioId": 2,
    "clienteId": 1,
    "fechaEmision": "2025-10-30",
    "formaPagoId": 2,
    "estado": "EMITIDA",
    "comentarios": "Factura actualizada",
    "lineas": [
      {
        "concepto": "Desarrollo web actualizado",
        "cantidad": 50,
        "precioUnitario": 55.00,
        "porcentajeDescuento": 5,
        "tipoIVAId": 1
      }
    ]
  }'
```

### Generar pagos para una factura
```bash
curl -X POST http://localhost:8080/api/facturas/1/generar-pagos \
  -H "Authorization: Bearer $TOKEN_USER"
```

---

## 💰 Pagos (USUARIO)

### Listar pagos de un usuario
```bash
curl -X GET "http://localhost:8080/api/pagos?usuarioId=2" \
  -H "Authorization: Bearer $TOKEN_USER"
```

### Filtrar pagos por cliente
```bash
curl -X GET "http://localhost:8080/api/pagos?usuarioId=2&clienteId=1" \
  -H "Authorization: Bearer $TOKEN_USER"
```

### Cambiar estado de un pago
```bash
curl -X PATCH "http://localhost:8080/api/pagos/1/estado?estado=PAGADO" \
  -H "Authorization: Bearer $TOKEN_USER"
```

---

## 🧪 Pruebas de Errores

### Intento de acceso sin token (debe dar 403)
```bash
curl -X GET http://localhost:8080/api/usuarios
```

### Intento de acceso con token inválido (debe dar 403)
```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer token_invalido"
```

### Usuario normal intentando acceder a endpoint de admin (debe dar 403)
```bash
curl -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN_USER"
```

### Login con credenciales incorrectas (debe dar 401)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "login": "admin",
    "password": "password_incorrecto"
  }'
```

---

## 📋 Script de Prueba Completo

Guarda esto en un archivo `test_api.sh`:

```bash
#!/bin/bash

echo "=== 1. Verificando que la API está activa ==="
curl -s http://localhost:8080/health | jq .

echo -e "\n=== 2. Login como ADMINISTRADOR ==="
TOKEN_ADMIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"admin123"}' \
  | jq -r '.token')
echo "Token obtenido: ${TOKEN_ADMIN:0:50}..."

echo -e "\n=== 3. Listar usuarios (requiere ADMIN) ==="
curl -s -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN_ADMIN" | jq .

echo -e "\n=== 4. Login como USUARIO ==="
TOKEN_USER=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"user","password":"user123"}' \
  | jq -r '.token')
echo "Token obtenido: ${TOKEN_USER:0:50}..."

echo -e "\n=== 5. Listar tipos de IVA activos (USER puede verlos) ==="
curl -s -X GET http://localhost:8080/api/tipos-iva/activos \
  -H "Authorization: Bearer $TOKEN_USER" | jq .

echo -e "\n=== 6. Intentar listar usuarios como USER (debe fallar) ==="
curl -s -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN_USER"

echo -e "\n\n✅ Pruebas completadas"
```

Ejecutar:
```bash
chmod +x test_api.sh
./test_api.sh
```

---

## 💡 Notas

1. **jq**: Los ejemplos usan `jq` para formatear el JSON. Si no lo tienes instalado:
   ```bash
   sudo apt install jq  # Ubuntu/Debian
   ```

2. **Variables de entorno**: Guarda los tokens en variables para no tener que copiar/pegar:
   ```bash
   export TOKEN_ADMIN="tu_token_aqui"
   export TOKEN_USER="tu_token_aqui"
   ```

3. **Consola H2**: Accede a la base de datos en http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:facturaaas`
   - Usuario: `sa`
   - Password: (vacío)
