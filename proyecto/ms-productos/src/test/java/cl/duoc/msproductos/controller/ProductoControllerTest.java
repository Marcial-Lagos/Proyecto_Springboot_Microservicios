package cl.duoc.msproductos.controller;

import cl.duoc.msproductos.dto.ProductoDTO;
import cl.duoc.msproductos.dto.ProductoRequestDTO;
import cl.duoc.msproductos.service.ProductoService;
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

@WebMvcTest(ProductoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService service;

    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void listarDebeRetornar200YProductosActivos() throws Exception {
        when(service.listar()).thenReturn(List.of(productoDto(1L)));

        mockMvc.perform(get("/api/v1/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].activo").value(true));
    }

    @Test
    void crearDebeRetornar201ConRequestValido() throws Exception {
        String categoria = "CAT" + faker.number().digits(4);
        when(service.crear(any(ProductoRequestDTO.class))).thenReturn(productoDto(2L));

        String body = """
                {
                  "nombre": "Producto de prueba",
                  "descripcion": "Descripcion valida para la prueba",
                  "precio": 12500.00,
                  "stock": 10,
                  "categoria": "%s",
                  "imagenUrl": "https://example.com/producto.jpg"
                }
                """.formatted(categoria);

        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.activo").value(true));
    }

    @Test
    void crearDebeRetornar400ConPrecioYStockInvalidos() throws Exception {
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"precio\":-1,\"stock\":-1}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).crear(any(ProductoRequestDTO.class));
    }

    private ProductoDTO productoDto(Long id) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(id);
        dto.setNombre("Producto de prueba");
        dto.setDescripcion("Descripcion de prueba");
        dto.setPrecio(new BigDecimal("12500.00"));
        dto.setStock(10);
        dto.setCategoria("COMIDA");
        dto.setImagenUrl("https://example.com/producto.jpg");
        dto.setActivo(true);
        return dto;
    }
}