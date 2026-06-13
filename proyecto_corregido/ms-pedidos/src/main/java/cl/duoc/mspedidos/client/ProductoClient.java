package cl.duoc.mspedidos.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ProductoClient {
    private final WebClient.Builder webClientBuilder;

    public ProductoResponse obtenerProducto(Long productoId) {
        return webClientBuilder.build()
                .get()
                .uri("http://ms-productos/api/v1/productos/{id}", productoId)
                .retrieve()
                .bodyToMono(ProductoResponse.class)
                .block();
    }

    public void descontarStock(Long productoId, int cantidad) {
        webClientBuilder.build()
                .put()
                .uri("http://ms-productos/api/v1/productos/{id}/stock/decrement?cantidad={cantidad}", productoId, cantidad)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
