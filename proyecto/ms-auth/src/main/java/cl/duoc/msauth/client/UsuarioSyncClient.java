package cl.duoc.msauth.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UsuarioSyncClient {
    private final WebClient.Builder webClientBuilder;

    public record Perfil(Long id) {}
    private record Alta(String nombre, String email, String password) {}

    public Long sincronizarUsuario(String nombre, String email, String password) {
        var client = webClientBuilder.build();
        Perfil perfil = client.get()
                .uri("http://ms-usuarios/interno/usuarios/perfil?email={email}", email)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) return response.bodyToMono(Perfil.class);
                    if (response.statusCode().value() == 404) return Mono.empty();
                    return response.createError();
                })
                .switchIfEmpty(Mono.defer(() -> client.post().uri("http://ms-usuarios/api/v1/usuarios")
                        .bodyValue(new Alta(nombre, email, password)).retrieve().bodyToMono(Perfil.class)))
                .block(java.time.Duration.ofSeconds(12));
        if (perfil == null || perfil.id() == null) throw new IllegalStateException("Perfil no disponible");
        return perfil.id();
    }
}
