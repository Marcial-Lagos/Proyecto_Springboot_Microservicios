package cl.duoc.mscupones.controller;

import cl.duoc.mscupones.dto.*;
import cl.duoc.mscupones.service.CuponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cupones")
@RequiredArgsConstructor    

@Tag(name = "Cupones", description = "Operaciones para manejo de cupones de descuento")
public class CuponController {
    private final CuponService service;

    @GetMapping
    @Operation(summary = "Listar cupones", description = "Obtiene una lista de todos los cupones disponibles")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuponDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<CuponDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    @Operation(summary = "Crear cupon", description = "Crea un nuevo cupon con los datos proporcionados")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cupon creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuponDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos del cupon inválidos", content = @Content)
    })
    public ResponseEntity<CuponDTO> crear(@Valid @RequestBody CuponRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @GetMapping("/{codigo}/validar")
    @Operation(summary = "Validar cupon", description = "Valida si un cupon es válido para un monto específico")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Operacion exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cupon no encontrado", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> validar(@PathVariable String codigo, @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(service.validar(codigo, monto));
    }

    @PostMapping("/{codigo}/aplicar")
    @Operation(summary = "Aplicar cupon", description = "Aplica un cupon a una transacción")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cupon aplicado exitosamente", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Cupon no encontrado", content = @Content)
    })
    public ResponseEntity<Void> aplicar(@PathVariable String codigo) {
        service.aplicar(codigo);
        return ResponseEntity.ok().build();
    }
}
