package es.uvigo.mei.facturaaas.config;

import es.uvigo.mei.facturaaas.model.Usuario;
import es.uvigo.mei.facturaaas.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Inicializador para entorno de producción.
 * Crea únicamente el usuario administrador si no existe.
 * La contraseña se obtiene de la variable de entorno ADMIN_PASSWORD.
 */
@Component
@Profile("prod")  // Solo se ejecuta con perfil "prod"
public class AdminInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${admin.password}")
    private String adminPassword;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("=== Inicializando datos de producción ===");
        
        // Verificar que se ha configurado la contraseña del admin
        if (adminPassword == null || adminPassword.isBlank()) {
            logger.error("ERROR: La variable de entorno ADMIN_PASSWORD no está configurada");
            throw new IllegalStateException("ADMIN_PASSWORD es requerida en producción");
        }
        
        // Crear usuario administrador si no existe
        if (usuarioRepository.findByLogin("admin").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail("admin@facturaaas.com");
            admin.setNombre("Administrador");
            admin.setRol(Usuario.Rol.ADMINISTRADOR);  // Usar Usuario.Rol en lugar de Rol
            admin.setActivo(true);
            admin.setFechaCreacion(LocalDateTime.now());
            
            usuarioRepository.save(admin);
            logger.info("Usuario administrador creado exitosamente");
        } else {
            logger.info("Usuario administrador ya existe, omitiendo creación");
        }
        
        logger.info("=== Inicialización de producción completada ===");
    }
}

