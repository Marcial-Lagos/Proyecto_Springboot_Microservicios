package cl.duoc.msproductos.controller;

import cl.duoc.msproductos.model.ReservaStock;
import cl.duoc.msproductos.service.ReservaService;
import cl.duoc.msproductos.service.ReservaService.Cantidad;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService service;
    public record Solicitud(@NotEmpty List<@NotNull @Valid Cantidad> items) {}

    @PutMapping("/interno/reservas/{clave}")
    public List<ReservaStock.Linea> reservar(@PathVariable String clave, @Valid @RequestBody Solicitud solicitud) {
        return service.reservar(clave, solicitud.items());
    }
}
