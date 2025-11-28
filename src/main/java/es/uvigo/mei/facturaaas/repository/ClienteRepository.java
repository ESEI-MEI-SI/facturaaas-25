package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT c FROM Cliente c WHERE c.usuario.id = :usuarioId " +
           "AND (LOWER(c.nombre) LIKE LOWER(CONCAT('%', :patron, '%')) " +
           "OR LOWER(c.localidad) LIKE LOWER(CONCAT('%', :patron, '%')))")
    List<Cliente> buscarPorUsuarioYPatron(@Param("usuarioId") Long usuarioId, 
                                           @Param("patron") String patron);
}
