package es.uvigo.mei.facturaaas.controller;

import es.uvigo.mei.facturaaas.dto.ClienteDTO;
import es.uvigo.mei.facturaaas.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@PreAuthorize("hasRole('USUARIO')")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    @GetMapping
    @PreAuthorize("@resourceSecurity.canAccess(#usuarioId)")
    public ResponseEntity<List<ClienteDTO>> listar(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) String patron) {
        if (usuarioId != null && patron != null) {
            return ResponseEntity.ok(clienteService.buscarPorUsuarioYPatron(usuarioId, patron));
        } else if (usuarioId != null) {
            return ResponseEntity.ok(clienteService.obtenerPorUsuario(usuarioId));
        }
        return ResponseEntity.badRequest().build();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessCliente(#id)")
    public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }
    
    @PostMapping
    @PreAuthorize("@resourceSecurity.canAccess(#dto.usuarioId)")
    public ResponseEntity<ClienteDTO> crear(@Valid @RequestBody ClienteDTO dto) {
        ClienteDTO nuevo = clienteService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("@resourceSecurity.canAccessCliente(#id)")
    public ResponseEntity<ClienteDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }
}
