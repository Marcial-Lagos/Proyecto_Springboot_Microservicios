package cl.duoc.mspedidos.service;

import cl.duoc.mspedidos.client.PagoClient;
import cl.duoc.mspedidos.client.ProductoClient;
import cl.duoc.mspedidos.dto.*;
import cl.duoc.mspedidos.exception.ResourceNotFoundException;
import cl.duoc.mspedidos.model.*;
import cl.duoc.mspedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PedidoService {
    private final PedidoRepository repo;
    private final ProductoClient productos;
    private final PagoClient pagos;

    @Transactional(readOnly = true)
    public List<PedidoDTO> listar() { return repo.findAll().stream().map(this::toDTO).toList(); }
    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPorUsuario(Long uid) { return repo.findByUsuarioId(uid).stream().map(this::toDTO).toList(); }
    @Transactional(readOnly = true)
    public PedidoDTO buscarPorId(Long id) { return toDTO(buscar(id)); }

    private Pedido buscar(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id));
    }

    public PedidoDTO crear(PedidoRequestDTO req) {
        String clave = req.getUsuarioId() + "-" + Objects.requireNonNullElseGet(req.getSolicitudId(), UUID::randomUUID);
        Pedido pedido = repo.findByClaveSolicitud(clave).orElseGet(() -> crearConReserva(clave, req));
        if (!mismaSolicitud(pedido, req))
            throw new IllegalArgumentException("La referencia ya corresponde a otro pedido");
        if (pedido.getMetodoPago() != null && pedido.getReferenciaPago() == null) {
            String referencia = pagos.simular(pedido);
            repo.guardarReferencia(pedido.getId(), referencia);
            pedido.setReferenciaPago(referencia);
        }
        return toDTO(pedido);
    }

    private boolean mismaSolicitud(Pedido pedido, PedidoRequestDTO req) {
        if (!Objects.equals(pedido.getDireccionEntrega(), req.getDireccionEntrega())
                || !Objects.equals(pedido.getTipoEntrega(), req.getTipoEntrega())
                || !Objects.equals(pedido.getNombreContacto(), req.getNombreContacto())
                || !Objects.equals(pedido.getCorreoContacto(), req.getCorreoContacto())
                || !Objects.equals(pedido.getMetodoPago(), req.getMetodoPago())
                || pedido.getItems().size() != req.getItems().size()) return false;
        return pedido.getItems().stream().allMatch(item -> req.getItems().stream().anyMatch(linea ->
                item.getProductoId().equals(linea.getProductoId()) && item.getCantidad().equals(linea.getCantidad())));
    }

    private Pedido crearConReserva(String clave, PedidoRequestDTO req) {
        var lineas = productos.reservar(clave, req.getItems());
        BigDecimal envio = "domicilio".equals(req.getTipoEntrega()) ? BigDecimal.valueOf(3990) : BigDecimal.ZERO;
        Pedido pedido = Pedido.builder().claveSolicitud(clave).usuarioId(req.getUsuarioId())
                .tipoEntrega(req.getTipoEntrega()).direccionEntrega(req.getDireccionEntrega()).costoEnvio(envio)
                .nombreContacto(req.getNombreContacto()).correoContacto(req.getCorreoContacto())
                .metodoPago(req.getMetodoPago()).build();
        var items = lineas.stream().map(l -> ItemPedido.builder().pedido(pedido).productoId(l.getProductoId())
                .nombreProducto(l.getNombreProducto()).cantidad(l.getCantidad()).precioUnitario(l.getPrecioUnitario()).build()).toList();
        pedido.setItems(items);
        pedido.setTotal(items.stream().map(i -> i.getPrecioUnitario().multiply(BigDecimal.valueOf(i.getCantidad())))
                .reduce(envio, BigDecimal::add));
        try {
            return repo.saveAndFlush(pedido);
        } catch (DataIntegrityViolationException concurrente) {
            return repo.findByClaveSolicitud(clave).orElseThrow(() -> concurrente);
        }
    }

    @Transactional
    public PedidoDTO cambiarEstado(Long id, String estado) {
        Pedido p = buscar(id);
        p.setEstado(Pedido.Estado.valueOf(estado.toUpperCase(java.util.Locale.ROOT)));
        return toDTO(repo.save(p));
    }

    private PedidoDTO toDTO(Pedido p) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(p.getId()); dto.setUsuarioId(p.getUsuarioId()); dto.setEstado(p.getEstado());
        dto.setTotal(p.getTotal()); dto.setDireccionEntrega(p.getDireccionEntrega()); dto.setFechaCreacion(p.getFechaCreacion());
        dto.setTipoEntrega(p.getTipoEntrega()); dto.setCostoEnvio(p.getCostoEnvio()); dto.setNombreContacto(p.getNombreContacto());
        dto.setCorreoContacto(p.getCorreoContacto()); dto.setReferenciaPago(p.getReferenciaPago());
        if (p.getItems() != null) dto.setItems(p.getItems().stream().map(i -> {
            ItemPedidoDTO d = new ItemPedidoDTO(); d.setProductoId(i.getProductoId()); d.setNombreProducto(i.getNombreProducto());
            d.setCantidad(i.getCantidad()); d.setPrecioUnitario(i.getPrecioUnitario()); return d;
        }).toList());
        return dto;
    }
}
