package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.DatosFacturacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatosFacturacionRepository extends JpaRepository<DatosFacturacion, Long> {
    // Métodos personalizados (de momento ninguno)
}
