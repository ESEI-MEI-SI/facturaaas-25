package es.uvigo.mei.facturaaas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreateDTO {
    @NotBlank(message = "El login es obligatorio")
    @Size(max = 50)
    private String login;
    
    @NotBlank(message = "El password es obligatorio")
    @Size(min = 6, message = "El password debe tener al menos 6 caracteres")
    private String password;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email no válido")
    @Size(max = 100)
    private String email;
}
