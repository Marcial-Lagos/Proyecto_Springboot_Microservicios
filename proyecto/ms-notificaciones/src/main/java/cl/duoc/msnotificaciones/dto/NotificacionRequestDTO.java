package cl.duoc.msnotificaciones.dto;

import cl.duoc.msnotificaciones.model.Notificacion;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NotificacionRequestDTO {
    @NotNull
    private Long usuarioId;
    @NotBlank
    private String titulo;
    @NotBlank
    private String mensaje;
    @NotNull
    private Notificacion.TipoNotificacion tipo;
}
