package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.DatosFacturacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DatosFacturacionRepository extends JpaRepository<DatosFacturacion, Long> {
    Optional<DatosFacturacion> findByUsuarioId(Long usuarioId);
}
