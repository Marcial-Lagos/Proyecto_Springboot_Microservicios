package cl.duoc.msenvios.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "envios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long pedidoId;
    private Long repartidorId;
    private String direccionOrigen;
    private String direccionDestino;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoEnvio estado = EstadoEnvio.PENDIENTE;
    private LocalDateTime fechaEstimada;
    private LocalDateTime fechaEntrega;
    private String codigoSeguimiento;

    public enum EstadoEnvio {
        PENDIENTE, ASIGNADO, EN_CAMINO, ENTREGADO, FALLIDO
    }
}
