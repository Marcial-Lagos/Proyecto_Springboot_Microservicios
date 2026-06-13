package cl.duoc.msinventario.repository;

import cl.duoc.msinventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProductoId(Long productoId);

    List<Inventario> findByStockActualLessThanEqual(Integer stock);
}
