package cl.duoc.apigateway;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/voz")
public class Voz {
    record Miembro(String id, String nombre, boolean hablando, boolean silencio) {}
    record Senal(String de, String tipo, String sdp) {}
    record Estado(List<Miembro> miembros, List<Senal> senales) {}
    record Presencia(boolean hablando, boolean silencio) {}
    record Envio(String destino, String tipo, String sdp) {}
    static class Sesion {
        String nombre; long activo; boolean hablando, silencio;
        List<Senal> cola = new ArrayList<>();
        Sesion(String nombre) { this.nombre = nombre; activo = System.currentTimeMillis(); }
    }
    private final Map<String, Map<String, Sesion>> salas = new HashMap<>();
    private Map<String, Sesion> sala(String id) {
        if (!Set.of("general", "ranked").contains(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        var miembros = salas.computeIfAbsent(id, x -> new HashMap<>());
        miembros.entrySet().removeIf(e -> System.currentTimeMillis() - e.getValue().activo > 30000);
        return miembros;
    }
    private Sesion usuario(Map<String, Sesion> sala, String id) {
        var s = sala.get(id);
        if (s == null) throw new ResponseStatusException(HttpStatus.GONE);
        s.activo = System.currentTimeMillis();
        return s;
    }
    @GetMapping("/{sala}")
    public synchronized List<Miembro> participantes(@PathVariable String sala) {
        return sala(sala).entrySet().stream().map(e -> new Miembro(e.getKey(), e.getValue().nombre,
                e.getValue().hablando, e.getValue().silencio)).sorted(Comparator.comparing(Miembro::nombre)).toList();
    }
    @PostMapping("/{sala}")
    public synchronized void entrar(@PathVariable String sala, @RequestHeader("X-User-Id") String id,
                                    @RequestBody Map<String,String> datos) {
        var miembros = sala(sala);
        if (miembros.containsKey(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya conectado");
        if (miembros.size() >= 6) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
        String nombre = Objects.toString(datos.get("nombre"), "").trim();
        if (nombre.isEmpty() || nombre.length() > 60) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        miembros.put(id, new Sesion(nombre));
    }
    @PostMapping("/{sala}/estado")
    public synchronized Estado estado(@PathVariable String sala, @RequestHeader("X-User-Id") String id,
                                      @RequestBody Presencia datos) {
        var miembros = sala(sala); var s = usuario(miembros, id);
        s.silencio = datos.silencio(); s.hablando = datos.hablando() && !s.silencio;
        var lista = miembros.entrySet().stream().map(e -> new Miembro(e.getKey(), e.getValue().nombre,
                e.getValue().hablando, e.getValue().silencio)).toList();
        var senales = List.copyOf(s.cola); s.cola.clear();
        return new Estado(lista, senales);
    }
    @PostMapping("/{sala}/senal")
    public synchronized void senal(@PathVariable String sala, @RequestHeader("X-User-Id") String id,
                                   @RequestBody Envio datos) {
        var miembros = sala(sala); usuario(miembros, id);
        if (datos.destino() == null || datos.destino().equals(id) ||
            datos.tipo() == null || !Set.of("offer","answer").contains(datos.tipo()) ||
            datos.sdp() == null || !datos.sdp().startsWith("v=0") || datos.sdp().length() > 100000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var receptor = miembros.get(datos.destino());
        if (receptor == null) throw new ResponseStatusException(HttpStatus.GONE);
        if (receptor.cola.size() >= 24) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
        receptor.cola.add(new Senal(id, datos.tipo(), datos.sdp()));
    }
    @DeleteMapping("/{sala}")
    public synchronized void salir(@PathVariable String sala, @RequestHeader("X-User-Id") String id) {
        sala(sala).remove(id);
    }
}
