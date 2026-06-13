package cl.duoc.msauth.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioSyncClient {

    private final WebClient.Builder webClientBuilder;

    @Data
    public static class UsuarioRequest {
        private String nombre;
        private String email;
        private String password;
        private String telefono;
        private String direccion;
    }

    /**
     * Crea el usuario en ms-usuarios para mantener sincronía con ms-auth.
     * Si falla (ms-usuarios no disponible), solo loguea el error — no rompe el registro.
     */
    public void sincronizarUsuario(String nombre, String email, String rawPassword) {
        try {
            UsuarioRequest req = new UsuarioRequest();
            req.setNombre(nombre);
            req.setEmail(email);
            req.setPassword(rawPassword);

            webClientBuilder.build()
                    .post()
                    .uri("http://ms-usuarios/api/v1/usuarios")
                    .bodyValue(req)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException e) {
            // 409 Conflict = usuario ya existía en ms-usuarios (caso normal en re-registro)
            if (e.getStatusCode().value() != 409) {
                log.warn("No se pudo sincronizar usuario '{}' en ms-usuarios: {}", email, e.getMessage());
            }
        } catch (Exception e) {
            log.warn("ms-usuarios no disponible al sincronizar usuario '{}': {}", email, e.getMessage());
        }
    }
}
