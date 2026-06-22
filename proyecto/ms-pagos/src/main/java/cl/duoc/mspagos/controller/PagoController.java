package cl.duoc.mspagos.controller;

import cl.duoc.mspagos.dto.PagoDTO;
import cl.duoc.mspagos.dto.PagoRequestDTO;
import cl.duoc.mspagos.service.PagoService;
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
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Procesamiento y reembolso de pagos.")
public class PagoController {

    private final PagoService service;

    @GetMapping
    @Operation(summary = "Listar pagos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class)))
    public ResponseEntity<List<PagoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pago por identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    public ResponseEntity<PagoDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    @Operation(summary = "Listar pagos por pedido")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class)))
    public ResponseEntity<List<PagoDTO>> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(service.listarPorPedido(pedidoId));
    }

    @PostMapping
    @Operation(summary = "Procesar pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pago procesado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o pago no procesable", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos para procesar el pago", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoRequestDTO.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "pedidoId": 1,
                      "usuarioId": 1,
                      "monto": 21980.00,
                      "metodo": "TARJETA"
                    }
                    """)))
    public ResponseEntity<PagoDTO> procesar(@Valid @org.springframework.web.bind.annotation.RequestBody PagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.procesar(dto));
    }

    @PatchMapping("/{id}/reembolsar")
    @Operation(summary = "Reembolsar pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pago reembolsado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
            @ApiResponse(responseCode = "400", description = "El pago no puede reembolsarse", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    public ResponseEntity<PagoDTO> reembolsar(@PathVariable Long id) {
        return ResponseEntity.ok(service.reembolsar(id));
    }
}
