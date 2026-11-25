package cl.duoc.apigateway;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/comunidad/chat")
public class ChatComunidad {
    private final HistorialChat historial;
    public ChatComunidad(HistorialChat historial) { this.historial=historial; }
    private String clave(String canal) {
        if (!Set.of("reglas","anuncios","general","juegos","memes","programaci\u00f3n","musica","Sala general","Ranked").contains(canal))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return "comunidad:"+canal;
    }
    @GetMapping public List<HistorialChat.Mensaje> leer(@RequestParam String canal,
            @RequestParam(required=false) Long antes) {
        return historial.leer(clave(canal),antes);
    }
    @PostMapping public HistorialChat.Mensaje enviar(@RequestParam String canal,
            @RequestHeader("X-User-Id") String usuario, @RequestBody Map<String,String> datos) {
        return historial.guardar(clave(canal),usuario,datos.get("texto"),2000);
    }
}
