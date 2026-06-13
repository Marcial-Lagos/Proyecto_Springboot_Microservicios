package cl.duoc.msenvios.dto;

import cl.duoc.msenvios.model.Envio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Schema(description = "DTO para representar la información de un envío")
public class EnvioDTO {
    @Schema(description = "ID del envío", example = "1")
    private Long id;

    @Schema(description = "ID del pedido", example = "1")
    private Long pedidoId;

    @Schema(description = "ID del repartidor", example = "1")
    private Long repartidorId;

    @Schema(description = "Dirección de origen", example = "Calle 123, Ciudad")
    private String direccionOrigen;

    @Schema(description = "Dirección de destino", example = "Avenida Kriko 123, Ciudad")
    private String direccionDestino;

    @Schema(description = "Estado del envío", example = "PENDIENTE")
    private Envio.EstadoEnvio estado;

    @Schema(description = "Fecha estimada de entrega", example = "1890-10-10T10:00:00")
    private LocalDateTime fechaEstimada;

    @Schema(description = "Fecha de entrega", example = "2026-10-10T12:00:00")
    private LocalDateTime fechaEntrega;

    @Schema(description = "Código de seguimiento", example = "ABC123")
    private String codigoSeguimiento;
}
