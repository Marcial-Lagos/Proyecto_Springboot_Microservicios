package cl.duoc.mspedidos.controller;

import cl.duoc.mspedidos.dto.PedidoDTO;
import cl.duoc.mspedidos.dto.PedidoRequestDTO;
import cl.duoc.mspedidos.model.Pedido;
import cl.duoc.mspedidos.service.PedidoService;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

@WebMvcTest(PedidoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService service;

    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void listarDebeRetornar200YListaDePedidos() throws Exception {
        when(service.listar()).thenReturn(List.of(pedidoDto(1L)));

        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void crearDebeRetornar201ConPedidoValido() throws Exception {
        long usuarioId = Long.parseLong(faker.number().digits(6));
        when(service.crear(any(PedidoRequestDTO.class))).thenReturn(pedidoDto(2L));

        String body = """
                {
                  "usuarioId": %d,
                  "direccionEntrega": "Direccion de prueba 123",
                  "items": [{"productoId": 10, "cantidad": 2}]
                }
                """.formatted(usuarioId);

        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.total").value(24000));
    }

    @Test
    void crearDebeRetornar400ConCamposObligatoriosInvalidos() throws Exception {
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":null,\"direccionEntrega\":\"\",\"items\":[]}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).crear(any(PedidoRequestDTO.class));
    }

    private PedidoDTO pedidoDto(Long id) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(id);
        dto.setUsuarioId(1L);
        dto.setEstado(Pedido.Estado.PENDIENTE);
        dto.setTotal(new BigDecimal("24000"));
        dto.setDireccionEntrega("Direccion de prueba 123");
        return dto;
    }
}