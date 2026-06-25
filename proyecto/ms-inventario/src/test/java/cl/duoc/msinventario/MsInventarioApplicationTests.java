package cl.duoc.msinventario;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MsInventarioApplicationTests {

    @Test
    void contextLoads() {
        // La prueba aprueba si Spring inicia el contexto con el perfil test.
    }
}