package es.uvigo.mei.facturaaas.controller;

import es.uvigo.mei.facturaaas.dto.AuthResponseDTO;
import es.uvigo.mei.facturaaas.dto.LoginDTO;
import es.uvigo.mei.facturaaas.dto.UsuarioDTO;
import es.uvigo.mei.facturaaas.security.JwtTokenProvider;
import es.uvigo.mei.facturaaas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDTO.getLogin(),
                loginDTO.getPassword()
            )
        );
        
        String token = jwtTokenProvider.generateToken(authentication);
        UsuarioDTO usuario = usuarioService.obtenerPorLogin(loginDTO.getLogin());
        usuarioService.actualizarFechaAcceso(loginDTO.getLogin());
        
        return ResponseEntity.ok(new AuthResponseDTO(token, usuario));
    }
}
