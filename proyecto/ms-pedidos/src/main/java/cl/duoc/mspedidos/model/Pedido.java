package cl.duoc.mspedidos.model;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name="pedidos") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Pedido {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long usuarioId;
    @Column(unique = true, length = 80)
    private String claveSolicitud;
    private String tipoEntrega;
    private String nombreContacto;
    private String correoContacto;
    private BigDecimal costoEnvio;
    private String metodoPago;
    private String referenciaPago;
    @Enumerated(EnumType.STRING) @Builder.Default
    private Estado estado = Estado.PENDIENTE;
    private BigDecimal total;
    @Column(length = 350) private String direccionEntrega;
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @JsonManagedReference
    @OneToMany(mappedBy="pedido", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<ItemPedido> items;
    public enum Estado { PENDIENTE, CONFIRMADO, EN_PREPARACION, EN_CAMINO, ENTREGADO, CANCELADO }
}
