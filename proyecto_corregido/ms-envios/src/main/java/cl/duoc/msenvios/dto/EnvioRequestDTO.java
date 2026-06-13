package cl.duoc.msenvios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "DTO para crear un nuevo envío")
public class EnvioRequestDTO {

    @NotNull
    @Schema(description = "ID del pedido", example = "1")
    private Long pedidoId;
    @NotBlank
    @Schema(description = "Dirección de origen", example = "Calle 123, Ciudad")
    private String direccionOrigen;
    @NotBlank
    @Schema(description = "Dirección de destino", example = "Avenida Kriko 123, Ciudad")
    private String direccionDestino;
}
