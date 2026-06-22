package cl.duoc.msusuarios.service;

import cl.duoc.msusuarios.dto.*;
import cl.duoc.msusuarios.exception.ResourceNotFoundException;
import cl.duoc.msusuarios.model.Usuario;
import cl.duoc.msusuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repo;
    private final BCryptPasswordEncoder encoder;

    public List<UsuarioDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public UsuarioDTO buscarPorId(Long id) {
        return toDTO(
                repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id)));
    }

    public UsuarioDTO crear(UsuarioRequestDTO dto) {
        if (repo.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email ya registrado");
        Usuario u = Usuario.builder().nombre(dto.getNombre()).email(dto.getEmail())
                .password(encoder.encode(dto.getPassword())).telefono(dto.getTelefono()).direccion(dto.getDireccion())
                .build();
        return toDTO(repo.save(u));
    }

    public UsuarioDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario u = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        u.setNombre(dto.getNombre());
        u.setTelefono(dto.getTelefono());
        u.setDireccion(dto.getDireccion());
        return toDTO(repo.save(u));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Usuario no encontrado: " + id);
        repo.deleteById(id);
    }

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setEmail(u.getEmail());
        dto.setRol(u.getRol());
        dto.setTelefono(u.getTelefono());
        dto.setDireccion(u.getDireccion());
        return dto;
    }
}
