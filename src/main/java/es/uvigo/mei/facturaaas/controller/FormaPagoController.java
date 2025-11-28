package es.uvigo.mei.facturaaas.controller;

import es.uvigo.mei.facturaaas.dto.FormaPagoDTO;
import es.uvigo.mei.facturaaas.service.FormaPagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/formas-pago")
@PreAuthorize("hasRole('USUARIO')")
public class FormaPagoController {
    
    @Autowired
    private FormaPagoService formaPagoService;
    
    @GetMapping
    @PreAuthorize("@resourceSecurity.canAccess(#usuarioId)")
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
    
    @GetMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessFormaPago(#id)")
    public ResponseEntity<FormaPagoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(formaPagoService.obtenerPorId(id));
    }
    
    @PostMapping
    @PreAuthorize("@resourceSecurity.canAccess(#dto.usuarioId)")
    public ResponseEntity<FormaPagoDTO> crear(@Valid @RequestBody FormaPagoDTO dto) {
        FormaPagoDTO nueva = formaPagoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessFormaPago(#id)")
    public ResponseEntity<FormaPagoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody FormaPagoDTO dto) {
        return ResponseEntity.ok(formaPagoService.actualizar(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessFormaPago(#id)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        formaPagoService.eliminarLogicamente(id);
        return ResponseEntity.noContent().build();
    }
}
