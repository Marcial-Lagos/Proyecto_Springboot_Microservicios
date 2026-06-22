package cl.duoc.mspagos.service;

import cl.duoc.mspagos.dto.*;
import cl.duoc.mspagos.exception.ResourceNotFoundException;
import cl.duoc.mspagos.model.Pago;
import cl.duoc.mspagos.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoService {
    private final PagoRepository repo;

    public List<PagoDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PagoDTO buscarPorId(Long id) {
        return toDTO(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id)));
    }

    public List<PagoDTO> listarPorPedido(Long pedidoId) {
        return repo.findByPedidoId(pedidoId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PagoDTO procesar(PagoRequestDTO req) {
        Pago pago = Pago.builder().pedidoId(req.getPedidoId()).usuarioId(req.getUsuarioId())
                .monto(req.getMonto()).metodo(req.getMetodo()).estado(Pago.EstadoPago.APROBADO)
                .referencia("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()).build();
        return toDTO(repo.save(pago));
    }

    public PagoDTO reembolsar(Long id) {
        Pago pago = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
        pago.setEstado(Pago.EstadoPago.REEMBOLSADO);
        return toDTO(repo.save(pago));
    }

    private PagoDTO toDTO(Pago p) {
        PagoDTO dto = new PagoDTO();
        dto.setId(p.getId());
        dto.setPedidoId(p.getPedidoId());
        dto.setUsuarioId(p.getUsuarioId());
        dto.setMonto(p.getMonto());
        dto.setMetodo(p.getMetodo());
        dto.setEstado(p.getEstado());
        dto.setFechaPago(p.getFechaPago());
        dto.setReferencia(p.getReferencia());
        return dto;
    }
}
