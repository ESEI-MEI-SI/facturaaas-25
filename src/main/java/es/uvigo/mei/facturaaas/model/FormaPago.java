package es.uvigo.mei.facturaaas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "forma_pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormaPago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false, length = 100)
    private String descripcion;
    
    @Column(nullable = false)
    private Integer numeroPagos;
    
    @Column(nullable = false)
    private Integer periodicidadDias;
    
    @Column(nullable = false)
    private Boolean activa = true;
}
