package cl.duoc.msproductos.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventarioClient {

    private final WebClient.Builder webClientBuilder;

    /**
     * Notifica a ms-inventario para ajustar su stockActual cuando cambia el stock
     * en ms-productos.
     * Usa delta negativo para descuentos, positivo para reposición.
     * Best-effort: si falla, solo se loguea.
     */
    public void ajustarStock(Long productoId, int delta) {
        try {
            webClientBuilder.build()
                    .patch()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("http")
                            .host("INVENTARIO-SERVICE")
                            .path("/api/v1/inventario/producto/{productoId}/ajustar")
                            .queryParam("cantidad", delta)
                            .build(productoId))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.warn("No se pudo sincronizar stock en ms-inventario para producto {}: {}", productoId, e.getMessage());
        }
    }
}
