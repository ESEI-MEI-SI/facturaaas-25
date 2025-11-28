package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.LineaFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LineaFacturaRepository extends JpaRepository<LineaFactura, Long> {
    List<LineaFactura> findByFacturaIdOrderByNumeroLineaAsc(Long facturaId);
}
