package cl.duoc.mspedidos.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class PedidoRequestDTO {
    @NotNull private Long usuarioId;
    @NotBlank @Size(max = 350) private String direccionEntrega;
    private java.util.UUID solicitudId;
    @Pattern(regexp = "retiro|domicilio") private String tipoEntrega;
    @Size(max = 100) private String nombreContacto;
    @Email @Size(max = 254) private String correoContacto;
    @Pattern(regexp = "TARJETA|TRANSFERENCIA|EFECTIVO") private String metodoPago;
    @NotEmpty @Size(max = 100) private List<@NotNull @jakarta.validation.Valid ItemPedidoDTO> items;
}
