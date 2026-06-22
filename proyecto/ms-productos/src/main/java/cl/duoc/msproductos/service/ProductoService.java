package cl.duoc.msproductos.service;

import cl.duoc.msproductos.client.InventarioClient;
import cl.duoc.msproductos.dto.*;
import cl.duoc.msproductos.exception.ResourceNotFoundException;
import cl.duoc.msproductos.model.Producto;
import cl.duoc.msproductos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository repo;
    private final InventarioClient inventarioClient;

    public List<ProductoDTO> listar() {
        return repo.findByActivoTrue().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ProductoDTO buscarPorId(Long id) {
        return toDTO(
                repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id)));
    }

    public List<ProductoDTO> listarPorCategoria(String cat) {
        return repo.findByCategoriaAndActivoTrue(cat).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ProductoDTO crear(ProductoRequestDTO dto) {
        Producto p = Producto.builder().nombre(dto.getNombre()).descripcion(dto.getDescripcion())
                .precio(dto.getPrecio()).stock(dto.getStock()).categoria(dto.getCategoria())
                .imagenUrl(dto.getImagenUrl()).build();
        return toDTO(repo.save(p));
    }

    public ProductoDTO actualizar(Long id, ProductoRequestDTO dto) {
        Producto p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setStock(dto.getStock());
        p.setCategoria(dto.getCategoria());
        p.setImagenUrl(dto.getImagenUrl());
        return toDTO(repo.save(p));
    }

    public void descontarStock(Long id, int cantidad) {
        Producto p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        if (p.getStock() < cantidad)
            throw new RuntimeException("Stock insuficiente");
        p.setStock(p.getStock() - cantidad);
        repo.save(p);
        // Sincroniza ms-inventario (best-effort)
        inventarioClient.ajustarStock(id, -cantidad);
    }

    public void eliminar(Long id) {
        Producto p = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        p.setActivo(false);
        repo.save(p);
    }

    private ProductoDTO toDTO(Producto p) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setStock(p.getStock());
        dto.setCategoria(p.getCategoria());
        dto.setImagenUrl(p.getImagenUrl());
        dto.setActivo(p.isActivo());
        return dto;
    }
}
