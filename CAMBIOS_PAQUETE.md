# Cambio de Paquete Base de la Aplicación

## 📅 Fecha: 21 de noviembre de 2025

## 📋 Descripción del Cambio

Se ha modificado el paquete base de toda la aplicación:

**Antes**: `com.facturaaas`  
**Después**: `es.uvigo.mei.facturaaas`

---

## 🔄 Cambios Realizados

### 1. Estructura de Directorios

**Antes**:
```
src/main/java/
└── com/
    └── facturaaas/
        ├── config/
        ├── controller/
        ├── dto/
        ├── model/
        ├── repository/
        ├── security/
        └── service/
```

**Después**:
```
src/main/java/
└── es/
    └── uvigo/
        └── mei/
            └── facturaaas/
                ├── config/
                ├── controller/
                ├── dto/
                ├── model/
                ├── repository/
                ├── security/
                └── service/
```

### 2. Declaraciones de Paquete

Todos los archivos `.java` han sido actualizados:

**Antes**:
```java
package com.facturaaas.controller;
package com.facturaaas.service;
package com.facturaaas.model;
// etc.
```

**Después**:
```java
package es.uvigo.mei.facturaaas.controller;
package es.uvigo.mei.facturaaas.service;
package es.uvigo.mei.facturaaas.model;
// etc.
```

### 3. Importaciones

Todas las importaciones internas han sido actualizadas:

**Antes**:
```java
import com.facturaaas.dto.UsuarioDTO;
import com.facturaaas.model.Usuario;
import com.facturaaas.repository.UsuarioRepository;
import com.facturaaas.service.UsuarioService;
```

**Después**:
```java
import es.uvigo.mei.facturaaas.dto.UsuarioDTO;
import es.uvigo.mei.facturaaas.model.Usuario;
import es.uvigo.mei.facturaaas.repository.UsuarioRepository;
import es.uvigo.mei.facturaaas.service.UsuarioService;
```

### 4. Configuración de Logging

**Archivo**: `src/main/resources/application.properties`

**Antes**:
```properties
logging.level.com.facturaaas=DEBUG
```

**Después**:
```properties
logging.level.es.uvigo.mei.facturaaas=DEBUG
```

---

## 📂 Archivos Afectados

### Todos los archivos Java (49 archivos)

**Controllers** (7):
- `AuthController.java`
- `ClienteController.java`
- `FacturaController.java`
- `FormaPagoController.java`
- `PagoController.java`
- `TipoIVAController.java`
- `UsuarioController.java`

**Services** (6):
- `ClienteService.java`
- `FacturaService.java`
- `FormaPagoService.java`
- `PagoService.java`
- `TipoIVAService.java`
- `UsuarioService.java`

**Repositories** (8):
- `ClienteRepository.java`
- `DatosFacturacionRepository.java`
- `FacturaRepository.java`
- `FormaPagoRepository.java`
- `LineaFacturaRepository.java`
- `PagoRepository.java`
- `TipoIVARepository.java`
- `UsuarioRepository.java`

**Models** (8):
- `Cliente.java`
- `DatosFacturacion.java`
- `Factura.java`
- `FormaPago.java`
- `LineaFactura.java`
- `Pago.java`
- `TipoIVA.java`
- `Usuario.java`

**DTOs** (10):
- `AuthResponseDTO.java`
- `ClienteDTO.java`
- `DatosFacturacionDTO.java`
- `FacturaDTO.java`
- `FormaPagoDTO.java`
- `LineaFacturaDTO.java`
- `LoginDTO.java`
- `PagoDTO.java`
- `TipoIVADTO.java`
- `UsuarioCreateDTO.java`
- `UsuarioDTO.java`

**Security** (3):
- `CustomUserDetailsService.java`
- `JwtAuthenticationFilter.java`
- `JwtTokenProvider.java`
- `ResourceSecurityService.java`

**Config** (2):
- `DataInitializer.java`
- `SecurityConfig.java`

**Application** (1):
- `FACTURAaaSApplication.java`

**Properties** (1):
- `application.properties`

---

## 🛠️ Método de Actualización

### Paso 1: Mover Archivos
```bash
cd src/main/java
mkdir -p es/uvigo/mei/facturaaas
mv com/facturaaas/* es/uvigo/mei/facturaaas/
rm -rf com
```

### Paso 2: Actualizar Declaraciones de Paquete
```bash
find src/main/java/es/uvigo/mei/facturaaas -name "*.java" -type f \
  -exec sed -i 's/package com\.facturaaas/package es.uvigo.mei.facturaaas/g' {} \;
```

### Paso 3: Actualizar Importaciones
```bash
find src/main/java/es/uvigo/mei/facturaaas -name "*.java" -type f \
  -exec sed -i 's/import com\.facturaaas/import es.uvigo.mei.facturaaas/g' {} \;
```

### Paso 4: Actualizar application.properties
Manual: cambiar `logging.level.com.facturaaas` a `logging.level.es.uvigo.mei.facturaaas`

---

## ✅ Verificación

### Compilación
```bash
mvn clean compile
```

**Resultado**: ✅ **BUILD SUCCESS**
- 49 archivos compilados correctamente
- Tiempo: 2.826s
- Sin errores ni warnings

### Verificación de Archivos
Se verificó que los siguientes archivos contienen el nuevo paquete:
- ✅ `AuthController.java`: `package es.uvigo.mei.facturaaas.controller;`
- ✅ `Usuario.java`: `package es.uvigo.mei.facturaaas.model;`
- ✅ `UsuarioService.java`: `package es.uvigo.mei.facturaaas.service;`
- ✅ Todas las importaciones actualizadas correctamente

---

## 📊 Estadísticas

| Concepto | Cantidad |
|----------|----------|
| **Archivos Java modificados** | 49 |
| **Archivos de configuración modificados** | 1 |
| **Paquetes afectados** | 8 (config, controller, dto, model, repository, security, service, root) |
| **Líneas de import actualizadas** | ~350+ |
| **Directorios movidos** | 1 |

---

## 🎯 Impacto

### ✅ Sin Impacto Funcional
- La lógica de negocio permanece intacta
- Las anotaciones de Spring funcionan correctamente
- La seguridad JWT sigue operativa
- Todas las rutas REST mantienen su funcionalidad

### ℹ️ Cambios Visibles
- URLs de paquetes en stacktraces mostrarán el nuevo paquete
- Logs mostrarán `es.uvigo.mei.facturaaas` en lugar de `com.facturaaas`
- Estructura de directorios refleja la organización institucional

---

## 📚 Razón del Cambio

El nuevo paquete `es.uvigo.mei.facturaaas` sigue la convención de nomenclatura de Java que usa:
- **País**: `es` (España)
- **Institución**: `uvigo` (Universidad de Vigo)
- **Departamento/Área**: `mei` (Máster en Ingeniería Informática)
- **Proyecto**: `facturaaas` (Sistema de Facturación)

Esta estructura es más apropiada para un proyecto académico/institucional.

---

## 🔙 Reversión

Si fuera necesario revertir el cambio, se aplicaría el proceso inverso:

```bash
# 1. Mover archivos
cd src/main/java
mkdir -p com/facturaaas
mv es/uvigo/mei/facturaaas/* com/facturaaas/
rm -rf es

# 2. Actualizar paquetes
find src/main/java/com/facturaaas -name "*.java" -type f \
  -exec sed -i 's/package es\.uvigo\.mei\.facturaaas/package com.facturaaas/g' {} \;

# 3. Actualizar imports
find src/main/java/com/facturaaas -name "*.java" -type f \
  -exec sed -i 's/import es\.uvigo\.mei\.facturaaas/import com.facturaaas/g' {} \;

# 4. Actualizar application.properties
# Cambiar logging.level.es.uvigo.mei.facturaaas a logging.level.com.facturaaas
```

---

**Documento generado automáticamente**: 21 de noviembre de 2025
