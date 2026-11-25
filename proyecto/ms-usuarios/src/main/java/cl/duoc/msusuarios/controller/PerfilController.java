package cl.duoc.msusuarios.controller;

import cl.duoc.msusuarios.dto.UsuarioDTO;
import cl.duoc.msusuarios.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PerfilController {
    private final UsuarioService service;
    public record Cambio(@NotBlank @Size(max = 100) String nombre, @Size(max = 500) String biografia) {}

    @GetMapping("/interno/usuarios/perfil")
    public UsuarioDTO porEmail(@RequestParam String email) {
        return service.buscarPorEmail(email);
    }

    @PatchMapping("/api/v1/usuarios/{id}")
    public UsuarioDTO actualizar(@PathVariable Long id, @Valid @RequestBody Cambio cambio) {
        return service.actualizarPerfil(id, cambio.nombre(), cambio.biografia());
    }
}
