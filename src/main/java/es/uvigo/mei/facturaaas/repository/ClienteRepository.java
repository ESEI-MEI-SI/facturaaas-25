package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Métodos personalizados (de momento ninguno)
}
