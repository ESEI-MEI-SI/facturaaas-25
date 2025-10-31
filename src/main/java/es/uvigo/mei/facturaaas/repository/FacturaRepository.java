package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    // Métodos personalizados (de momento ninguno)
}
