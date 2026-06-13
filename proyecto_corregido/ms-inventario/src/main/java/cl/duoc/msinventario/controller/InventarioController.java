package cl.duoc.msinventario.controller;

import cl.duoc.msinventario.dto.*;
import cl.duoc.msinventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {
    private final InventarioService service;

    @GetMapping
    public ResponseEntity<List<InventarioDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<InventarioDTO>> bajoStock() {
        return ResponseEntity.ok(service.listarBajoStock());
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioDTO> buscar(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.buscarPorProducto(productoId));
    }

    @PostMapping
    public ResponseEntity<InventarioDTO> crear(@Valid @RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PatchMapping("/producto/{productoId}/ajustar")
    public ResponseEntity<InventarioDTO> ajustar(@PathVariable Long productoId, @RequestParam int cantidad) {
        return ResponseEntity.ok(service.ajustar(productoId, cantidad));
    }
}
