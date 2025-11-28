# Cambio de Inyección de Dependencias: Constructor → Field Injection

## 📅 Fecha: 21 de noviembre de 2025

## 📋 Descripción del Cambio

Se ha modificado el mecanismo de inyección de dependencias en todo el proyecto, cambiando de **Constructor Injection** (con `@RequiredArgsConstructor` de Lombok) a **Field Injection** (con `@Autowired` de Spring).

---

## 🔄 Cambios Realizados

### Patrón ANTES (Constructor Injection)

```java
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MiController {
    
    private final MiService miService;
    private final OtroService otroService;
}
```

### Patrón DESPUÉS (Field Injection)

```java
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class MiController {
    
    @Autowired
    private MiService miService;
    
    @Autowired
    private OtroService otroService;
}
```

---

## 📂 Archivos Modificados

### Controllers (7 archivos)

1. **`AuthController.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Eliminado: `import lombok.RequiredArgsConstructor;`
   - Añadido: `import org.springframework.beans.factory.annotation.Autowired;`
   - Cambiado: `private final AuthenticationManager` → `@Autowired private AuthenticationManager`
   - Cambiado: `private final JwtTokenProvider` → `@Autowired private JwtTokenProvider`
   - Cambiado: `private final UsuarioService` → `@Autowired private UsuarioService`

2. **`UsuarioController.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `usuarioService`

3. **`TipoIVAController.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `tipoIVAService`

4. **`FormaPagoController.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `formaPagoService`

5. **`ClienteController.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `clienteService`

6. **`FacturaController.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `facturaService`

7. **`PagoController.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `pagoService`

### Services (6 archivos)

1. **`UsuarioService.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `usuarioRepository` y `passwordEncoder`

2. **`TipoIVAService.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `tipoIVARepository`

3. **`FormaPagoService.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `formaPagoRepository` y `usuarioRepository`

4. **`ClienteService.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `clienteRepository` y `usuarioRepository`

5. **`FacturaService.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a 6 dependencias:
     * `facturaRepository`
     * `usuarioRepository`
     * `clienteRepository`
     * `formaPagoRepository`
     * `tipoIVARepository`
     * `pagoService`

6. **`PagoService.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `pagoRepository`

### Security (2 archivos)

1. **`ResourceSecurityService.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a 5 repositorios:
     * `clienteRepository`
     * `facturaRepository`
     * `formaPagoRepository`
     * `pagoRepository`
     * `usuarioRepository`

2. **`CustomUserDetailsService.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a `usuarioRepository`

### Config (1 archivo)

1. **`DataInitializer.java`**
   - Eliminado: `@RequiredArgsConstructor`
   - Añadido: `@Autowired` a 4 dependencias:
     * `usuarioRepository`
     * `tipoIVARepository`
     * `formaPagoRepository`
     * `passwordEncoder`

---

## 📊 Resumen de Cambios

| Tipo | Archivos Modificados | Dependencias Cambiadas |
|------|---------------------|------------------------|
| **Controllers** | 7 | 10 |
| **Services** | 6 | 14 |
| **Security** | 2 | 6 |
| **Config** | 1 | 4 |
| **TOTAL** | **16** | **34** |

---

## ✅ Verificación

### Compilación
```bash
mvn clean compile
```

**Resultado**: ✅ **BUILD SUCCESS**
- 49 archivos compilados correctamente
- Tiempo: 2.906s
- Sin errores ni warnings

---

## 🔍 Diferencias entre Patrones

### Constructor Injection (ANTES)

**Ventajas**:
- ✅ Inmutabilidad (`final`)
- ✅ Mandatory dependencies explícitas
- ✅ Facilita testing (sin reflexión)
- ✅ No necesita `@Autowired` (desde Spring 4.3)
- ✅ Recomendado por Spring

**Desventajas**:
- ❌ Constructores grandes con muchas dependencias
- ❌ Dependencia de Lombok para reducir boilerplate

### Field Injection (DESPUÉS)

**Ventajas**:
- ✅ Código más compacto
- ✅ Sin necesidad de Lombok para inyección
- ✅ Fácil añadir/quitar dependencias

**Desventajas**:
- ❌ No permite `final` (mutabilidad)
- ❌ Dependencias opcionales no explícitas
- ❌ Más difícil de testear unitariamente
- ❌ Viola principio de inmutabilidad
- ⚠️ No recomendado por Spring oficialmente

---

## 📚 Documentación Relacionada

- [Spring Framework: Dependency Injection](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-collaborators)
- [Why field injection is evil](https://www.vojtechruzicka.com/field-dependency-injection-considered-harmful/)

---

## 🎯 Recomendación

Aunque el cambio se ha aplicado correctamente, la **inyección por constructor** es considerada la mejor práctica por:

1. **Inmutabilidad**: Uso de `final`
2. **Testabilidad**: Más fácil crear mocks
3. **Claridad**: Dependencias obligatorias explícitas
4. **Seguridad**: Falla rápido si falta una dependencia

Si se desea revertir al patrón anterior, se puede hacer fácilmente ejecutando el mismo proceso en sentido inverso.

---

**Documento generado automáticamente**: 21 de noviembre de 2025
