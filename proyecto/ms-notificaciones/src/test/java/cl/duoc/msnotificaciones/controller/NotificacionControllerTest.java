package cl.duoc.msnotificaciones.controller;

import cl.duoc.msnotificaciones.dto.NotificacionDTO;
import cl.duoc.msnotificaciones.dto.NotificacionRequestDTO;
import cl.duoc.msnotificaciones.model.Notificacion;
import cl.duoc.msnotificaciones.service.NotificacionService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificacionControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean NotificacionService service;
    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void listarPorUsuarioRetorna200() throws Exception {
        when(service.listarPorUsuario(1L)).thenReturn(List.of(dto(1L, false)));
        mockMvc.perform(get("/api/v1/notificaciones/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leida").value(false));
    }

    @Test
    void crearRetorna201() throws Exception {
        when(service.crear(any(NotificacionRequestDTO.class))).thenReturn(dto(2L, false));
        String titulo = faker.lorem().sentence(3);
        String body = """
                {"usuarioId":1,"titulo":"%s","mensaje":"Mensaje de prueba","tipo":"PEDIDO"}
                """.formatted(titulo);
        mockMvc.perform(post("/api/v1/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void marcarLeidaRetorna200() throws Exception {
        when(service.marcarLeida(3L)).thenReturn(dto(3L, true));
        mockMvc.perform(patch("/api/v1/notificaciones/3/leer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leida").value(true));
    }

    private NotificacionDTO dto(Long id, boolean leida) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(id); dto.setUsuarioId(1L); dto.setTitulo("Aviso");
        dto.setMensaje("Mensaje"); dto.setTipo(Notificacion.TipoNotificacion.PEDIDO); dto.setLeida(leida);
        return dto;
    }
}