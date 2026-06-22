package cl.duoc.msnotificaciones.controller;

import cl.duoc.msnotificaciones.dto.NotificacionDTO;
import cl.duoc.msnotificaciones.dto.NotificacionRequestDTO;
import cl.duoc.msnotificaciones.service.NotificacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Mensajes y avisos asociados a usuarios.")
public class NotificacionController {

    private final NotificacionService service;

    @GetMapping("/usuario/{uid}")
    @Operation(summary = "Listar notificaciones por usuario")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionDTO.class)))
    public ResponseEntity<List<NotificacionDTO>> listar(@PathVariable Long uid) {
        return ResponseEntity.ok(service.listarPorUsuario(uid));
    }

    @GetMapping("/usuario/{uid}/no-leidas")
    @Operation(summary = "Listar notificaciones no leídas")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionDTO.class)))
    public ResponseEntity<List<NotificacionDTO>> noLeidas(@PathVariable Long uid) {
        return ResponseEntity.ok(service.noLeidas(uid));
    }

    @PostMapping
    @Operation(summary = "Crear notificación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificación creada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la notificación", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionRequestDTO.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "usuarioId": 1,
                      "titulo": "Pedido confirmado",
                      "mensaje": "Tu pedido #1 fue confirmado y está siendo preparado.",
                      "tipo": "PEDIDO"
                    }
                    """)))
    public ResponseEntity<NotificacionDTO> crear(@Valid @org.springframework.web.bind.annotation.RequestBody NotificacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PatchMapping("/{id}/leer")
    @Operation(summary = "Marcar notificación como leída")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación actualizada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada", content = @Content)
    })
    public ResponseEntity<NotificacionDTO> marcarLeida(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarLeida(id));
    }
}
