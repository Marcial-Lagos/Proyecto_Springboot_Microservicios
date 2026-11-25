package cl.duoc.msauth.config;

import cl.duoc.msauth.model.AuthUser;
import cl.duoc.msauth.repository.AuthUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdministradorInicial {
    @Bean
    CommandLineRunner configurarAdministrador(AuthUserRepository usuarios, BCryptPasswordEncoder encoder,
            @Value("${NEXUS_ADMIN_EMAIL:}") String correo, @Value("${NEXUS_ADMIN_PASSWORD:}") String clave) {
        return args -> {
            if (correo.isBlank() && clave.isBlank()) return;
            if (!correo.contains("@") || clave.length() < 12)
                throw new IllegalStateException("Configura el correo y una clave administrativa de al menos 12 caracteres");
            if (!usuarios.existsByEmail(correo))
                usuarios.save(AuthUser.builder().email(correo).password(encoder.encode(clave)).rol(AuthUser.Rol.ADMIN).build());
        };
    }
}
