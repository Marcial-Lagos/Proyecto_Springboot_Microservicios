package cl.duoc.mspedidos.client;

import cl.duoc.mspedidos.dto.ItemPedidoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductoClient {
    private final WebClient.Builder webClientBuilder;
    public List<ItemPedidoDTO> reservar(String clave, List<ItemPedidoDTO> items) {
        return webClientBuilder.build().put().uri("http://ms-productos/interno/reservas/{clave}", clave)
                .bodyValue(Map.of("items", items)).retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ItemPedidoDTO>>() {}).block(Duration.ofSeconds(15));
    }
}
