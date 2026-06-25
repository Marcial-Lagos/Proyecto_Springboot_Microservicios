package cl.duoc.msproductos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MsProductosApplicationTests {

    @Test
    void contextLoads() {
        // Aprueba si Spring puede iniciar el microservicio con el perfil test.
    }
}