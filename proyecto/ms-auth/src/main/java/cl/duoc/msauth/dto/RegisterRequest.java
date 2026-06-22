package cl.duoc.msauth.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Request para registrar un nuevo usuario")
public class RegisterRequest {
    @NotBlank @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String nombre;
    @NotBlank @Email @Schema(description = "Correo electrónico del usuario", example = "juan.perez@duoc.cl")
    private String email;
    @NotBlank @Size(min=6) @Schema(description = "Contraseña del usuario", example = "password123")
    private String password;
}
