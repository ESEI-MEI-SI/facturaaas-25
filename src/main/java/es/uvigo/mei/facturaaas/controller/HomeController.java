package es.uvigo.mei.facturaaas.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@Profile("!prod")  // Solo activo en desarrollo, deshabilitado en producción
public class HomeController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("aplicacion", "FACTURAaaS Backend");
        response.put("version", "1.0.0");
        response.put("estado", "Activo");
        response.put("timestamp", LocalDateTime.now());
        response.put("mensaje", "API REST funcionando correctamente");
        response.put("endpoints", Map.of(
            "login", "POST /api/auth/login",
            "usuarios", "GET /api/usuarios (requiere auth ADMIN)",
            "clientes", "GET /api/clientes?usuarioId={id} (requiere auth USER)",
            "facturas", "GET /api/facturas?usuarioId={id} (requiere auth USER)",
            "pagos", "GET /api/pagos?usuarioId={id} (requiere auth USER)",
            "h2-console", "GET /h2-console (base de datos)"
        ));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
