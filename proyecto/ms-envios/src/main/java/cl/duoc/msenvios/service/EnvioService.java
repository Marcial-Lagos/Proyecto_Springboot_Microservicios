package cl.duoc.msenvios.service;

import cl.duoc.msenvios.dto.*;
import cl.duoc.msenvios.exception.ResourceNotFoundException;
import cl.duoc.msenvios.model.Envio;
import cl.duoc.msenvios.repository.EnvioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnvioService {
    private final EnvioRepository repo;

    public List<EnvioDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EnvioDTO buscarPorId(Long id) {
        return toDTO(repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Envio no encontrado: " + id)));
    }

    public EnvioDTO buscarPorCodigo(String codigo) {
        return toDTO(repo.findByCodigoSeguimiento(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Envio no encontrado: " + codigo)));
    }

    public EnvioDTO crear(EnvioRequestDTO req) {
        Envio envio = Envio.builder().pedidoId(req.getPedidoId()).direccionOrigen(req.getDireccionOrigen())
                .direccionDestino(req.getDireccionDestino())
                .codigoSeguimiento("ENV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .fechaEstimada(LocalDateTime.now().plusHours(1)).build();
        return toDTO(repo.save(envio));
    }

    public EnvioDTO cambiarEstado(Long id, String estado) {
        Envio e = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Envio no encontrado: " + id));
        e.setEstado(Envio.EstadoEnvio.valueOf(estado));
        if (estado.equals("ENTREGADO"))
            e.setFechaEntrega(LocalDateTime.now());
        return toDTO(repo.save(e));
    }

    private EnvioDTO toDTO(Envio e) {
        EnvioDTO dto = new EnvioDTO();
        dto.setId(e.getId());
        dto.setPedidoId(e.getPedidoId());
        dto.setRepartidorId(e.getRepartidorId());
        dto.setDireccionOrigen(e.getDireccionOrigen());
        dto.setDireccionDestino(e.getDireccionDestino());
        dto.setEstado(e.getEstado());
        dto.setFechaEstimada(e.getFechaEstimada());
        dto.setFechaEntrega(e.getFechaEntrega());
        dto.setCodigoSeguimiento(e.getCodigoSeguimiento());
        return dto;
    }
}
