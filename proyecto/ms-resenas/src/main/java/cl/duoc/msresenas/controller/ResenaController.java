package cl.duoc.msresenas.controller;

import cl.duoc.msresenas.dto.ResenaDTO;
import cl.duoc.msresenas.dto.ResenaRequestDTO;
import cl.duoc.msresenas.service.ResenaService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/resenas")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Calificaciones y comentarios de productos.")
public class ResenaController {

    private final ResenaService service;

    @GetMapping("/producto/{pid}")
    @Operation(summary = "Listar reseñas por producto")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResenaDTO.class)))
    public ResponseEntity<List<ResenaDTO>> porProducto(@PathVariable Long pid) {
        return ResponseEntity.ok(service.listarPorProducto(pid));
    }

    @GetMapping("/usuario/{uid}")
    @Operation(summary = "Listar reseñas por usuario")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResenaDTO.class)))
    public ResponseEntity<List<ResenaDTO>> porUsuario(@PathVariable Long uid) {
        return ResponseEntity.ok(service.listarPorUsuario(uid));
    }

    @GetMapping("/producto/{pid}/promedio")
    @Operation(summary = "Calcular promedio de calificación")
    @ApiResponse(responseCode = "200", description = "Promedio calculado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> promedio(@PathVariable Long pid) {
        return ResponseEntity.ok(service.promedio(pid));
    }

    @PostMapping
    @Operation(summary = "Crear reseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reseña creada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResenaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos de la reseña", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResenaRequestDTO.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "usuarioId": 1,
                      "productoId": 1,
                      "pedidoId": 1,
                      "calificacion": 5,
                      "comentario": "Excelente producto y entrega rápida."
                    }
                    """)))
    public ResponseEntity<ResenaDTO> crear(@Valid @org.springframework.web.bind.annotation.RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reseña eliminada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada", content = @Content)
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
