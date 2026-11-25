package cl.duoc.apigateway;

import java.util.UUID;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.core.io.ClassPathResource;
import static org.junit.jupiter.api.Assertions.*;

class HistorialMySqlTest {
    @Test
    @EnabledIfEnvironmentVariable(named="NEXUS_TEST_MYSQL_URL",matches=".+")
    void conservaDatosEnMySqlReal() {
        String url=System.getenv("NEXUS_TEST_MYSQL_URL");
        String user=System.getenv("NEXUS_TEST_MYSQL_USER");
        String password=System.getenv("NEXUS_TEST_MYSQL_PASSWORD");
        String schema="nexus_chat_test_"+UUID.randomUUID().toString().replace("-","");
        var admin=new JdbcTemplate(new DriverManagerDataSource(url,user,password));
        admin.execute("CREATE DATABASE "+schema+" CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        try {
            var ds=new DriverManagerDataSource(url+schema,user,password);
            var migracion=new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
            migracion.execute(ds); migracion.execute(ds);
            var antes=new Directos(new HistorialChat(new JdbcTemplate(ds)));
            var canal=antes.iniciar("101",Map.of("titulo","Prueba aislada"));
            var mensaje=antes.mensaje(canal.id(),"102",Map.of("texto","Mensaje persistente"));
            antes.cerrar(canal.id(),"101");
            var despues=new Directos(new HistorialChat(new JdbcTemplate(new DriverManagerDataSource(url+schema,user,password))));
            assertEquals(mensaje,despues.chat(canal.id(),null).getFirst());
            assertTrue(despues.listar().isEmpty());
            var chat=new ChatComunidad(new HistorialChat(new JdbcTemplate(ds)));
            chat.enviar("general","102",Map.of("texto","Comunidad persistente"));
            assertEquals(1,chat.leer("general",null).size());
            assertTrue(chat.leer("juegos",null).isEmpty());
        } finally {
            admin.execute("DROP DATABASE "+schema);
        }
    }
}
