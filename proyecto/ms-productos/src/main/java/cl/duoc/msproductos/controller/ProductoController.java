package cl.duoc.msproductos.controller;

import cl.duoc.msproductos.dto.ProductoDTO;
import cl.duoc.msproductos.dto.ProductoRequestDTO;
import cl.duoc.msproductos.service.ProductoService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Catálogo de productos y control de stock.")
public class ProductoController {

    private final ProductoService service;

    @GetMapping
    @Operation(summary = "Listar productos activos")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class)))
    public ResponseEntity<List<ProductoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar producto por identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<ProductoDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/categoria/{cat}")
    @Operation(summary = "Listar productos por categoría")
    @ApiResponse(responseCode = "200", description = "Listado obtenido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class)))
    public ResponseEntity<List<ProductoDTO>> listarPorCategoria(@PathVariable String cat) {
        return ResponseEntity.ok(service.listarPorCategoria(cat));
    }

    @PostMapping
    @Operation(summary = "Crear producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Producto creado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del producto", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoRequestDTO.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "nombre": "Pizza Pepperoni Familiar",
                      "descripcion": "Pizza de 32 cm con pepperoni y queso mozzarella.",
                      "precio": 10990.00,
                      "stock": 25,
                      "categoria": "PIZZAS",
                      "imagenUrl": "https://example.com/pizza-pepperoni.jpg"
                    }
                    """)))
    public ResponseEntity<ProductoDTO> crear(@Valid @org.springframework.web.bind.annotation.RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del producto", required = true,
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductoRequestDTO.class), examples = @ExampleObject(name = "Ejemplo de body", value = """
                    {
                      "nombre": "Pizza Pepperoni Familiar",
                      "descripcion": "Pizza de 32 cm con extra queso mozzarella.",
                      "precio": 11990.00,
                      "stock": 30,
                      "categoria": "PIZZAS",
                      "imagenUrl": "https://example.com/pizza-pepperoni.jpg"
                    }
                    """)))
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/stock/decrement")
    @Operation(summary = "Descontar stock", description = "Reduce el stock disponible del producto en la cantidad indicada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock descontado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Cantidad inválida o stock insuficiente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Void> descontarStock(@PathVariable Long id,
            @Parameter(description = "Unidades a descontar", example = "2", required = true)
            @RequestParam int cantidad) {
        service.descontarStock(id, cantidad);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar producto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto desactivado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
