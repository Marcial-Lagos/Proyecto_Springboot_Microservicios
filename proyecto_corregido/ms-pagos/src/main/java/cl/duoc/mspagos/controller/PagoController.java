package cl.duoc.mspagos.controller;

import cl.duoc.mspagos.dto.*;
import cl.duoc.mspagos.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoService service;

    @GetMapping
    public ResponseEntity<List<PagoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<PagoDTO>> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(service.listarPorPedido(pedidoId));
    }

    @PostMapping
    public ResponseEntity<PagoDTO> procesar(@Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.procesar(dto));
    }

    @PatchMapping("/{id}/reembolsar")
    public ResponseEntity<PagoDTO> reembolsar(@PathVariable Long id) {
        return ResponseEntity.ok(service.reembolsar(id));
    }
}
