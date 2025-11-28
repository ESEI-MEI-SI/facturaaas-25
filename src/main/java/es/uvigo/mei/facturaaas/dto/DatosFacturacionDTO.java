package es.uvigo.mei.facturaaas.dto;

import es.uvigo.mei.facturaaas.model.DatosFacturacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosFacturacionDTO {
    private Long id;
    private Long usuarioId;
    
    @Size(max = 200)
    private String nombreComercial;
    
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
    
    @Size(max = 20)
    private String telefono;
    
    @Email
    @Size(max = 100)
    private String emailContacto;
    
    @Size(max = 50)
    private String cuentaBancaria;
    
    private DatosFacturacion.TipoEntidad tipo;
    private Long tipoIVADefectoId;
    private Long formaPagoDefectoId;
}
