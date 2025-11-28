package es.uvigo.mei.facturaaas.service;

import es.uvigo.mei.facturaaas.dto.UsuarioCreateDTO;
import es.uvigo.mei.facturaaas.dto.UsuarioDTO;
import es.uvigo.mei.facturaaas.model.Usuario;
import es.uvigo.mei.facturaaas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<UsuarioDTO> obtenerTodos() {
        return usuarioRepository.findAll().stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    public UsuarioDTO obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirADTO(usuario);
    }
    
    public UsuarioDTO obtenerPorLogin(String login) {
        Usuario usuario = usuarioRepository.findByLogin(login)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirADTO(usuario);
    }
    
    public UsuarioDTO crear(UsuarioCreateDTO dto) {
        if (usuarioRepository.existsByLogin(dto.getLogin())) {
            throw new RuntimeException("El login ya existe");
        }
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email ya existe");
        }
        
        Usuario usuario = new Usuario();
        usuario.setLogin(dto.getLogin());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setRol(Usuario.Rol.USUARIO);
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        
        usuario = usuarioRepository.save(usuario);
        return convertirADTO(usuario);
    }
    
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        usuario.setRol(dto.getRol());
        usuario.setActivo(dto.getActivo());
        
        usuario = usuarioRepository.save(usuario);
        return convertirADTO(usuario);
    }
    
    public void eliminarLogicamente(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }
    
    public void actualizarFechaAcceso(String login) {
        Usuario usuario = usuarioRepository.findByLogin(login)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setFechaUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
    }
    
    private UsuarioDTO convertirADTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setLogin(usuario.getLogin());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        dto.setActivo(usuario.getActivo());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setFechaUltimoAcceso(usuario.getFechaUltimoAcceso());
        return dto;
    }
}
