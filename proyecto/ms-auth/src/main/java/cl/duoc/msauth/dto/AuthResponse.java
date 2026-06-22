package cl.duoc.msauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con token JWT y datos del usuario")
public class AuthResponse {
    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...etc")
    private String token;
    @Schema(description = "Tipo de token", example = "Bearer")
    private String tipo;
    @Schema(description = "Correo electrónico del usuario", example = "user@ejemplo.cl")
    private String email;
    @Schema(description = "Rol del usuario", example = "USER")
    private String rol;

    public AuthResponse(String token, String email, String rol) {
        this.token = token;
        this.tipo = "Bearer";
        this.email = email;
        this.rol = rol;
    }
}
