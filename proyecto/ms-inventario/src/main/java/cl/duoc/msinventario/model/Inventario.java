package cl.duoc.msinventario.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true) private Long productoId;
    private Long revisionProducto;
    @Version @Column(nullable = false, columnDefinition = "bigint default 0") private long version;
    private String nombreProducto;
    private Integer stockActual;
    private Integer stockMinimo;
    @Builder.Default
    private LocalDateTime ultimaActualizacion = LocalDateTime.now();
}
