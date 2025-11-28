package es.uvigo.mei.facturaaas.dto;

import es.uvigo.mei.facturaaas.model.Pago;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private Long id;
    private Long facturaId;
    private String numeroFactura;
    private String clienteNombre;
    private String clienteNif;
    
    @NotNull
    private Integer numeroPago;
    
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate fechaVencimiento;
    
    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe debe ser mayor que 0")
    private BigDecimal importe;
    
    @NotNull
    private Pago.EstadoPago estado;
    
    private LocalDate fechaPago;
}
