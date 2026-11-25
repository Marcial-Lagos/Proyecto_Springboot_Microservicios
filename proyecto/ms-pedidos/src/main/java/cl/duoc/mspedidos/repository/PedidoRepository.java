package cl.duoc.mspedidos.repository;
import cl.duoc.mspedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "items")
    java.util.Optional<Pedido> findByClaveSolicitud(String clave);
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "items")
    List<Pedido> findByUsuarioId(Long usuarioId);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("update Pedido p set p.referenciaPago = :referencia where p.id = :id and p.referenciaPago is null")
    int guardarReferencia(@org.springframework.data.repository.query.Param("id") Long id,
            @org.springframework.data.repository.query.Param("referencia") String referencia);
}
