package cl.duoc.msenvios.controller;

import cl.duoc.msenvios.dto.*;
import cl.duoc.msenvios.service.EnvioService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1/envios")
@RequiredArgsConstructor
@Tag(name = "Envios", description = "Operaciones relacionadas con envíos")  
public class EnvioController {
    private final EnvioService service;

    @GetMapping
    @Operation(summary = "Listar envíos", description = "Obtiene una lista de todos los envíos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioDTO.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    public ResponseEntity<List<EnvioDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar envío", description = "Obtiene un envío específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado", content = @Content)
    })
    public ResponseEntity<EnvioDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/seguimiento/{codigo}")
    @Operation(summary = "Seguimiento de envío", description = "Obtiene información de un envío por su código")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado", content = @Content)
    })
    public ResponseEntity<EnvioDTO> seguimiento(@PathVariable String codigo) {
        return ResponseEntity.ok(service.buscarPorCodigo(codigo));
    }

    @PostMapping
    @Operation(summary = "Crear envío", description = "Crea un nuevo envío")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Envío creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content)
    })
    public ResponseEntity<EnvioDTO> crear(@Valid @RequestBody EnvioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de envío", description = "Actualiza el estado de un envío específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado", content = @Content)
    })
    public ResponseEntity<EnvioDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(service.cambiarEstado(id, estado));
    }
}
