package cl.duoc.msresenas.controller;

import cl.duoc.msresenas.dto.ResenaDTO;
import cl.duoc.msresenas.dto.ResenaRequestDTO;
import cl.duoc.msresenas.service.ResenaService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResenaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResenaControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean ResenaService service;
    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void listarPorProductoRetorna200() throws Exception {
        when(service.listarPorProducto(1L)).thenReturn(List.of(dto(1L)));
        mockMvc.perform(get("/api/v1/resenas/producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].calificacion").value(5));
    }

    @Test
    void crearRetorna201() throws Exception {
        when(service.crear(any(ResenaRequestDTO.class))).thenReturn(dto(2L));
        String comentario = faker.lorem().sentence();
        String body = """
                {"usuarioId":1,"productoId":1,"pedidoId":1,"calificacion":5,"comentario":"%s"}
                """.formatted(comentario);
        mockMvc.perform(post("/api/v1/resenas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void eliminarRetorna204() throws Exception {
        doNothing().when(service).eliminar(3L);
        mockMvc.perform(delete("/api/v1/resenas/3"))
                .andExpect(status().isNoContent());
    }

    private ResenaDTO dto(Long id) {
        ResenaDTO dto = new ResenaDTO();
        dto.setId(id); dto.setUsuarioId(1L); dto.setProductoId(1L); dto.setPedidoId(1L);
        dto.setCalificacion(5); dto.setComentario("Excelente");
        return dto;
    }
}