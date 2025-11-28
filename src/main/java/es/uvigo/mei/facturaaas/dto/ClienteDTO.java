package es.uvigo.mei.facturaaas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private Long id;
    private Long usuarioId;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200)
    private String nombre;
    
    @NotBlank(message = "El NIF es obligatorio")
    @Size(max = 20)
    private String nif;
    
    @Size(max = 200)
    private String domicilio;
    
    @Size(max = 100)
    private String localidad;
    
    @Size(max = 10)
    private String codigoPostal;
    
    @Size(max = 100)
    private String provincia;
    
    @Email
    @Size(max = 100)
    private String email;
    
    @Size(max = 20)
    private String telefono;
    
    @Size(max = 50)
    private String cuentaBancaria;
}
