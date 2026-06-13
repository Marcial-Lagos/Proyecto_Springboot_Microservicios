package cl.duoc.msresenas.controller;

import cl.duoc.msresenas.dto.*;
import cl.duoc.msresenas.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resenas")
@RequiredArgsConstructor
public class ResenaController {
    private final ResenaService service;

    @GetMapping("/producto/{pid}")
    public ResponseEntity<List<ResenaDTO>> porProducto(@PathVariable Long pid) {
        return ResponseEntity.ok(service.listarPorProducto(pid));
    }

    @GetMapping("/usuario/{uid}")
    public ResponseEntity<List<ResenaDTO>> porUsuario(@PathVariable Long uid) {
        return ResponseEntity.ok(service.listarPorUsuario(uid));
    }

    @GetMapping("/producto/{pid}/promedio")
    public ResponseEntity<Map<String, Object>> promedio(@PathVariable Long pid) {
        return ResponseEntity.ok(service.promedio(pid));
    }

    @PostMapping
    public ResponseEntity<ResenaDTO> crear(@Valid @RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
