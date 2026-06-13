package cl.duoc.mscupones.dto;

import cl.duoc.mscupones.model.Cupon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Modelo que representa la información de respuesta de un cupón")
public class CuponDTO {
    @Schema(description = "ID único del cupón en la base de datos", example = "15")
    private Long id;

    @Schema(description = "Código alfanumérico del cupón", example = "DESCUENTO10")
    private String codigo;

    @Schema(description = "Tipo de descuento del cupón", example = "PORCENTAJE")
    private Cupon.TipoDescuento tipo;

    @Schema(description = "Valor del descuento", example = "10.00")
    private BigDecimal valor;

    @Schema(description = "Monto mínimo para utilizar el cupón", example = "100")
    private BigDecimal montoMinimo;

    @Schema(description = "Fecha de vencimiento del cupón", example = "2028-12-31T23:59:59")
    private LocalDateTime fechaVencimiento;

    @Schema(description = "Número máximo de usos del cupón", example = "10")
    private Integer usosMaximos;

    @Schema(description = "Número de usos actuales del cupón", example = "2")
    private Integer usosActuales;

    @Schema(description = "Indica si el cupón está activo", example = "true")
    private boolean activo;

    @Schema(description = "Indica si el cupón es válido", example = "true")
    private boolean valido;
}
