package cl.duoc.apigateway;

import java.util.UUID;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import static org.junit.jupiter.api.Assertions.*;

class HistorialChatTest {
    static HistorialChat crear() {
        var ds=new DriverManagerDataSource("jdbc:h2:mem:"+UUID.randomUUID()+";MODE=MySQL;DB_CLOSE_DELAY=-1","sa","");
        new ResourceDatabasePopulator(new ClassPathResource("schema.sql")).execute(ds);
        return new HistorialChat(new JdbcTemplate(ds));
    }
    @Test void conservaHistorialTrasRecrearControlador() {
        var historial=crear();
        var antes=new Directos(historial);
        var canal=antes.iniciar("1", Map.of("titulo","Prueba"));
        var m=antes.mensaje(canal.id(),"2",Map.of("texto","Hola"));
        antes.cerrar(canal.id(),"1");
        var despues=new Directos(historial);
        assertTrue(despues.listar().isEmpty());
        assertEquals(m,despues.chat(canal.id(),null).getFirst());
        assertEquals(canal.id().toString(),despues.archivo().getFirst().id());
    }
    @Test void comunidadAisladaYCursor() {
        var chat=new ChatComunidad(crear());
        var a=chat.enviar("general","1",Map.of("texto","Uno"));
        var b=chat.enviar("general","2",Map.of("texto","Dos"));
        assertEquals(2,chat.leer("general",null).size());
        assertEquals(a,chat.leer("general",b.id()).getFirst());
        assertTrue(chat.leer("juegos",null).isEmpty());
    }
}
