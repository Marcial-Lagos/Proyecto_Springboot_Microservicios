package cl.duoc.mscupones.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cupones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String codigo;
    @Enumerated(EnumType.STRING)
    private TipoDescuento tipo;
    @DecimalMin("0.01")
    private BigDecimal valor;
    private BigDecimal montoMinimo;
    private LocalDateTime fechaVencimiento;
    private Integer usosMaximos;
    @Builder.Default
    private Integer usosActuales = 0;
    @Builder.Default
    private boolean activo = true;

    public enum TipoDescuento {
        PORCENTAJE, MONTO_FIJO
    }
}
