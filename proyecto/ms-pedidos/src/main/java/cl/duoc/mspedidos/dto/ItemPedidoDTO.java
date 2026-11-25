package cl.duoc.mspedidos.dto;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemPedidoDTO {
    @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Positive
    private Long productoId;
    private String nombreProducto;
    @jakarta.validation.constraints.NotNull @jakarta.validation.constraints.Positive
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
