package cl.duoc.mscupones.controller;

import cl.duoc.mscupones.dto.CuponDTO;
import cl.duoc.mscupones.dto.CuponRequestDTO;
import cl.duoc.mscupones.model.Cupon;
import cl.duoc.mscupones.service.CuponService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CuponController.class)
@AutoConfigureMockMvc(addFilters = false)
class CuponControllerTest {
    @Autowired MockMvc mockMvc;
    @MockitoBean CuponService service;
    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void listarRetorna200() throws Exception {
        when(service.listar()).thenReturn(List.of(dto(1L)));
        mockMvc.perform(get("/api/v1/cupones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("PROMO10"));
    }

    @Test
    void crearRetorna201() throws Exception {
        when(service.crear(any(CuponRequestDTO.class))).thenReturn(dto(2L));
        String codigo = "PROMO" + faker.number().digits(5);
        String body = """
                {"codigo":"%s","tipo":"PORCENTAJE","valor":10.00,"montoMinimo":1000.00,"usosMaximos":10}
                """.formatted(codigo);
        mockMvc.perform(post("/api/v1/cupones").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void validarRetorna200() throws Exception {
        when(service.validar("PROMO10", new BigDecimal("20000")))
                .thenReturn(Map.of("valido", true, "descuento", new BigDecimal("2000")));
        mockMvc.perform(get("/api/v1/cupones/PROMO10/validar").param("monto", "20000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valido").value(true));
    }

    private CuponDTO dto(Long id) {
        CuponDTO dto = new CuponDTO();
        dto.setId(id); dto.setCodigo("PROMO10"); dto.setTipo(Cupon.TipoDescuento.PORCENTAJE);
        dto.setValor(new BigDecimal("10")); dto.setActivo(true);
        return dto;
    }
}