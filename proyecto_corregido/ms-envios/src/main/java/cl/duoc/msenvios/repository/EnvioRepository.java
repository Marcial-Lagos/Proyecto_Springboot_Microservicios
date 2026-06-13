package cl.duoc.msenvios.repository;

import cl.duoc.msenvios.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnvioRepository extends JpaRepository<Envio, Long> {
    Optional<Envio> findByPedidoId(Long pedidoId);

    List<Envio> findByRepartidorId(Long repartidorId);

    Optional<Envio> findByCodigoSeguimiento(String codigo);
}
