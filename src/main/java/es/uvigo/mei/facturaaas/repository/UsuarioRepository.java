package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método necesario usado en DataInitializer
    Optional<Usuario> findByLogin(String login);
}
