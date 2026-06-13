package cl.duoc.msauth.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para iniciar sesión")
public class LoginRequest {
    @Schema(description = "Correo electrónico del usuario", example = "user@ejemplo.cl")
    @NotBlank @Email private String email;

    @Schema(description = "Contraseña del usuario", example = "Pezword123")
    @NotBlank private String password;
    
}
