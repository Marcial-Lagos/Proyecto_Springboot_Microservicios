package cl.duoc.msproductos.service;

import cl.duoc.msproductos.client.InventarioClient;
import cl.duoc.msproductos.dto.ProductoDTO;
import cl.duoc.msproductos.dto.ProductoRequestDTO;
import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock private ProductoRepository repository;
    @Mock private InventarioClient inventarioClient;
    @InjectMocks private ProductoService service;

    @Test
    void listarDevuelveSoloProductosActivosEntregadosPorRepositorio() {
        Producto producto = producto(1L, 10, true);
        when(repository.findByActivoTrue()).thenReturn(List.of(producto));

        List<ProductoDTO> response = service.listar();

        assertEquals(1, response.size());
        assertEquals("Pizza familiar", response.get(0).getNombre());
        assertTrue(response.get(0).isActivo());
    }

    @Test
    void buscarPorIdDevuelveProductoExistente() {
        when(repository.findById(2L)).thenReturn(Optional.of(producto(2L, 8, true)));

        ProductoDTO response = service.buscarPorId(2L);

        assertEquals(2L, response.getId());
        assertEquals(8, response.getStock());
    }

    @Test
    void crearProductoPersisteDatosYQuedaActivo() {
        ProductoRequestDTO request = productoRequest();
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto producto = invocation.getArgument(0);
            producto.setId(3L);
            return producto;
        });

        ProductoDTO response = service.crear(request);

        assertEquals(3L, response.getId());
        assertEquals("Pizza familiar", response.getNombre());
        assertTrue(response.isActivo());
    }

    @Test
    void descontarStockActualizaProductoYSincronizaInventario() {
        Producto producto = producto(4L, 10, true);
        when(repository.findById(4L)).thenReturn(Optional.of(producto));

        service.descontarStock(4L, 3);

        assertEquals(7, producto.getStock());
        verify(repository).save(producto);
        verify(inventarioClient).ajustarStock(4L, -3);
    }

    @Test
    void descontarStockInsuficienteNoPersisteNiSincroniza() {
        when(repository.findById(5L)).thenReturn(Optional.of(producto(5L, 2, true)));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.descontarStock(5L, 3));

        assertEquals("Stock insuficiente", exception.getMessage());
        verify(repository, never()).save(any(Producto.class));
        verifyNoInteractions(inventarioClient);
    }

    private Producto producto(Long id, int stock, boolean activo) {
        return Producto.builder().id(id).nombre("Pizza familiar").descripcion("Pizza de prueba")
                .precio(new BigDecimal("12000")).stock(stock).categoria("COMIDA").activo(activo).build();
    }

    private ProductoRequestDTO productoRequest() {
        ProductoRequestDTO request = new ProductoRequestDTO();
        request.setNombre("Pizza familiar");
        request.setDescripcion("Pizza de prueba");
        request.setPrecio(new BigDecimal("12000"));
        request.setStock(10);
        request.setCategoria("COMIDA");
        return request;
    }
}
