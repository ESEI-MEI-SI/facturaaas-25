package es.uvigo.mei.facturaaas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "El login es obligatorio")
    private String login;
    
    @NotBlank(message = "El password es obligatorio")
    private String password;
}
