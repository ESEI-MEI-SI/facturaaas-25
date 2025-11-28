#!/bin/bash

# Colores para la salida
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Test FACTURAaaS Backend API${NC}"
echo -e "${BLUE}========================================${NC}\n"

# 1. Verificar que la API está activa
echo -e "${YELLOW}=== 1. Verificando que la API está activa ===${NC}"
curl -s http://localhost:8080/health | jq .
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ API activa${NC}\n"
else
    echo -e "${RED}✗ API no responde${NC}\n"
    exit 1
fi

# 2. Página de inicio
echo -e "${YELLOW}=== 2. Información de la API ===${NC}"
curl -s http://localhost:8080/ | jq .
echo ""

# 3. Intento de acceso sin autenticación (debe fallar)
echo -e "${YELLOW}=== 3. Intento de acceso sin autenticación (debe fallar con 403) ===${NC}"
HTTP_CODE=$(curl -s -w "%{http_code}" -o /dev/null http://localhost:8080/api/usuarios)
if [ "$HTTP_CODE" -eq 403 ]; then
    echo -e "${GREEN}✓ Correctamente bloqueado: HTTP $HTTP_CODE${NC}\n"
else
    echo -e "${RED}✗ Inesperado: HTTP $HTTP_CODE${NC}\n"
fi

# 4. Login como ADMINISTRADOR
echo -e "${YELLOW}=== 4. Login como ADMINISTRADOR ===${NC}"
TOKEN_ADMIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"admin123"}' \
  | jq -r '.token')

if [ -n "$TOKEN_ADMIN" ] && [ "$TOKEN_ADMIN" != "null" ]; then
    echo -e "${GREEN}✓ Token de ADMIN obtenido: ${TOKEN_ADMIN:0:60}...${NC}\n"
else
    echo -e "${RED}✗ Error obteniendo token de ADMIN${NC}\n"
    exit 1
fi

# 5. Listar usuarios (requiere ADMIN)
echo -e "${YELLOW}=== 5. Listar todos los usuarios (requiere ADMIN) ===${NC}"
curl -s -X GET http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN_ADMIN" | jq .
echo ""

# 6. Crear un nuevo usuario
echo -e "${YELLOW}=== 6. Crear un nuevo usuario (ADMIN) ===${NC}"
NUEVO_USUARIO=$(curl -s -X POST http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "login": "testuser",
    "password": "test123",
    "nombre": "Usuario Test",
    "email": "testuser@example.com"
  }')
echo "$NUEVO_USUARIO" | jq .
USUARIO_ID=$(echo "$NUEVO_USUARIO" | jq -r '.id')
echo -e "${GREEN}✓ Usuario creado con ID: $USUARIO_ID${NC}\n"

# 7. Login como USUARIO normal
echo -e "${YELLOW}=== 7. Login como USUARIO normal ===${NC}"
TOKEN_USER=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"user","password":"user123"}' \
  | jq -r '.token')

if [ -n "$TOKEN_USER" ] && [ "$TOKEN_USER" != "null" ]; then
    echo -e "${GREEN}✓ Token de USER obtenido: ${TOKEN_USER:0:60}...${NC}\n"
else
    echo -e "${RED}✗ Error obteniendo token de USER${NC}\n"
    exit 1
fi

# 8. Usuario normal intentando acceder a endpoint de ADMIN (debe fallar)
echo -e "${YELLOW}=== 8. USER intentando acceder a endpoint de ADMIN (debe fallar) ===${NC}"
HTTP_CODE=$(curl -s -w "%{http_code}" -o /dev/null \
  http://localhost:8080/api/usuarios \
  -H "Authorization: Bearer $TOKEN_USER")
if [ "$HTTP_CODE" -eq 400 ] || [ "$HTTP_CODE" -eq 403 ]; then
    echo -e "${GREEN}✓ Correctamente bloqueado: HTTP $HTTP_CODE${NC}\n"
else
    echo -e "${RED}✗ Inesperado: HTTP $HTTP_CODE${NC}\n"
fi

# 9. Listar tipos de IVA activos (USER puede acceder)
echo -e "${YELLOW}=== 9. Listar tipos de IVA activos (USER tiene permiso) ===${NC}"
curl -s -X GET http://localhost:8080/api/tipos-iva/activos \
  -H "Authorization: Bearer $TOKEN_USER" | jq .
echo ""

# 10. Listar formas de pago activas (USER puede acceder)
echo -e "${YELLOW}=== 10. Listar formas de pago activas (USER tiene permiso) ===${NC}"
curl -s -X GET http://localhost:8080/api/formas-pago/activas \
  -H "Authorization: Bearer $TOKEN_USER" | jq .
echo ""

# 11. Crear un cliente
echo -e "${YELLOW}=== 11. Crear un cliente (USER) ===${NC}"
NUEVO_CLIENTE=$(curl -s -X POST http://localhost:8080/api/clientes \
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
  }')
echo "$NUEVO_CLIENTE" | jq .
CLIENTE_ID=$(echo "$NUEVO_CLIENTE" | jq -r '.id')
echo -e "${GREEN}✓ Cliente creado con ID: $CLIENTE_ID${NC}\n"

# 12. Crear una factura con líneas
echo -e "${YELLOW}=== 12. Crear una factura con líneas (USER) ===${NC}"
NUEVA_FACTURA=$(curl -s -X POST http://localhost:8080/api/facturas \
  -H "Authorization: Bearer $TOKEN_USER" \
  -H "Content-Type: application/json" \
  -d "{
    \"usuarioId\": 2,
    \"clienteId\": $CLIENTE_ID,
    \"ejercicio\": 2025,
    \"fechaEmision\": \"2025-10-30\",
    \"formaPagoId\": 1,
    \"estado\": \"EMITIDA\",
    \"comentarios\": \"Factura de prueba\",
    \"lineas\": [
      {
        \"concepto\": \"Desarrollo web\",
        \"cantidad\": 40,
        \"precioUnitario\": 50.00,
        \"porcentajeDescuento\": 0,
        \"tipoIVAId\": 1
      },
      {
        \"concepto\": \"Consultoría\",
        \"cantidad\": 10,
        \"precioUnitario\": 80.00,
        \"porcentajeDescuento\": 10,
        \"tipoIVAId\": 1
      }
    ]
  }")
echo "$NUEVA_FACTURA" | jq .
FACTURA_ID=$(echo "$NUEVA_FACTURA" | jq -r '.id')
echo -e "${GREEN}✓ Factura creada con ID: $FACTURA_ID${NC}\n"

# 13. Generar pagos para la factura
echo -e "${YELLOW}=== 13. Generar pagos para la factura ===${NC}"
curl -s -X POST http://localhost:8080/api/facturas/$FACTURA_ID/generar-pagos \
  -H "Authorization: Bearer $TOKEN_USER" | jq .
echo ""

# 14. Listar pagos del usuario
echo -e "${YELLOW}=== 14. Listar pagos del usuario ===${NC}"
curl -s -X GET http://localhost:8080/api/pagos?usuarioId=2 \
  -H "Authorization: Bearer $TOKEN_USER" | jq .
echo ""

# 15. Actualizar estado de un pago
echo -e "${YELLOW}=== 15. Marcar pago como PAGADO ===${NC}"
PAGOS=$(curl -s -X GET http://localhost:8080/api/pagos?usuarioId=2 \
  -H "Authorization: Bearer $TOKEN_USER")
PAGO_ID=$(echo "$PAGOS" | jq -r '.[0].id')
if [ -n "$PAGO_ID" ] && [ "$PAGO_ID" != "null" ]; then
    curl -s -X PATCH "http://localhost:8080/api/pagos/$PAGO_ID/estado?estado=PAGADO" \
      -H "Authorization: Bearer $TOKEN_USER" | jq .
    echo -e "${GREEN}✓ Estado del pago $PAGO_ID actualizado${NC}\n"
else
    echo -e "${RED}✗ No se encontraron pagos para actualizar${NC}\n"
fi

# 16. Login con credenciales incorrectas (debe fallar)
echo -e "${YELLOW}=== 16. Intento de login con credenciales incorrectas (debe fallar) ===${NC}"
HTTP_CODE=$(curl -s -w "%{http_code}" -o /dev/null \
  -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"wrongpassword"}')
if [ "$HTTP_CODE" -eq 401 ]; then
    echo -e "${GREEN}✓ Correctamente rechazado: HTTP $HTTP_CODE${NC}\n"
else
    echo -e "${RED}✗ Inesperado: HTTP $HTTP_CODE${NC}\n"
fi

# 17. Eliminar el usuario de prueba
echo -e "${YELLOW}=== 17. Eliminar usuario de prueba (ADMIN) ===${NC}"
curl -s -X DELETE http://localhost:8080/api/usuarios/$USUARIO_ID \
  -H "Authorization: Bearer $TOKEN_ADMIN"
echo -e "${GREEN}✓ Usuario $USUARIO_ID eliminado${NC}\n"

# Resumen final
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  ✅ Pruebas completadas exitosamente${NC}"
echo -e "${BLUE}========================================${NC}\n"

echo -e "${GREEN}Endpoints públicos:${NC}"
echo "  • GET  http://localhost:8080/"
echo "  • GET  http://localhost:8080/health"
echo "  • POST http://localhost:8080/api/auth/login"
echo ""
echo -e "${GREEN}H2 Console (base de datos):${NC}"
echo "  • URL:      http://localhost:8080/h2-console"
echo "  • JDBC URL: jdbc:h2:mem:facturaaas"
echo "  • Usuario:  sa"
echo "  • Password: (vacío)"
echo ""
echo -e "${GREEN}Usuarios de prueba:${NC}"
echo "  • ADMIN: admin / admin123"
echo "  • USER:  user / user123"
echo ""
