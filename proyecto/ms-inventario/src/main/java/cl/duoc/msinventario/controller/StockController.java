package cl.duoc.msinventario.controller;

import cl.duoc.msinventario.model.Inventario;
import cl.duoc.msinventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class StockController {
    private final InventarioRepository inventario;
    public record Cambio(String nombre, int stock, long revision) {}

    @PutMapping("/interno/inventario/{id}")
    public void sincronizar(@PathVariable Long id, @RequestBody Cambio cambio) {
        var item = inventario.findByProductoId(id).orElseGet(() ->
                Inventario.builder().productoId(id).stockMinimo(5).build());
        if (item.getRevisionProducto() != null && item.getRevisionProducto() >= cambio.revision()) return;
        item.setRevisionProducto(cambio.revision());
        item.setNombreProducto(cambio.nombre());
        item.setStockActual(cambio.stock());
        item.setUltimaActualizacion(LocalDateTime.now());
        inventario.save(item);
    }
}
