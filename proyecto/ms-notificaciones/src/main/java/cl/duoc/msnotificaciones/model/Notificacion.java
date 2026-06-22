package cl.duoc.msnotificaciones.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long usuarioId;
    private String titulo;
    private String mensaje;
    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;
    @Builder.Default
    private boolean leida = false;
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public enum TipoNotificacion {
        PEDIDO, PAGO, ENVIO, PROMOCION, SISTEMA
    }
}
