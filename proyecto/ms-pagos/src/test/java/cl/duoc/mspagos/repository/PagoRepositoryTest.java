package cl.duoc.mspagos.repository;

import cl.duoc.mspagos.model.Pago;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PagoRepositoryTest {
    @Autowired PagoRepository repository;

    @Test
    void findByPedidoIdRetornaSoloPagosDelPedido() {
        Pago pago = guardar(100L);
        guardar(200L);

        assertThat(repository.findByPedidoId(100L)).hasSize(1);
        assertThat(repository.findByPedidoId(100L).getFirst().getId()).isEqualTo(pago.getId());
    }

    private Pago guardar(Long pedidoId) {
        return repository.save(Pago.builder()
                .pedidoId(pedidoId)
                .usuarioId(1L)
                .monto(new BigDecimal("12000.00"))
                .metodo(Pago.MetodoPago.WEBPAY)
                .estado(Pago.EstadoPago.APROBADO)
                .referencia("REF-PRUEBA")
                .build());
    }
}