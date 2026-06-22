package cl.duoc.msresenas.service;

import cl.duoc.msresenas.dto.*;
import cl.duoc.msresenas.exception.ResourceNotFoundException;
import cl.duoc.msresenas.model.Resena;
import cl.duoc.msresenas.repository.ResenaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResenaService {
    private final ResenaRepository repo;

    public List<ResenaDTO> listarPorProducto(Long productoId) {
        return repo.findByProductoId(productoId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ResenaDTO> listarPorUsuario(Long usuarioId) {
        return repo.findByUsuarioId(usuarioId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Map<String, Object> promedio(Long productoId) {
        return Map.of("productoId", productoId, "promedio", repo.promedioCalificacion(productoId));
    }

    public ResenaDTO crear(ResenaRequestDTO req) {
        Resena r = Resena.builder().usuarioId(req.getUsuarioId()).productoId(req.getProductoId())
                .pedidoId(req.getPedidoId()).calificacion(req.getCalificacion()).comentario(req.getComentario())
                .build();
        return toDTO(repo.save(r));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new ResourceNotFoundException("Reseña no encontrada: " + id);
        repo.deleteById(id);
    }

    private ResenaDTO toDTO(Resena r) {
        ResenaDTO dto = new ResenaDTO();
        dto.setId(r.getId());
        dto.setUsuarioId(r.getUsuarioId());
        dto.setProductoId(r.getProductoId());
        dto.setPedidoId(r.getPedidoId());
        dto.setCalificacion(r.getCalificacion());
        dto.setComentario(r.getComentario());
        dto.setFechaCreacion(r.getFechaCreacion());
        return dto;
    }
}
