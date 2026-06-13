package cl.duoc.msusuarios.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioRequestDTO {
    @NotBlank
    private String nombre;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 6)
    private String password;
    private String telefono;
    private String direccion;
}
