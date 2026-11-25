package cl.duoc.msproductos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String nombre;
    private String descripcion;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal precio;
    private BigDecimal precioOferta;
    @NotNull
    @Min(0)
    private Integer stock;
    private String categoria;
    private String imagenUrl;
    @Builder.Default
    private boolean activo = true;
    @Version
    @Column(nullable = false, columnDefinition = "bigint default 0")
    private long version;
    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean pendienteInventario = true;
}
