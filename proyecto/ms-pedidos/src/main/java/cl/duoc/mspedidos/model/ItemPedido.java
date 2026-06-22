package cl.duoc.mspedidos.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name="items_pedido") @Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemPedido {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @JsonBackReference
    @ManyToOne @JoinColumn(name="pedido_id") private Pedido pedido;
    private Long productoId;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
}
