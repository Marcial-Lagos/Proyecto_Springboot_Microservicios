package cl.duoc.apigateway;

import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Repository
public class HistorialChat {
    public record Mensaje(long id, String usuario, String texto, String fecha) {}
    public record Archivo(String id, String titulo, String fecha) {}
    private final JdbcTemplate jdbc;
    public HistorialChat(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    void registrar(UUID id, String titulo, String propietario) {
        jdbc.update("INSERT INTO nexus_chat_directo(id,titulo,propietario) VALUES (?,?,?)",
                id.toString(), titulo, propietario);
    }
    boolean existe(UUID id) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM nexus_chat_directo WHERE id=?",
                Integer.class,id.toString()) > 0;
    }
    List<Archivo> archivos() {
        return jdbc.query("SELECT id,titulo,creado FROM nexus_chat_directo ORDER BY creado DESC,id DESC LIMIT 100",
                (r,n)->new Archivo(r.getString("id"),r.getString("titulo"),r.getTimestamp("creado").toInstant().toString()));
    }
    Mensaje guardar(String canal, String usuario, String texto, int maximo) {
        String limpio = Objects.toString(texto,"").trim();
        if(limpio.isEmpty() || limpio.length()>maximo) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        var key = new GeneratedKeyHolder();
        jdbc.update(c -> {
            var ps=c.prepareStatement("INSERT INTO nexus_chat_mensaje(canal,usuario,texto) VALUES (?,?,?)", new String[]{"id"});
            ps.setString(1,canal); ps.setString(2,usuario); ps.setString(3,limpio);
            return ps;
        },key);
        return jdbc.queryForObject("SELECT id,usuario,texto,creado FROM nexus_chat_mensaje WHERE id=?",
            (r,n)->new Mensaje(r.getLong("id"),r.getString("usuario"),r.getString("texto"),r.getTimestamp("creado").toInstant().toString()),
            Objects.requireNonNull(key.getKey()).longValue());
    }
    List<Mensaje> leer(String canal, Long antes) {
        var lista=jdbc.query("SELECT id,usuario,texto,creado FROM nexus_chat_mensaje WHERE canal=? AND id<? ORDER BY id DESC LIMIT 100",
            (r,n)->new Mensaje(r.getLong("id"),r.getString("usuario"),r.getString("texto"),r.getTimestamp("creado").toInstant().toString()),
            canal, antes==null?Long.MAX_VALUE:antes);
        Collections.reverse(lista);
        return lista;
    }
}
