package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    // Métodos personalizados (de momento ninguno)
}
