package cl.duoc.msauth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MsAuthApplicationTests {

    @Test
    void contextLoads() {
        // Aprueba si el microservicio completo inicia con el perfil test.
    }
}