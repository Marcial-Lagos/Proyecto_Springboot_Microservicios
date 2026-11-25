package cl.duoc.msproductos.client;

import cl.duoc.msproductos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventarioClient {
    private final WebClient.Builder clientes;
    private final ProductoRepository productos;
    public record Cambio(String nombre, int stock, long revision) {}

    @Scheduled(initialDelay = 10000, fixedDelay = 5000)
    public void sincronizarPendientes() {
        for (var p : productos.findTop50ByPendienteInventarioTrueOrderById()) {
            try {
                clientes.build().put().uri("http://ms-inventario/interno/inventario/{id}", p.getId())
                        .bodyValue(new Cambio(p.getNombre(), p.isActivo() ? p.getStock() : 0, p.getVersion()))
                        .retrieve().toBodilessEntity().block(Duration.ofSeconds(4));
                productos.confirmarInventario(p.getId(), p.getVersion());
            } catch (RuntimeException error) {
                log.warn("Se reintentará la sincronización del inventario desde el producto {}", p.getId());
                return;
            }
        }
    }
}
