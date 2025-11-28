package es.uvigo.mei.facturaaas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormaPagoDTO {
    private Long id;
    
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100)
    private String descripcion;
    
    @NotNull(message = "El número de pagos es obligatorio")
    @Min(value = 1, message = "El número de pagos debe ser al menos 1")
    private Integer numeroPagos;
    
    @NotNull(message = "La periodicidad es obligatoria")
    @Min(value = 0, message = "La periodicidad debe ser mayor o igual a 0")
    private Integer periodicidadDias;
    
    @NotNull
    private Boolean activa;
}
