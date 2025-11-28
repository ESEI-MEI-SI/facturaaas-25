package es.uvigo.mei.facturaaas.dto;

import es.uvigo.mei.facturaaas.model.Factura;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private Long id;
    private String numeroFactura;
    
    @NotNull(message = "El ejercicio es obligatorio")
    private Integer ejercicio;
    
    private Long usuarioId;
    
    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;
    
    private String clienteNombre;
    private String clienteNif;
    
    @NotNull(message = "La fecha de emisión es obligatoria")
    private LocalDate fechaEmision;
    
    @NotNull(message = "La forma de pago es obligatoria")
    private Long formaPagoId;
    
    private String formaPagoDescripcion;
    
    @NotNull
    private Factura.EstadoFactura estado;
    
    @Size(max = 500)
    private String comentarios;
    
    private BigDecimal importeTotal;
    private BigDecimal ivaTotal;
    private BigDecimal sumaTotal;
    
    private List<LineaFacturaDTO> lineas = new ArrayList<>();
}
