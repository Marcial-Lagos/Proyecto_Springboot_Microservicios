package cl.duoc.msnotificaciones.dto;

import cl.duoc.msnotificaciones.model.Notificacion;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionDTO {
    private Long id;
    private Long usuarioId;
    private String titulo;
    private String mensaje;
    private Notificacion.TipoNotificacion tipo;
    private boolean leida;
    private LocalDateTime fechaCreacion;
}
