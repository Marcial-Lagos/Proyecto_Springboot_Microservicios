package cl.duoc.mspedidos.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    @NotNull private Long usuarioId;
    @NotBlank private String direccionEntrega;
    @NotEmpty private List<ItemPedidoDTO> items;
}
