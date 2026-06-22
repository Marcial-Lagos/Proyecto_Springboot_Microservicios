package cl.duoc.mscupones.controller;

import cl.duoc.mscupones.dto.CuponDTO;
import cl.duoc.mscupones.dto.CuponRequestDTO;
import cl.duoc.mscupones.service.CuponService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cupones")
@RequiredArgsConstructor
@Tag(name = "Cupones", description = "Validación y aplicación de cupones de descuento.")
public class CuponController {

    private final CuponService service;

    @GetMapping
    @Operation(summary = "Listar cupones")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuponDTO.class)))
    public ResponseEntity<List<CuponDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    @Operation(summary = "Crear cupón")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cupón creado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuponDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del cupón", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CuponRequestDTO.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "codigo": "DESCUENTO10",
                      "tipo": "PORCENTAJE",
                      "valor": 10.00,
                      "montoMinimo": 10000.00,
                      "fechaVencimiento": "2026-12-31T23:59:59",
                      "usosMaximos": 100
                    }
                    """)))
    public ResponseEntity<CuponDTO> crear(@Valid @org.springframework.web.bind.annotation.RequestBody CuponRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @GetMapping("/{codigo}/validar")
    @Operation(summary = "Validar cupón para un monto de compra")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de la validación", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Cupón no válido para el monto indicado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cupón no encontrado", content = @Content)
    })
    public ResponseEntity<Map<String, Object>> validar(@PathVariable String codigo,
            @Parameter(description = "Monto total de la compra", example = "25000.00", required = true)
            @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(service.validar(codigo, monto));
    }

    @PostMapping("/{codigo}/aplicar")
    @Operation(summary = "Registrar uso de cupón")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uso registrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Cupón inválido o sin usos disponibles", content = @Content),
            @ApiResponse(responseCode = "404", description = "Cupón no encontrado", content = @Content)
    })
    public ResponseEntity<Void> aplicar(@PathVariable String codigo) {
        service.aplicar(codigo);
        return ResponseEntity.ok().build();
    }
}
