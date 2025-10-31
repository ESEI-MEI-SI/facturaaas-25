package es.uvigo.mei.facturaaas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String numeroFactura;
    
    @Column(nullable = false)
    private Integer ejercicio;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @Column(nullable = false)
    private LocalDate fechaEmision;
    
    @ManyToOne
    @JoinColumn(name = "forma_pago_id", nullable = false)
    private FormaPago formaPago;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoFactura estado = EstadoFactura.EMITIDA;
    
    @Column(length = 500)
    private String comentarios;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal importeTotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal ivaTotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal sumaTotal;
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaFactura> lineas = new ArrayList<>();
    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos = new ArrayList<>();
    
    public enum EstadoFactura {
        EMITIDA,
        ANULADA,
        PAGADA,
        RECLAMADA,
        ABONADA
    }
    
    public void calcularTotales() {
        importeTotal = lineas.stream()
            .map(LineaFactura::getImporteTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        ivaTotal = lineas.stream()
            .map(linea -> {
                BigDecimal base = linea.getImporteTotal();
                BigDecimal porcentajeIva = linea.getTipoIVA().getPorcentaje();
                return base.multiply(porcentajeIva).divide(new BigDecimal("100"));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        sumaTotal = importeTotal.add(ivaTotal);
    }
}
