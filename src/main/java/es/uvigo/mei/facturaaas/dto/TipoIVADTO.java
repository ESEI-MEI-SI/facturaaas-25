package es.uvigo.mei.facturaaas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoIVADTO {
    private Long id;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100)
    private String descripcion;
    
    @NotNull(message = "El porcentaje es obligatorio")
    @DecimalMin(value = "0.0", message = "El porcentaje debe ser mayor o igual a 0")
    @DecimalMax(value = "100.0", message = "El porcentaje debe ser menor o igual a 100")
    private BigDecimal porcentaje;
    
    @NotNull
    private Boolean activo;
}
