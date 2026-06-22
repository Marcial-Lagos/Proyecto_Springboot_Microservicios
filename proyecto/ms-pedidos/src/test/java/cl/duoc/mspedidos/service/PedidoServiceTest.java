package cl.duoc.mspedidos.service;

import cl.duoc.mspedidos.client.ProductoClient;
import cl.duoc.mspedidos.client.ProductoResponse;
import cl.duoc.mspedidos.dto.ItemPedidoDTO;
import cl.duoc.mspedidos.dto.PedidoDTO;
import cl.duoc.mspedidos.dto.PedidoRequestDTO;
import cl.duoc.mspedidos.exception.ResourceNotFoundException;
import cl.duoc.mspedidos.model.Pedido;
import cl.duoc.mspedidos.repository.PedidoRepository;
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
class PedidoServiceTest {

    @Mock private PedidoRepository repository;
    @Mock private ProductoClient productoClient;
    @InjectMocks private PedidoService service;

    @Test
    void crearPedidoConsultaProductoCalculaTotalYDescuentaStock() {
        PedidoRequestDTO request = pedidoRequest(10L, 2);
        when(productoClient.obtenerProducto(10L)).thenReturn(producto(10L, 5, true));
        when(repository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(100L);
            return pedido;
        });

        PedidoDTO response = service.crear(request);

        assertEquals(100L, response.getId());
        assertEquals(new BigDecimal("24000"), response.getTotal());
        verify(productoClient).descontarStock(10L, 2);
    }

    @Test
    void crearPedidoConProductoInactivoNoPersiste() {
        PedidoRequestDTO request = pedidoRequest(10L, 1);
        when(productoClient.obtenerProducto(10L)).thenReturn(producto(10L, 5, false));

        assertThrows(ResourceNotFoundException.class, () -> service.crear(request));

        verify(repository, never()).save(any(Pedido.class));
        verify(productoClient, never()).descontarStock(anyLong(), anyInt());
    }

    @Test
    void crearPedidoConStockInsuficienteNoPersiste() {
        PedidoRequestDTO request = pedidoRequest(10L, 3);
        when(productoClient.obtenerProducto(10L)).thenReturn(producto(10L, 2, true));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.crear(request));

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(repository, never()).save(any(Pedido.class));
    }

    @Test
    void cambiarEstadoActualizaPedidoExistente() {
        Pedido pedido = Pedido.builder().id(20L).usuarioId(1L).estado(Pedido.Estado.PENDIENTE)
                .total(new BigDecimal("5000")).direccionEntrega("Av. Principal 123").build();
        when(repository.findById(20L)).thenReturn(Optional.of(pedido));
        when(repository.save(pedido)).thenReturn(pedido);

        PedidoDTO response = service.cambiarEstado(20L, "en_camino");

        assertEquals(Pedido.Estado.EN_CAMINO, response.getEstado());
        verify(repository).save(pedido);
    }

    @Test
    void buscarPedidoInexistenteLanzaExcepcion() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(999L));
    }

    private PedidoRequestDTO pedidoRequest(Long productoId, int cantidad) {
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProductoId(productoId);
        item.setCantidad(cantidad);
        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setUsuarioId(1L);
        request.setDireccionEntrega("Av. Principal 123");
        request.setItems(List.of(item));
        return request;
    }

    private ProductoResponse producto(Long id, int stock, boolean activo) {
        ProductoResponse response = new ProductoResponse();
        response.setId(id);
        response.setNombre("Pizza familiar");
        response.setPrecio(new BigDecimal("12000"));
        response.setStock(stock);
        response.setActivo(activo);
        return response;
    }
}
