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
    @Enumerated(EnumType.STRING) @Builder.Default
    private Estado estado = Estado.PENDIENTE;
    private BigDecimal total;
    private String direccionEntrega;
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    @JsonManagedReference
    @OneToMany(mappedBy="pedido", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<ItemPedido> items;
    public enum Estado { PENDIENTE, CONFIRMADO, EN_PREPARACION, EN_CAMINO, ENTREGADO, CANCELADO }
}
