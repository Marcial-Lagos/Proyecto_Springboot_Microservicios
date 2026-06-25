package cl.duoc.mspedidos.repository;

import cl.duoc.mspedidos.model.Pedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository repository;

    @Test
    void findByUsuarioIdDebeRetornarSoloPedidosDelUsuarioSolicitado() {
        Pedido pedidoUsuario = guardarPedido(100L);
        guardarPedido(200L);

        List<Pedido> pedidos = repository.findByUsuarioId(100L);

        assertThat(pedidos).hasSize(1);
        assertThat(pedidos.getFirst().getId()).isEqualTo(pedidoUsuario.getId());
        assertThat(pedidos.getFirst().getEstado()).isEqualTo(Pedido.Estado.PENDIENTE);
    }

    private Pedido guardarPedido(Long usuarioId) {
        Pedido pedido = Pedido.builder()
                .usuarioId(usuarioId)
                .estado(Pedido.Estado.PENDIENTE)
                .total(new BigDecimal("9900.00"))
                .direccionEntrega("Direccion de prueba 123")
                .build();
        return repository.save(pedido);
    }
}