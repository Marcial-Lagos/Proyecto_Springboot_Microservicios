package cl.duoc.mspedidos.service;

import cl.duoc.mspedidos.client.ProductoClient;
import cl.duoc.mspedidos.client.PagoClient;
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
    @Mock private PagoClient pagos;
    @InjectMocks private PedidoService service;

    @Test
    void crearPedidoConsultaProductoCalculaTotalYDescuentaStock() {
        PedidoRequestDTO request = pedidoRequest(10L, 2);
        request.setSolicitudId(java.util.UUID.randomUUID());
        request.setTipoEntrega("domicilio");
        request.setMetodoPago("TARJETA");
        ItemPedidoDTO reservado = request.getItems().getFirst();
        reservado.setNombreProducto("Teclado");
        reservado.setPrecioUnitario(new BigDecimal("12000"));
        String clave = "1-" + request.getSolicitudId();
        when(productoClient.reservar(clave, request.getItems())).thenReturn(List.of(reservado));
        when(repository.saveAndFlush(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0); p.setId(100L); return p;
        });
        when(pagos.simular(any())).thenReturn("REF-PRUEBA");

        PedidoDTO response = service.crear(request);

        assertEquals(100L, response.getId());
        assertEquals(new BigDecimal("27990"), response.getTotal());
        assertEquals("REF-PRUEBA", response.getReferenciaPago());
        verify(repository).guardarReferencia(100L, "REF-PRUEBA");
    }

    @Test
    void sinStockNoSeGuardaPedidoNiPago() {
        PedidoRequestDTO request = pedidoRequest(10L, 3);
        when(productoClient.reservar(anyString(), eq(request.getItems()))).thenThrow(new IllegalArgumentException("Stock insuficiente"));
        assertThrows(IllegalArgumentException.class, () -> service.crear(request));
        verify(repository, never()).saveAndFlush(any());
        verifyNoInteractions(pagos);
    }

    @Test
    void repetirSolicitudConfirmadaDevuelveElMismoPedido() {
        PedidoRequestDTO request = pedidoRequest(10L, 1);
        request.setSolicitudId(java.util.UUID.randomUUID());
        var item = cl.duoc.mspedidos.model.ItemPedido.builder().productoId(10L).cantidad(1).build();
        var existente = Pedido.builder().id(77L).usuarioId(1L).direccionEntrega(request.getDireccionEntrega())
                .items(List.of(item)).referenciaPago("REF-PREVIA").build();
        when(repository.findByClaveSolicitud("1-" + request.getSolicitudId())).thenReturn(Optional.of(existente));
        assertEquals(77L, service.crear(request).getId());
        verifyNoInteractions(productoClient, pagos);
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

}
