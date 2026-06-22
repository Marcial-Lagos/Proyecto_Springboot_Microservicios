package cl.duoc.msresenas.repository;

import cl.duoc.msresenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByProductoId(Long productoId);

    List<Resena> findByUsuarioId(Long usuarioId);

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.productoId = :productoId")
    Double promedioCalificacion(@Param("productoId") Long productoId);
}
