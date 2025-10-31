package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormaPagoRepository extends JpaRepository<FormaPago, Long> {
    // Métodos personalizados (de momento ninguno)
}
