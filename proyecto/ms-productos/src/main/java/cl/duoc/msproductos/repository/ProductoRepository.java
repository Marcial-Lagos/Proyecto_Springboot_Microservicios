package cl.duoc.msproductos.repository;

import cl.duoc.msproductos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Producto p where p.id in :ids order by p.id")
    List<Producto> bloquearPorId(@org.springframework.data.repository.query.Param("ids") java.util.Collection<Long> ids);

    List<Producto> findByActivoTrue();
    List<Producto> findTop50ByPendienteInventarioTrueOrderById();

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("update Producto p set p.pendienteInventario = false where p.id = :id and p.version = :revision")
    int confirmarInventario(@org.springframework.data.repository.query.Param("id") Long id,
            @org.springframework.data.repository.query.Param("revision") long revision);

    List<Producto> findByCategoriaAndActivoTrue(String categoria);
}
