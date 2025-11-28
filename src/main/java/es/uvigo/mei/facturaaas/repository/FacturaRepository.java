package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByUsuarioId(Long usuarioId);
    List<Factura> findByUsuarioIdAndClienteId(Long usuarioId, Long clienteId);
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    boolean existsByNumeroFactura(String numeroFactura);
}
