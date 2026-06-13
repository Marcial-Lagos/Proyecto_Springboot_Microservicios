package cl.duoc.mspedidos.dto;
import cl.duoc.mspedidos.model.Pedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoDTO {
    private Long id;
    private Long usuarioId;
    private Pedido.Estado estado;
    private BigDecimal total;
    private String direccionEntrega;
    private LocalDateTime fechaCreacion;
    private List<ItemPedidoDTO> items;
}
