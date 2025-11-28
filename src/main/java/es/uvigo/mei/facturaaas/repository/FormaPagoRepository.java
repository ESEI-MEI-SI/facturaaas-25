package es.uvigo.mei.facturaaas.repository;

import es.uvigo.mei.facturaaas.model.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FormaPagoRepository extends JpaRepository<FormaPago, Long> {
    List<FormaPago> findByActivaTrue();
    List<FormaPago> findByUsuarioIdAndActivaTrue(Long usuarioId);
    List<FormaPago> findByUsuarioId(Long usuarioId);
}
