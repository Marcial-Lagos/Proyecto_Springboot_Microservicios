package cl.duoc.msproductos.service;

import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.model.ReservaStock;
import cl.duoc.msproductos.repository.ProductoRepository;
import cl.duoc.msproductos.repository.ReservaStockRepository;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ProductoRepository productos;
    private final ReservaStockRepository reservas;
    private final EntityManager entityManager;
    public record Cantidad(@NotNull @Positive Long productoId, @Positive int cantidad) {}

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<ReservaStock.Linea> reservar(String clave, List<Cantidad> solicitud) {
        var cantidades = new TreeMap<Long, Integer>();
        for (var linea : solicitud) {
            if (cantidades.putIfAbsent(linea.productoId(), linea.cantidad()) != null)
                throw new IllegalArgumentException("Producto repetido en el carrito");
        }
        List<Producto> bloqueados = productos.bloquearPorId(cantidades.keySet());
        var existente = reservas.findById(clave);
        if (existente.isPresent()) {
            var lineas = existente.get().getLineas();
            if (lineas.size() != cantidades.size() || lineas.stream().anyMatch(
                    l -> !l.getCantidad().equals(cantidades.get(l.getProductoId()))))
                throw new IllegalArgumentException("La referencia corresponde a otro carrito");
            return lineas;
        }
        if (bloqueados.size() != cantidades.size())
            throw new IllegalArgumentException("Hay un producto que ya no está disponible");
        var lineas = new ArrayList<ReservaStock.Linea>();
        for (var producto : bloqueados) {
            int cantidad = cantidades.get(producto.getId());
            if (!producto.isActivo() || producto.getStock() < cantidad)
                throw new IllegalArgumentException("Stock insuficiente para " + producto.getNombre());
            producto.setStock(producto.getStock() - cantidad);
            lineas.add(new ReservaStock.Linea(producto, cantidad));
            producto.setPendienteInventario(true);
        }
        entityManager.persist(new ReservaStock(clave, lineas));
        return lineas;
    }
}
