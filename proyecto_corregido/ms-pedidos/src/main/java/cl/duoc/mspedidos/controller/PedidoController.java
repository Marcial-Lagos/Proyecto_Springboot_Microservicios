package cl.duoc.mspedidos.controller;
import cl.duoc.mspedidos.dto.*;
import cl.duoc.mspedidos.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/v1/pedidos") @RequiredArgsConstructor
public class PedidoController {
    private final PedidoService service;

    @GetMapping public ResponseEntity<List<PedidoDTO>> listar() { return ResponseEntity.ok(service.listar()); }
    @GetMapping("/{id}") public ResponseEntity<PedidoDTO> buscar(@PathVariable Long id) { return ResponseEntity.ok(service.buscarPorId(id)); }
    @GetMapping("/usuario/{uid}") public ResponseEntity<List<PedidoDTO>> listarPorUsuario(@PathVariable Long uid) { return ResponseEntity.ok(service.listarPorUsuario(uid)); }
    @PostMapping public ResponseEntity<PedidoDTO> crear(@Valid @RequestBody PedidoRequestDTO dto) { return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto)); }
    @PatchMapping("/{id}/estado") public ResponseEntity<PedidoDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) { return ResponseEntity.ok(service.cambiarEstado(id, estado)); }
}
