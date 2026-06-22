package cl.duoc.mspagos.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pedidoId;
    private Long usuarioId;
    private BigDecimal monto;
    @Enumerated(EnumType.STRING)
    private MetodoPago metodo;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;
    @Builder.Default
    private LocalDateTime fechaPago = LocalDateTime.now();
    private String referencia;

    public enum MetodoPago {
        TARJETA, TRANSFERENCIA, EFECTIVO, WEBPAY
    }

    public enum EstadoPago {
        PENDIENTE, APROBADO, RECHAZADO, REEMBOLSADO
    }
}
