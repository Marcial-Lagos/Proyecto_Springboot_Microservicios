package cl.duoc.msproductos.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductoRequestDTO {
    @NotBlank
    private String nombre;
    private String descripcion;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal precio;
    @NotNull
    @Min(0)
    private Integer stock;
    private String categoria;
    private String imagenUrl;
}
