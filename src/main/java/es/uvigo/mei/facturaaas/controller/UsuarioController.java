package es.uvigo.mei.facturaaas.controller;

import es.uvigo.mei.facturaaas.dto.UsuarioCreateDTO;
import es.uvigo.mei.facturaaas.dto.UsuarioDTO;
import es.uvigo.mei.facturaaas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioCreateDTO dto) {
        UsuarioDTO nuevoUsuario = usuarioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarLogicamente(id);
        return ResponseEntity.noContent().build();
    }
}
