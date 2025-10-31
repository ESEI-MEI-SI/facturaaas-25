package es.uvigo.mei.facturaaas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "datos_facturacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosFacturacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(length = 200)
    private String nombreComercial;
    
    @Column(length = 20)
    private String nif;
    
    @Column(length = 200)
    private String domicilio;
    
    @Column(length = 100)
    private String localidad;
    
    @Column(length = 10)
    private String codigoPostal;
    
    @Column(length = 100)
    private String provincia;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 100)
    private String emailContacto;
    
    @Column(length = 50)
    private String cuentaBancaria;
    
    @Enumerated(EnumType.STRING)
    private TipoEntidad tipo;
    
    @ManyToOne
    @JoinColumn(name = "tipo_iva_defecto_id")
    private TipoIVA tipoIVADefecto;
    
    @ManyToOne
    @JoinColumn(name = "forma_pago_defecto_id")
    private FormaPago formaPagoDefecto;
    
    public enum TipoEntidad {
        PARTICULAR,
        AUTONOMO,
        SOCIEDAD_LIMITADA,
        SOCIEDAD_ANONIMA,
        COOPERATIVA,
        ONG,
        OTRO
    }
}
