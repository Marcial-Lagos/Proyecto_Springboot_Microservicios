package cl.duoc.mspagos.controller;

import cl.duoc.mspagos.dto.PagoDTO;
import cl.duoc.mspagos.dto.PagoRequestDTO;
import cl.duoc.mspagos.model.Pago;
import cl.duoc.mspagos.service.PagoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PagoControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean PagoService service;

    @Test
    void listarRetorna200() throws Exception {
        when(service.listar()).thenReturn(List.of(dto(1L)));
        mockMvc.perform(get("/api/v1/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("APROBADO"));
    }

    @Test
    void procesarRetorna201() throws Exception {
        when(service.procesar(any(PagoRequestDTO.class))).thenReturn(dto(2L));
        mockMvc.perform(post("/api/v1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pedidoId\":5,\"usuarioId\":1,\"monto\":12000.00,\"metodo\":\"WEBPAY\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.referencia").value("REF-PRUEBA"));
    }

    private PagoDTO dto(Long id) {
        PagoDTO dto = new PagoDTO();
        dto.setId(id);
        dto.setPedidoId(5L);
        dto.setUsuarioId(1L);
        dto.setMonto(new BigDecimal("12000.00"));
        dto.setMetodo(Pago.MetodoPago.WEBPAY);
        dto.setEstado(Pago.EstadoPago.APROBADO);
        dto.setReferencia("REF-PRUEBA");
        return dto;
    }
}