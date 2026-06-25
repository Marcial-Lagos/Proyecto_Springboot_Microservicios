package cl.duoc.msusuarios;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifica que el microservicio completo pueda iniciar con el perfil test.
 * Este perfil reemplaza MySQL por H2 y desactiva Discovery/Swagger.
 */
@SpringBootTest
@ActiveProfiles("test")
class MsUsuariosApplicationTests {

    @Test
    void contextLoads() {
        // La prueba aprueba si Spring Boot inicia todos los beans sin errores.
    }
}