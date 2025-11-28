package es.uvigo.mei.facturaaas.dto;

import es.uvigo.mei.facturaaas.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long id;
    
    @NotBlank(message = "El login es obligatorio")
    @Size(max = 50)
    private String login;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email no válido")
    @Size(max = 100)
    private String email;
    
    @NotNull(message = "El rol es obligatorio")
    private Usuario.Rol rol;
    
    @NotNull
    private Boolean activo;
    
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoAcceso;
}
