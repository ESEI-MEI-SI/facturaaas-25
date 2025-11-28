package es.uvigo.mei.facturaaas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineaFacturaDTO {
    private Long id;
    private Long facturaId;
    private Integer numeroLinea;
    
    @NotBlank(message = "El concepto es obligatorio")
    @Size(max = 300)
    private String concepto;
    
    @NotNull(message = "La cantidad es obligatoria")
    @DecimalMin(value = "0.0", message = "La cantidad debe ser mayor que 0")
    private BigDecimal cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    private BigDecimal precioUnitario;
    
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private BigDecimal porcentajeDescuento;
    
    @NotNull(message = "El tipo de IVA es obligatorio")
    private Long tipoIVAId;
    
    private String tipoIVADescripcion;
    private BigDecimal tipoIVAPorcentaje;
    
    private BigDecimal importeTotal;
}
