package cl.duoc.mspedidos.client;

import cl.duoc.mspedidos.model.Pedido;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PagoClient {
    private final WebClient.Builder webClientBuilder;
    public record Comprobante(String referencia) {}

    public String simular(Pedido pedido) {
        var pago = webClientBuilder.build().put().uri("http://ms-pagos/interno/pagos/pedido/{id}", pedido.getId())
                .bodyValue(Map.of("pedidoId", pedido.getId(), "usuarioId", pedido.getUsuarioId(), "monto",
                        pedido.getTotal(), "metodo", pedido.getMetodoPago())).retrieve()
                .bodyToMono(Comprobante.class).block(Duration.ofSeconds(12));
        if (pago == null || pago.referencia() == null) throw new IllegalStateException("Pago pendiente de confirmación");
        return pago.referencia();
    }
}
