package cl.duoc.msresenas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResenaRequestDTO {
    @NotNull
    private Long usuarioId;
    @NotNull
    private Long productoId;
    @NotNull
    private Long pedidoId;
    @NotNull
    @Min(1)
    @Max(5)
    private Integer calificacion;
    private String comentario;
}
