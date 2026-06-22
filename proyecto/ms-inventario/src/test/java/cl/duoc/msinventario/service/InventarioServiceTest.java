package cl.duoc.msinventario.service;

import cl.duoc.msinventario.dto.InventarioDTO;
import cl.duoc.msinventario.dto.InventarioRequestDTO;
import cl.duoc.msinventario.exception.ResourceNotFoundException;
import cl.duoc.msinventario.model.Inventario;
import cl.duoc.msinventario.repository.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock private InventarioRepository repository;
    @InjectMocks private InventarioService service;

    @Test
    void listarBajoStockFiltraSegunStockMinimo() {
        Inventario bajo = inventario(1L, 10, 5, 5);
        Inventario normal = inventario(2L, 11, 8, 5);
        when(repository.findAll()).thenReturn(List.of(bajo, normal));

        List<InventarioDTO> response = service.listarBajoStock();

        assertEquals(1, response.size());
        assertEquals(10L, response.get(0).getProductoId());
        assertTrue(response.get(0).isBajoStock());
    }

    @Test
    void buscarPorProductoDevuelveInventarioExistente() {
        when(repository.findByProductoId(10L)).thenReturn(Optional.of(inventario(1L, 10, 8, 5)));

        InventarioDTO response = service.buscarPorProducto(10L);

        assertEquals("Pizza familiar", response.getNombreProducto());
    }

    @Test
    void crearInventarioPersisteDatos() {
        InventarioRequestDTO request = request();
        when(repository.save(any(Inventario.class))).thenAnswer(invocation -> {
            Inventario inventory = invocation.getArgument(0);
            inventory.setId(3L);
            return inventory;
        });

        InventarioDTO response = service.crear(request);

        assertEquals(3L, response.getId());
        assertEquals(20, response.getStockActual());
        assertNotNull(response.getUltimaActualizacion());
    }

    @Test
    void ajustarIncrementaStockYActualizaFecha() {
        Inventario inventory = inventario(1L, 10, 5, 5);
        when(repository.findByProductoId(10L)).thenReturn(Optional.of(inventory));
        when(repository.save(inventory)).thenReturn(inventory);

        InventarioDTO response = service.ajustar(10L, 4);

        assertEquals(9, response.getStockActual());
        assertNotNull(response.getUltimaActualizacion());
    }

    @Test
    void ajustarProductoInexistenteLanzaExcepcion() {
        when(repository.findByProductoId(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.ajustar(999L, 1));
    }

    private Inventario inventario(Long id, long productoId, int stockActual, int stockMinimo) {
        return Inventario.builder().id(id).productoId(productoId).nombreProducto("Pizza familiar")
                .stockActual(stockActual).stockMinimo(stockMinimo).build();
    }

    private InventarioRequestDTO request() {
        InventarioRequestDTO request = new InventarioRequestDTO();
        request.setProductoId(10L);
        request.setNombreProducto("Pizza familiar");
        request.setStockActual(20);
        request.setStockMinimo(5);
        return request;
    }
}
