package cl.duoc.mspagos.repository;

import cl.duoc.mspagos.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByPedidoId(Long pedidoId);

    List<Pago> findByUsuarioId(Long usuarioId);
}
