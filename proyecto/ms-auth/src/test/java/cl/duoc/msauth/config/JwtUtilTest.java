package cl.duoc.msauth.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Prueba de seguridad real: no usa mocks; genera y valida JWT con JwtUtil.
 */
class JwtUtilTest {

    private final JwtUtil jwtUtil = jwtUtilDePrueba();

    @Test
    void tokenGeneradoDebeConservarEmailRolYSerValido() {
        String token = jwtUtil.generateToken("cliente@test.cl", "CLIENTE");

        assertTrue(jwtUtil.isValid(token));
        assertEquals("cliente@test.cl", jwtUtil.getEmail(token));
        assertEquals("CLIENTE", jwtUtil.getRol(token));
    }

    @Test
    void tokenAlteradoDebeSerInvalido() {
        String token = jwtUtil.generateToken("cliente@test.cl", "CLIENTE");

        assertFalse(jwtUtil.isValid(token + "alterado"));
    }

    private JwtUtil jwtUtilDePrueba() {
        JwtUtil util = new JwtUtil();
        ReflectionTestUtils.setField(util, "secret", "clave-de-prueba-para-jwt-con-una-longitud-segura-2026");
        ReflectionTestUtils.setField(util, "expiration", 3_600_000L);
        return util;
    }
}