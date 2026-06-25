package cl.duoc.msinventario.controller;

import cl.duoc.msinventario.dto.InventarioDTO;
import cl.duoc.msinventario.dto.InventarioRequestDTO;
import cl.duoc.msinventario.service.InventarioService;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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

@WebMvcTest(InventarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventarioService service;

    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void listarDebeRetornar200YJson() throws Exception {
        when(service.listar()).thenReturn(List.of(inventarioDto(1L, 100L)));

        mockMvc.perform(get("/api/v1/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productoId").value(100))
                .andExpect(jsonPath("$[0].bajoStock").value(false));
    }

    @Test
    void crearDebeRetornar201ConDatosValidos() throws Exception {
        long productoId = Long.parseLong(faker.number().digits(6));
        when(service.crear(any(InventarioRequestDTO.class))).thenReturn(inventarioDto(2L, productoId));

        String body = """
                {
                  "productoId": %d,
                  "nombreProducto": "Producto de prueba",
                  "stockActual": 20,
                  "stockMinimo": 5
                }
                """.formatted(productoId);

        mockMvc.perform(post("/api/v1/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.stockActual").value(20));
    }

    @Test
    void crearDebeRetornar400ConDatosInvalidos() throws Exception {
        mockMvc.perform(post("/api/v1/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":null,\"nombreProducto\":\"\",\"stockActual\":-1,\"stockMinimo\":-1}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).crear(any(InventarioRequestDTO.class));
    }

    private InventarioDTO inventarioDto(Long id, Long productoId) {
        InventarioDTO dto = new InventarioDTO();
        dto.setId(id);
        dto.setProductoId(productoId);
        dto.setNombreProducto("Producto de prueba");
        dto.setStockActual(20);
        dto.setStockMinimo(5);
        dto.setUltimaActualizacion(LocalDateTime.now());
        dto.setBajoStock(false);
        return dto;
    }
}