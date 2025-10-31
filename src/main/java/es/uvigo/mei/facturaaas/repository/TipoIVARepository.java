package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.TipoIVA;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoIVARepository extends JpaRepository<TipoIVA, Long> {
    // Métodos personalizados (de momento ninguno)
}
