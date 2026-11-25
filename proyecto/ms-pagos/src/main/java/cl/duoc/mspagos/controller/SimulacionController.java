package cl.duoc.mspagos.controller;

import cl.duoc.mspagos.dto.*;
import cl.duoc.mspagos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SimulacionController {
    private final PagoService pagos;
    @PutMapping("/interno/pagos/pedido/{id}")
    public PagoDTO registrar(@PathVariable Long id, @Valid @RequestBody PagoRequestDTO solicitud) {
        if (!id.equals(solicitud.getPedidoId())) throw new IllegalArgumentException("Pedido inconsistente");
        return pagos.registrarParaPedido(solicitud);
    }
}
