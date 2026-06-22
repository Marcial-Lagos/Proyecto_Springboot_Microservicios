package cl.duoc.msenvios.controller;

import cl.duoc.msenvios.dto.EnvioDTO;
import cl.duoc.msenvios.dto.EnvioRequestDTO;
import cl.duoc.msenvios.service.EnvioService;
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
@RequestMapping("/api/v1/envios")
@RequiredArgsConstructor
@Tag(name = "Envíos", description = "Despacho, seguimiento y estado de envíos.")
public class EnvioController {

    private final EnvioService service;

    @GetMapping
    @Operation(summary = "Listar envíos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioDTO.class)))
    public ResponseEntity<List<EnvioDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar envío por identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envío encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado", content = @Content)
    })
    public ResponseEntity<EnvioDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/seguimiento/{codigo}")
    @Operation(summary = "Consultar seguimiento por código")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Envío encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Código de seguimiento no encontrado", content = @Content)
    })
    public ResponseEntity<EnvioDTO> seguimiento(@PathVariable String codigo) {
        return ResponseEntity.ok(service.buscarPorCodigo(codigo));
    }

    @PostMapping
    @Operation(summary = "Crear envío")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Envío creado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del nuevo envío", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioRequestDTO.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "pedidoId": 1,
                      "direccionOrigen": "Av. Vicuña Mackenna 1234, Santiago",
                      "direccionDestino": "Av. Grecia 456, Ñuñoa"
                    }
                    """)))
    public ResponseEntity<EnvioDTO> crear(@Valid @org.springframework.web.bind.annotation.RequestBody EnvioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de envío")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EnvioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado", content = @Content)
    })
    public ResponseEntity<EnvioDTO> cambiarEstado(@PathVariable Long id,
            @Parameter(description = "Nuevo estado del envío", example = "EN_CAMINO", required = true)
            @RequestParam String estado) {
        return ResponseEntity.ok(service.cambiarEstado(id, estado));
    }
}
