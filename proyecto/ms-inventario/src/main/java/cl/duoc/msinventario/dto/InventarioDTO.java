package cl.duoc.msinventario.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventarioDTO {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer stockActual;
    private Integer stockMinimo;
    private LocalDateTime ultimaActualizacion;
    private boolean bajoStock;
}
