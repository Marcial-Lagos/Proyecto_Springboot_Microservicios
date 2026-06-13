package cl.duoc.mspedidos.service;

import cl.duoc.mspedidos.client.ProductoClient;
import cl.duoc.mspedidos.client.ProductoResponse;
import cl.duoc.mspedidos.dto.*;
import cl.duoc.mspedidos.exception.ResourceNotFoundException;
import cl.duoc.mspedidos.model.*;
import cl.duoc.mspedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository repo;
    private final ProductoClient productoClient;

    public List<PedidoDTO> listar() { return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList()); }
    public List<PedidoDTO> listarPorUsuario(Long uid) { return repo.findByUsuarioId(uid).stream().map(this::toDTO).collect(Collectors.toList()); }
    public PedidoDTO buscarPorId(Long id) { return toDTO(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id))); }

    public PedidoDTO crear(PedidoRequestDTO req) {
        Pedido pedido = Pedido.builder()
                .usuarioId(req.getUsuarioId())
                .direccionEntrega(req.getDireccionEntrega())
                .estado(Pedido.Estado.PENDIENTE)
                .build();

        List<ItemPedido> items = req.getItems().stream().map(i -> {
            ProductoResponse producto = productoClient.obtenerProducto(i.getProductoId());
            if (producto == null || !producto.isActivo()) {
                throw new ResourceNotFoundException("Producto no disponible: " + i.getProductoId());
            }
            if (producto.getStock() == null || producto.getStock() < i.getCantidad()) {
                throw new IllegalArgumentException("Stock insuficiente para producto: " + producto.getNombre());
            }

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProductoId(producto.getId());
            item.setNombreProducto(producto.getNombre());
            item.setCantidad(i.getCantidad());
            item.setPrecioUnitario(producto.getPrecio());
            return item;
        }).collect(Collectors.toList());

        BigDecimal total = items.stream()
                .map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setItems(items);
        pedido.setTotal(total);
        Pedido guardado = repo.save(pedido);

        items.forEach(i -> productoClient.descontarStock(i.getProductoId(), i.getCantidad()));
        return toDTO(guardado);
    }

    public PedidoDTO cambiarEstado(Long id, String estado) {
        Pedido p = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id));
        p.setEstado(Pedido.Estado.valueOf(estado.toUpperCase()));
        return toDTO(repo.save(p));
    }

    private PedidoDTO toDTO(Pedido p) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(p.getId()); dto.setUsuarioId(p.getUsuarioId()); dto.setEstado(p.getEstado());
        dto.setTotal(p.getTotal()); dto.setDireccionEntrega(p.getDireccionEntrega()); dto.setFechaCreacion(p.getFechaCreacion());
        if (p.getItems() != null) dto.setItems(p.getItems().stream().map(i -> { ItemPedidoDTO d = new ItemPedidoDTO(); d.setProductoId(i.getProductoId()); d.setNombreProducto(i.getNombreProducto()); d.setCantidad(i.getCantidad()); d.setPrecioUnitario(i.getPrecioUnitario()); return d; }).collect(Collectors.toList()));
        return dto;
    }
}
