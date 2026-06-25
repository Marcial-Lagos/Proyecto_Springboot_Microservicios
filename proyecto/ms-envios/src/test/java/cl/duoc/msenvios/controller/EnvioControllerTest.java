package cl.duoc.msenvios.controller;

import cl.duoc.msenvios.dto.EnvioDTO;
import cl.duoc.msenvios.dto.EnvioRequestDTO;
import cl.duoc.msenvios.model.Envio;
import cl.duoc.msenvios.service.EnvioService;
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

@WebMvcTest(EnvioController.class)
@AutoConfigureMockMvc(addFilters = false)
class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnvioService service;

    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void listarDebeRetornar200YEnvios() throws Exception {
        when(service.listar()).thenReturn(List.of(envioDto(1L)));

        mockMvc.perform(get("/api/v1/envios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void crearDebeRetornar201ConDatosValidos() throws Exception {
        long pedidoId = Long.parseLong(faker.number().digits(6));
        when(service.crear(any(EnvioRequestDTO.class))).thenReturn(envioDto(2L));

        String body = """
                {"pedidoId":%d,"direccionOrigen":"Origen 123","direccionDestino":"Destino 456"}
                """.formatted(pedidoId);

        mockMvc.perform(post("/api/v1/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.codigoSeguimiento").value("ENV-PRUEBA"));
    }

    @Test
    void crearConDatosInvalidosDebeRetornar400() throws Exception {
        mockMvc.perform(post("/api/v1/envios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pedidoId\":null,\"direccionOrigen\":\"\",\"direccionDestino\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).crear(any(EnvioRequestDTO.class));
    }

    private EnvioDTO envioDto(Long id) {
        EnvioDTO dto = new EnvioDTO();
        dto.setId(id);
        dto.setPedidoId(1L);
        dto.setDireccionOrigen("Origen 123");
        dto.setDireccionDestino("Destino 456");
        dto.setEstado(Envio.EstadoEnvio.PENDIENTE);
        dto.setCodigoSeguimiento("ENV-PRUEBA");
        return dto;
    }
}