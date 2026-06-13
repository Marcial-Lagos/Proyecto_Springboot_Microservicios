package cl.duoc.msusuarios.dto;

import cl.duoc.msusuarios.model.Usuario;
import lombok.Data;

@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private Usuario.Rol rol;
    private String telefono;
    private String direccion;
}
