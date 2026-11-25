package cl.duoc.msproductos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class ReservaStock {
    @Id private String clave;
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "posicion")
    private List<Linea> lineas;

    public ReservaStock(String clave, List<Linea> lineas) {
        this.clave = clave;
        this.lineas = lineas;
    }

    @Embeddable @Getter @NoArgsConstructor
    public static class Linea {
        private Long productoId;
        private Integer cantidad;
        private String nombreProducto;
        private BigDecimal precioUnitario;

        public Linea(Producto producto, int cantidad) {
            this.productoId = producto.getId();
            this.nombreProducto = producto.getNombre();
            this.precioUnitario = producto.getPrecioOferta() == null ? producto.getPrecio() : producto.getPrecioOferta();
            this.cantidad = cantidad;
        }
    }
}
