package cl.duoc.msauth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.flyway.enabled=false",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "jwt.expiration=3600000"
})
@ActiveProfiles("test")
class MsAuthApplicationTests {

    @Test
    void contextLoads() {
        // Aprueba si el microservicio completo inicia con el perfil test.
    }
}