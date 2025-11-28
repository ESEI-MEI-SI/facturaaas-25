package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.TipoIVA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TipoIVARepository extends JpaRepository<TipoIVA, Long> {
    List<TipoIVA> findByActivoTrue();
}
