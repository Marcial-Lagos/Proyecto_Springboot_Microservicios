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
    @DecimalMin("1") private BigDecimal precioOferta;
    @AssertTrue(message = "La oferta debe ser menor que el precio normal")
    public boolean isOfertaValida() { return precioOferta == null || precio != null && precioOferta.compareTo(precio) < 0; }
    @NotNull
    @Min(0)
    private Integer stock;
    private String categoria;
    private String imagenUrl;
}
