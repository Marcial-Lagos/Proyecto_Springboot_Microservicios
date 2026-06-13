package cl.duoc.msproductos.service;

import cl.duoc.msproductos.dto.ProductoRequestDTO;
import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {
    @Mock
    ProductoRepository repository;

    @InjectMocks
    ProductoService service;

    @Test
    void crearProductoRetornaDatosBasicos() {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre("Completo italiano");
        request.setDescripcion("Producto de prueba");
        request.setPrecio(new BigDecimal("2500"));
        request.setStock(20);
        request.setCategoria("COMIDA");

        when(repository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto producto = invocation.getArgument(0);
            producto.setId(1L);
            return producto;
        });

        var response = service.crear(request);

        assertEquals(1L, response.getId());
        assertEquals("Completo italiano", response.getNombre());
        assertEquals(new BigDecimal("2500"), response.getPrecio());
    }
}
