package cl.duoc.msresenas.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResenaDTO {
    private Long id;
    private Long usuarioId;
    private Long productoId;
    private Long pedidoId;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fechaCreacion;
}
