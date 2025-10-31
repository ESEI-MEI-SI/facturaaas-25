package es.uvigo.mei.facturaaas.config;

import es.uvigo.mei.facturaaas.model.*;
import es.uvigo.mei.facturaaas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final UsuarioRepository usuarioRepository;
    private final TipoIVARepository tipoIVARepository;
    private final FormaPagoRepository formaPagoRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Crear usuario administrador por defecto
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombre("Administrador");
            admin.setEmail("admin@facturaaas.com");
            admin.setRol(Usuario.Rol.ADMINISTRADOR);
            admin.setActivo(true);
            usuarioRepository.save(admin);
            
            Usuario usuario = new Usuario();
            usuario.setLogin("user");
            usuario.setPassword(passwordEncoder.encode("user123"));
            usuario.setNombre("Usuario Demo");
            usuario.setEmail("user@facturaaas.com");
            usuario.setRol(Usuario.Rol.USUARIO);
            usuario.setActivo(true);
            usuarioRepository.save(usuario);
            
            System.out.println("✓ Usuarios creados:");
            System.out.println("  - admin / admin123 (ADMINISTRADOR)");
            System.out.println("  - user / user123 (USUARIO)");
        }
        
        // Crear tipos de IVA por defecto
        if (tipoIVARepository.count() == 0) {
            TipoIVA ivaNormal = new TipoIVA();
            ivaNormal.setDescripcion("IVA Normal");
            ivaNormal.setPorcentaje(new BigDecimal("21.00"));
            ivaNormal.setActivo(true);
            tipoIVARepository.save(ivaNormal);
            
            TipoIVA ivaReducido = new TipoIVA();
            ivaReducido.setDescripcion("IVA Reducido");
            ivaReducido.setPorcentaje(new BigDecimal("10.00"));
            ivaReducido.setActivo(true);
            tipoIVARepository.save(ivaReducido);
            
            TipoIVA ivaSuperReducido = new TipoIVA();
            ivaSuperReducido.setDescripcion("IVA Superreducido");
            ivaSuperReducido.setPorcentaje(new BigDecimal("4.00"));
            ivaSuperReducido.setActivo(true);
            tipoIVARepository.save(ivaSuperReducido);
            
            TipoIVA sinIva = new TipoIVA();
            sinIva.setDescripcion("Sin IVA");
            sinIva.setPorcentaje(new BigDecimal("0.00"));
            sinIva.setActivo(true);
            tipoIVARepository.save(sinIva);
            
            System.out.println("✓ Tipos de IVA creados: 21%, 10%, 4%, 0%");
        }
        
        // Crear formas de pago por defecto para el usuario demo
        if (formaPagoRepository.count() == 0) {
            Usuario usuarioDemo = usuarioRepository.findByLogin("user")
                .orElseThrow(() -> new RuntimeException("Usuario demo no encontrado"));
            
            FormaPago contado = new FormaPago();
            contado.setUsuario(usuarioDemo);
            contado.setDescripcion("Contado");
            contado.setNumeroPagos(1);
            contado.setPeriodicidadDias(0);
            contado.setActiva(true);
            formaPagoRepository.save(contado);
            
            FormaPago transferencia30 = new FormaPago();
            transferencia30.setUsuario(usuarioDemo);
            transferencia30.setDescripcion("Transferencia a 30 días");
            transferencia30.setNumeroPagos(1);
            transferencia30.setPeriodicidadDias(30);
            transferencia30.setActiva(true);
            formaPagoRepository.save(transferencia30);
            
            FormaPago transferencia306090 = new FormaPago();
            transferencia306090.setUsuario(usuarioDemo);
            transferencia306090.setDescripcion("Transferencias a 30-60-90 días");
            transferencia306090.setNumeroPagos(3);
            transferencia306090.setPeriodicidadDias(30);
            transferencia306090.setActiva(true);
            formaPagoRepository.save(transferencia306090);
            
            System.out.println("✓ Formas de pago creadas para usuario demo: Contado, 30 días, 30-60-90 días");
        }
    }
}
