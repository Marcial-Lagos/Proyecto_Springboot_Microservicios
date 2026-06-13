package cl.duoc.mspedidos.service;

import cl.duoc.mspedidos.client.ProductoClient;
import cl.duoc.mspedidos.client.ProductoResponse;
import cl.duoc.mspedidos.dto.ItemPedidoDTO;
import cl.duoc.mspedidos.dto.PedidoRequestDTO;
import cl.duoc.mspedidos.model.Pedido;
import cl.duoc.mspedidos.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {
    @Mock
    PedidoRepository repository;

    @Mock
    ProductoClient productoClient;

    @InjectMocks
    PedidoService service;

    @Test
    void crearPedidoConsultaProductoYCalculaTotal() {
        ProductoResponse producto = new ProductoResponse();
        producto.setId(10L);
        producto.setNombre("Pizza familiar");
        producto.setPrecio(new BigDecimal("12000"));
        producto.setStock(5);
        producto.setActivo(true);

        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setProductoId(10L);
        item.setCantidad(2);

        PedidoRequestDTO request = new PedidoRequestDTO();
        request.setUsuarioId(1L);
        request.setDireccionEntrega("Av. Siempre Viva 123");
        request.setItems(List.of(item));

        when(productoClient.obtenerProducto(10L)).thenReturn(producto);
        when(repository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido pedido = invocation.getArgument(0);
            pedido.setId(100L);
            return pedido;
        });

        var response = service.crear(request);

        assertEquals(100L, response.getId());
        assertEquals(new BigDecimal("24000"), response.getTotal());
        assertEquals("Pizza familiar", response.getItems().get(0).getNombreProducto());
        verify(productoClient).descontarStock(10L, 2);
    }
}
