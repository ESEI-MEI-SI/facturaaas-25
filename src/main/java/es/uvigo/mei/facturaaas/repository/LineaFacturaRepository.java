package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.LineaFactura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LineaFacturaRepository extends JpaRepository<LineaFactura, Long> {
    // Métodos personalizados (de momento ninguno)
}
