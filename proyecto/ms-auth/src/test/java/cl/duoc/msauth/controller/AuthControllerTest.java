package cl.duoc.msauth.controller;

import cl.duoc.msauth.dto.AuthResponse;
import cl.duoc.msauth.dto.LoginRequest;
import cl.duoc.msauth.dto.RegisterRequest;
import cl.duoc.msauth.service.AuthService;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void loginDebeRetornar200ConToken() throws Exception {
        String email = "usuario" + faker.number().digits(6) + "@test.cl";
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("jwt-prueba", email, "CLIENTE"));

        String body = """
                {"email":"%s","password":"secreto123"}
                """.formatted(email);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-prueba"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    void registerDebeRetornar201ConRequestValido() throws Exception {
        String email = "registro" + faker.number().digits(6) + "@test.cl";
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("jwt-nuevo", email, "CLIENTE"));

        String body = """
                {"nombre":"Usuario Prueba","email":"%s","password":"secreto123"}
                """.formatted(email);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-nuevo"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void registerDebeRetornar400ConDatosInvalidos() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"email\":\"correo-invalido\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }
}