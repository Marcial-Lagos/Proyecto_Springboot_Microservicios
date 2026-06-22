package cl.duoc.mspagos.dto;

import cl.duoc.mspagos.model.Pago;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoDTO {
    private Long id;
    private Long pedidoId;
    private Long usuarioId;
    private BigDecimal monto;
    private Pago.MetodoPago metodo;
    private Pago.EstadoPago estado;
    private LocalDateTime fechaPago;
    private String referencia;
}
