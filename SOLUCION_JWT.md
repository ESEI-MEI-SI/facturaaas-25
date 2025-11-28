# Solución: Error de Compilación JwtTokenProvider

## Problema
Al ejecutar `mvn spring-boot:run`, se producían errores de compilación:
```
[ERROR] cannot find symbol: method parserBuilder()
```

## Causa
La versión de JJWT 0.12.x tiene una API diferente a versiones anteriores. Los métodos `parserBuilder()`, `setSubject()`, `setIssuedAt()`, `setExpiration()` y `signWith(Key, SignatureAlgorithm)` están deprecated o han sido reemplazados.

## Solución Aplicada

### 1. Actualización de JwtTokenProvider.java

**Cambios realizados:**

#### Imports actualizados
```java
// Eliminado
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;

// Añadido
import javax.crypto.SecretKey;
```

#### Método helper para la clave
```java
private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes());
}
```

#### Generación de token (API nueva)
```java
// ANTES (deprecated)
return Jwts.builder()
    .setSubject(username)
    .setIssuedAt(now)
    .setExpiration(expiryDate)
    .signWith(key, SignatureAlgorithm.HS512)
    .compact();

// AHORA (API 0.12.x)
return Jwts.builder()
    .subject(username)
    .issuedAt(now)
    .expiration(expiryDate)
    .signWith(getSigningKey())
    .compact();
```

#### Parsing de token (API nueva)
```java
// ANTES (no disponible)
Claims claims = Jwts.parserBuilder()
    .setSigningKey(key)
    .build()
    .parseClaimsJws(token)
    .getBody();

// AHORA (API 0.12.x)
Claims claims = Jwts.parser()
    .verifyWith(getSigningKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
```

### 2. Cambio de puerto (8080 → 8081)

Como el puerto 8080 estaba ocupado, se cambió en `application.properties`:
```properties
server.port=8081
```

## Resultado

✅ **Compilación exitosa**
✅ **Aplicación iniciada correctamente en puerto 8081**
✅ **Base de datos H2 configurada y tablas creadas**
✅ **Usuarios de prueba creados automáticamente**
✅ **Datos iniciales cargados (Tipos IVA, Formas de Pago)**

## Acceso a la Aplicación

- **API Backend**: http://localhost:8081
- **Consola H2**: http://localhost:8081/h2-console
  - JDBC URL: `jdbc:h2:mem:facturaaas`
  - Usuario: `sa`
  - Password: (vacío)

## Usuarios de Prueba

| Usuario | Password | Rol |
|---------|----------|-----|
| admin | admin123 | ADMINISTRADOR |
| user | user123 | USUARIO |

## Próximos Pasos

1. Probar el endpoint de login:
   ```bash
   curl -X POST http://localhost:8081/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"login":"admin","password":"admin123"}'
   ```

2. Usar el token JWT recibido para acceder a endpoints protegidos:
   ```bash
   curl -X GET http://localhost:8081/api/usuarios \
     -H "Authorization: Bearer <tu-token-jwt>"
   ```
