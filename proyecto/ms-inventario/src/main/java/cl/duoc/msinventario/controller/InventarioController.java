package cl.duoc.msinventario.controller;

import cl.duoc.msinventario.dto.InventarioDTO;
import cl.duoc.msinventario.dto.InventarioRequestDTO;
import cl.duoc.msinventario.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Control de existencias y ajustes de stock.")
public class InventarioController {

    private final InventarioService service;

    @GetMapping
    @Operation(summary = "Listar inventario")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioDTO.class)))
    public ResponseEntity<List<InventarioDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/bajo-stock")
    @Operation(summary = "Listar inventario con bajo stock")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioDTO.class)))
    public ResponseEntity<List<InventarioDTO>> bajoStock() {
        return ResponseEntity.ok(service.listarBajoStock());
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Buscar inventario por producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Registro de inventario no encontrado", content = @Content)
    })
    public ResponseEntity<InventarioDTO> buscar(@PathVariable Long productoId) {
        return ResponseEntity.ok(service.buscarPorProducto(productoId));
    }

    @PostMapping
    @Operation(summary = "Crear registro de inventario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro creado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos iniciales de inventario", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioRequestDTO.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "productoId": 1,
                      "nombreProducto": "Pizza Pepperoni Familiar",
                      "stockActual": 25,
                      "stockMinimo": 5
                    }
                    """)))
    public ResponseEntity<InventarioDTO> crear(@Valid @org.springframework.web.bind.annotation.RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PatchMapping("/producto/{productoId}/ajustar")
    @Operation(summary = "Ajustar stock", description = "Suma o resta unidades al stock actual. Puede recibir valores negativos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock ajustado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = InventarioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Ajuste inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Registro de inventario no encontrado", content = @Content)
    })
    public ResponseEntity<InventarioDTO> ajustar(@PathVariable Long productoId,
            @Parameter(description = "Variación de stock; ejemplo: 10 o -3", example = "10", required = true)
            @RequestParam int cantidad) {
        return ResponseEntity.ok(service.ajustar(productoId, cantidad));
    }
}
