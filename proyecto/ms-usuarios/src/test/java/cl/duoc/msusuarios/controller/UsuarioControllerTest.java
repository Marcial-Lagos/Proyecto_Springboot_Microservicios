package cl.duoc.msusuarios.controller;

import cl.duoc.msusuarios.dto.UsuarioDTO;
import cl.duoc.msusuarios.dto.UsuarioRequestDTO;
import cl.duoc.msusuarios.model.Usuario;
import cl.duoc.msusuarios.service.UsuarioService;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas MVC aisladas: se valida HTTP, JSON y Bean Validation.
 * UsuarioService se reemplaza por un mock porque su lógica se cubre en UsuarioServiceTest.
 */
@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService service;

    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void listarDebeRetornar200YJson() throws Exception {
        // Given
        when(service.listar()).thenReturn(List.of(usuarioDto(1L, "cliente@test.cl")));

        // When + Then
        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("cliente@test.cl"));
    }

    @Test
    void crearDebeRetornar201ConRequestValido() throws Exception {
        // Given
        String email = "usuario" + faker.number().digits(6) + "@test.cl";
        when(service.crear(any(UsuarioRequestDTO.class))).thenReturn(usuarioDto(2L, email));

        String body = """
                {
                  "nombre": "Usuario Prueba",
                  "email": "%s",
                  "password": "secreto123",
                  "telefono": "912345678",
                  "direccion": "Direccion de prueba 123"
                }
                """.formatted(email);

        // When + Then
        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void crearDebeRetornar400ConDatosInvalidos() throws Exception {
        // When + Then: el controller debe detener la solicitud antes de llamar al service.
        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"email\":\"correo-invalido\",\"password\":\"123\"}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).crear(any(UsuarioRequestDTO.class));
    }

    private UsuarioDTO usuarioDto(Long id, String email) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(id);
        dto.setNombre("Usuario Prueba");
        dto.setEmail(email);
        dto.setRol(Usuario.Rol.CLIENTE);
        dto.setTelefono("912345678");
        dto.setDireccion("Direccion de prueba 123");
        return dto;
    }
}