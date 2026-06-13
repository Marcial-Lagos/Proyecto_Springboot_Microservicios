package cl.duoc.mscupones.service;

import cl.duoc.mscupones.dto.*;
import cl.duoc.mscupones.exception.ResourceNotFoundException;
import cl.duoc.mscupones.model.Cupon;
import cl.duoc.mscupones.repository.CuponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuponService {
    private final CuponRepository repo;

    public List<CuponDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CuponDTO crear(CuponRequestDTO req) {
        if (repo.existsByCodigo(req.getCodigo()))
            throw new RuntimeException("Código ya existe");
        Cupon c = Cupon.builder().codigo(req.getCodigo().toUpperCase()).tipo(req.getTipo()).valor(req.getValor())
                .montoMinimo(req.getMontoMinimo()).fechaVencimiento(req.getFechaVencimiento())
                .usosMaximos(req.getUsosMaximos()).build();
        return toDTO(repo.save(c));
    }

    public Map<String, Object> validar(String codigo, BigDecimal montoCompra) {
        Cupon c = repo.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado: " + codigo));
        boolean valido = c.isActivo()
                && (c.getFechaVencimiento() == null || c.getFechaVencimiento().isAfter(LocalDateTime.now()))
                && (c.getUsosMaximos() == null || c.getUsosActuales() < c.getUsosMaximos())
                && (c.getMontoMinimo() == null || montoCompra.compareTo(c.getMontoMinimo()) >= 0);
        BigDecimal descuento = BigDecimal.ZERO;
        if (valido) {
            descuento = c.getTipo() == Cupon.TipoDescuento.PORCENTAJE
                    ? montoCompra.multiply(c.getValor()).divide(BigDecimal.valueOf(100))
                    : c.getValor();
        }
        return Map.of("valido", valido, "descuento", descuento, "cupon", toDTO(c));
    }

    public void aplicar(String codigo) {
        Cupon c = repo.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Cupón no encontrado"));
        c.setUsosActuales(c.getUsosActuales() + 1);
        repo.save(c);
    }

    private CuponDTO toDTO(Cupon c) {
        CuponDTO dto = new CuponDTO();
        dto.setId(c.getId());
        dto.setCodigo(c.getCodigo());
        dto.setTipo(c.getTipo());
        dto.setValor(c.getValor());
        dto.setMontoMinimo(c.getMontoMinimo());
        dto.setFechaVencimiento(c.getFechaVencimiento());
        dto.setUsosMaximos(c.getUsosMaximos());
        dto.setUsosActuales(c.getUsosActuales());
        dto.setActivo(c.isActivo());
        dto.setValido(c.isActivo()
                && (c.getFechaVencimiento() == null || c.getFechaVencimiento().isAfter(LocalDateTime.now())));
        return dto;
    }
}
