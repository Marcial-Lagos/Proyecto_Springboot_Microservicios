package cl.duoc.mspagos.dto;

import cl.duoc.mspagos.model.Pago;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PagoRequestDTO {
    @NotNull
    private Long pedidoId;
    @NotNull
    private Long usuarioId;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal monto;
    @NotNull
    private Pago.MetodoPago metodo;
}
