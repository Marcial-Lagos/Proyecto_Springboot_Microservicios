package cl.duoc.mscupones.dto;

import cl.duoc.mscupones.model.Cupon;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Modelo que representa la información de solicitud para crear o actualizar un cupón")
public class CuponRequestDTO {
    @NotBlank
    @Schema(description = "Código alfanumérico del cupón", example = "DESCUENTO10")
    private String codigo;

    @NotNull
    @Schema(description = "Tipo de descuento del cupón", example = "PORCENTAJE")
    private Cupon.TipoDescuento tipo;

    @NotNull
    @DecimalMin("0.01")
    @Schema(description = "Valor del descuento", example = "10.00")
    private BigDecimal valor;

    @Schema(description = "Monto mínimo para utilizar el cupón", example = "100.00")
    private BigDecimal montoMinimo;

    @Schema(description = "Fecha de vencimiento del cupón", example = "2028-12-31T23:59:59")
    private LocalDateTime fechaVencimiento;

    @Schema(description = "Número máximo de usos del cupón", example = "10")
    private Integer usosMaximos;
}
