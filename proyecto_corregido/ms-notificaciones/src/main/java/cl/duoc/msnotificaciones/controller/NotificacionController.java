package cl.duoc.msnotificaciones.controller;

import cl.duoc.msnotificaciones.dto.*;
import cl.duoc.msnotificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {
    private final NotificacionService service;

    @GetMapping("/usuario/{uid}")
    public ResponseEntity<List<NotificacionDTO>> listar(@PathVariable Long uid) {
        return ResponseEntity.ok(service.listarPorUsuario(uid));
    }

    @GetMapping("/usuario/{uid}/no-leidas")
    public ResponseEntity<List<NotificacionDTO>> noLeidas(@PathVariable Long uid) {
        return ResponseEntity.ok(service.noLeidas(uid));
    }

    @PostMapping
    public ResponseEntity<NotificacionDTO> crear(@Valid @RequestBody NotificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<NotificacionDTO> marcarLeida(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarLeida(id));
    }
}
