package cl.duoc.mspedidos.controller;

import cl.duoc.mspedidos.dto.PedidoDTO;
import cl.duoc.mspedidos.dto.PedidoRequestDTO;
import cl.duoc.mspedidos.service.PedidoService;
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
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Creación, consulta y cambio de estado de pedidos.")
public class PedidoController {

    private final PedidoService service;

    @GetMapping
    @Operation(summary = "Listar pedidos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class)))
    public ResponseEntity<List<PedidoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pedido encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    public ResponseEntity<PedidoDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/usuario/{uid}")
    @Operation(summary = "Listar pedidos por usuario")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class)))
    public ResponseEntity<List<PedidoDTO>> listarPorUsuario(@PathVariable Long uid) {
        return ResponseEntity.ok(service.listarPorUsuario(uid));
    }

    @PostMapping
    @Operation(summary = "Crear pedido", description = "Crea un pedido y calcula su total a partir de los ítems enviados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido creado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del pedido", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoRequestDTO.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "usuarioId": 1,
                      "direccionEntrega": "Av. Vicuña Mackenna 1234, Santiago",
                      "items": [
                        {
                          "productoId": 1,
                          "nombreProducto": "Pizza Pepperoni Familiar",
                          "cantidad": 2,
                          "precioUnitario": 10990.00
                        }
                      ]
                    }
                    """)))
    public ResponseEntity<PedidoDTO> crear(@Valid @org.springframework.web.bind.annotation.RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de pedido")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Estado inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado", content = @Content)
    })
    public ResponseEntity<PedidoDTO> cambiarEstado(@PathVariable Long id,
            @Parameter(description = "Nuevo estado del pedido", example = "CONFIRMADO", required = true)
            @RequestParam String estado) {
        return ResponseEntity.ok(service.cambiarEstado(id, estado));
    }
}
