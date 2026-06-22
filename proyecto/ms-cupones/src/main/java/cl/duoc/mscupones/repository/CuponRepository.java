package cl.duoc.mscupones.repository;

import cl.duoc.mscupones.model.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CuponRepository extends JpaRepository<Cupon, Long> {
    Optional<Cupon> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
