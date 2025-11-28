package es.uvigo.mei.facturaaas.security;

import es.uvigo.mei.facturaaas.model.Usuario;
import es.uvigo.mei.facturaaas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByLogin(login)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + login));
        
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + login);
        }
        
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );
        
        return new User(usuario.getLogin(), usuario.getPassword(), authorities);
    }
}
