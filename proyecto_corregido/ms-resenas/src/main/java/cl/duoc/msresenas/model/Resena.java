package cl.duoc.msresenas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resenas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resena {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usuarioId;
    private Long productoId;
    private Long pedidoId;
    @Min(1)
    @Max(5)
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
