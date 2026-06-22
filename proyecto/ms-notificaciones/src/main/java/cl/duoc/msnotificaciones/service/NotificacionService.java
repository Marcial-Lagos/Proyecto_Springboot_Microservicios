package cl.duoc.msnotificaciones.service;

import cl.duoc.msnotificaciones.dto.*;
import cl.duoc.msnotificaciones.exception.ResourceNotFoundException;
import cl.duoc.msnotificaciones.model.Notificacion;
import cl.duoc.msnotificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacionService {
    private final NotificacionRepository repo;

    public List<NotificacionDTO> listarPorUsuario(Long uid) {
        return repo.findByUsuarioIdOrderByFechaCreacionDesc(uid).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<NotificacionDTO> noLeidas(Long uid) {
        return repo.findByUsuarioIdAndLeidaFalse(uid).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public NotificacionDTO crear(NotificacionRequestDTO req) {
        Notificacion n = Notificacion.builder().usuarioId(req.getUsuarioId()).titulo(req.getTitulo())
                .mensaje(req.getMensaje()).tipo(req.getTipo()).build();
        return toDTO(repo.save(n));
    }

    public NotificacionDTO marcarLeida(Long id) {
        Notificacion n = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada: " + id));
        n.setLeida(true);
        return toDTO(repo.save(n));
    }

    private NotificacionDTO toDTO(Notificacion n) {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(n.getId());
        dto.setUsuarioId(n.getUsuarioId());
        dto.setTitulo(n.getTitulo());
        dto.setMensaje(n.getMensaje());
        dto.setTipo(n.getTipo());
        dto.setLeida(n.isLeida());
        dto.setFechaCreacion(n.getFechaCreacion());
        return dto;
    }
}
