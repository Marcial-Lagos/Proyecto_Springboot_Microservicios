package cl.duoc.apigateway;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.*;

@RestController
@RequestMapping("/api/v1/directos")
public class Directos {
    record Anuncio(UUID id, String titulo) {}
    record Canal(UUID id, String titulo, String propietario, Map<String, Conexion> espectadores) {}
    static class Conexion { String oferta; String respuesta; long activo; }
    record Senal(String sdp) {}
    record Oferta(String espectador, String sdp) {}
    private final Map<UUID,Canal> canales = new HashMap<>();
    private final Map<UUID,Long> actividad = new HashMap<>();
    private long ahora() { return System.currentTimeMillis(); }
    private void limpiar() {
        actividad.entrySet().removeIf(e -> {
            if (ahora()-e.getValue() < 120000) return false;
            canales.remove(e.getKey()); return true;
        });
    }
    private Canal canal(UUID id) {
        limpiar();
        var c = canales.get(id);
        if (c == null) throw new ResponseStatusException(HttpStatus.GONE);
        c.espectadores().values().removeIf(e -> ahora()-e.activo > 120000);
        return c;
    }
    private Canal propio(UUID id,String usuario) {
        var c=canal(id);
        if (!c.propietario().equals(usuario)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return c;
    }
    private String validar(Senal s) {
        if(s.sdp()==null || s.sdp().length()>100000 || !s.sdp().startsWith("v=0")) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        return s.sdp();
    }
    @GetMapping public synchronized List<Anuncio> listar() {
        limpiar(); return canales.values().stream().map(c->new Anuncio(c.id(),c.titulo())).toList();
    }
    @PostMapping public synchronized Anuncio iniciar(@RequestHeader("X-User-Id") String usuario,@RequestBody Map<String,String> datos) {
        limpiar(); String titulo=datos.getOrDefault("titulo","").trim();
        if(titulo.isEmpty() || titulo.length()>80) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if(canales.values().stream().anyMatch(c->c.propietario().equals(usuario))) throw new ResponseStatusException(HttpStatus.CONFLICT,"Ya tienes un directo activo");
        var id=UUID.randomUUID(); canales.put(id,new Canal(id,titulo,usuario,new HashMap<>())); actividad.put(id,ahora());
        return new Anuncio(id,titulo);
    }
    @DeleteMapping("/{id}") public synchronized void cerrar(@PathVariable UUID id,@RequestHeader("X-User-Id") String usuario) {
        propio(id,usuario); canales.remove(id); actividad.remove(id);
    }
    @PutMapping("/{id}/oferta") public synchronized void ofrecer(@PathVariable UUID id,@RequestHeader("X-User-Id") String usuario,@RequestBody Senal datos) {
        var c=canal(id); String sdp=validar(datos);
        if(c.espectadores().size()>=12 && !c.espectadores().containsKey(usuario)) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
        var e=new Conexion(); e.oferta=sdp; e.activo=ahora(); c.espectadores().put(usuario,e);
    }
    @GetMapping("/{id}/ofertas") public synchronized List<Oferta> ofertas(@PathVariable UUID id,@RequestHeader("X-User-Id") String usuario) {
        var c=propio(id,usuario); actividad.put(id,ahora());
        return c.espectadores().entrySet().stream().map(e->new Oferta(e.getKey(),e.getValue().oferta)).toList();
    }
    @PutMapping("/{id}/respuesta/{uid}") public synchronized void responder(@PathVariable UUID id,@PathVariable String uid,@RequestHeader("X-User-Id") String usuario,@RequestBody Map<String,String> datos) {
        var e=propio(id,usuario).espectadores().get(uid);
        if(e!=null && Objects.equals(e.oferta,datos.get("oferta"))) e.respuesta=validar(new Senal(datos.get("sdp")));
    }
    @GetMapping("/{id}/respuesta") public synchronized Senal respuesta(@PathVariable UUID id,@RequestHeader("X-User-Id") String usuario) {
        var e=canal(id).espectadores().get(usuario); if(e==null) throw new ResponseStatusException(HttpStatus.GONE);
        e.activo=ahora(); return new Senal(e.respuesta);
    }
    @DeleteMapping("/{id}/oferta") public synchronized void salir(@PathVariable UUID id,@RequestHeader("X-User-Id") String usuario) {
        canal(id).espectadores().remove(usuario);
    }
}
