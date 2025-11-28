package es.uvigo.mei.facturaaas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String tipo = "Bearer";
    private UsuarioDTO usuario;
    
    public AuthResponseDTO(String token, UsuarioDTO usuario) {
        this.token = token;
        this.usuario = usuario;
    }
}
