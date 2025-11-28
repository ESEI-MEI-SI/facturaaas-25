package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByFacturaId(Long facturaId);
    
    @Query("SELECT p FROM Pago p WHERE p.factura.usuario.id = :usuarioId")
    List<Pago> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT p FROM Pago p WHERE p.factura.usuario.id = :usuarioId " +
           "AND p.factura.cliente.id = :clienteId")
    List<Pago> findByUsuarioIdAndClienteId(@Param("usuarioId") Long usuarioId, 
                                            @Param("clienteId") Long clienteId);
}
