package cl.duoc.msinventario.service;

import cl.duoc.msinventario.dto.*;
import cl.duoc.msinventario.exception.ResourceNotFoundException;
import cl.duoc.msinventario.model.Inventario;
import cl.duoc.msinventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioService {
    private final InventarioRepository repo;

    public List<InventarioDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<InventarioDTO> listarBajoStock() {
        return repo.findAll().stream().filter(i -> i.getStockActual() <= i.getStockMinimo()).map(this::toDTO)
                .collect(Collectors.toList());
    }

    public InventarioDTO buscarPorProducto(Long productoId) {
        return toDTO(repo.findByProductoId(productoId).orElseThrow(
                () -> new ResourceNotFoundException("Inventario no encontrado para producto: " + productoId)));
    }

    public InventarioDTO crear(InventarioRequestDTO req) {
        Inventario inv = Inventario.builder().productoId(req.getProductoId()).nombreProducto(req.getNombreProducto())
                .stockActual(req.getStockActual()).stockMinimo(req.getStockMinimo()).build();
        return toDTO(repo.save(inv));
    }

    public InventarioDTO ajustar(Long productoId, int cantidad) {
        Inventario inv = repo.findByProductoId(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado: " + productoId));
        inv.setStockActual(inv.getStockActual() + cantidad);
        inv.setUltimaActualizacion(LocalDateTime.now());
        return toDTO(repo.save(inv));
    }

    private InventarioDTO toDTO(Inventario i) {
        InventarioDTO dto = new InventarioDTO();
        dto.setId(i.getId());
        dto.setProductoId(i.getProductoId());
        dto.setNombreProducto(i.getNombreProducto());
        dto.setStockActual(i.getStockActual());
        dto.setStockMinimo(i.getStockMinimo());
        dto.setUltimaActualizacion(i.getUltimaActualizacion());
        dto.setBajoStock(i.getStockActual() <= i.getStockMinimo());
        return dto;
    }
}
