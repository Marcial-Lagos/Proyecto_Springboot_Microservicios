package cl.duoc.msinventario.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class InventarioRequestDTO {
    @NotNull
    private Long productoId;
    @NotBlank
    private String nombreProducto;
    @NotNull
    @Min(0)
    private Integer stockActual;
    @NotNull
    @Min(0)
    private Integer stockMinimo;
}
