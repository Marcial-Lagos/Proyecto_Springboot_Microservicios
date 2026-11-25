package cl.duoc.apigateway;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class VozTest {
    @Test void participantesVisiblesSinEntrar() {
        var v = new Voz();
        v.entrar("general","1",Map.of("nombre","Ana"));
        v.entrar("ranked","2",Map.of("nombre","Luis"));
        assertEquals("Ana",v.participantes("general").getFirst().nombre());
        assertEquals(1,v.participantes("ranked").size());
        v.salir("general","1");
        assertTrue(v.participantes("general").isEmpty());
    }
    @Test void presenciaYsenalPrivada() {
        var v = new Voz();
        v.entrar("general","1", Map.of("nombre","Ana"));
        v.entrar("general","2", Map.of("nombre","Luis"));
        v.entrar("general","3", Map.of("nombre","Eva"));
        v.senal("general","1",new Voz.Envio("2","offer","v=0"));
        var e = v.estado("general","2",new Voz.Presencia(true,false));
        assertEquals(3,e.miembros().size());
        assertTrue(e.miembros().stream().anyMatch(m -> m.id().equals("2") && m.hablando()));
        assertEquals("1",e.senales().getFirst().de());
        assertTrue(v.estado("general","3",new Voz.Presencia(false,false)).senales().isEmpty());
        assertTrue(v.estado("general","2",new Voz.Presencia(true,true)).senales().isEmpty());
        v.salir("general","2");
        assertEquals(2,v.estado("general","1",new Voz.Presencia(false,false)).miembros().size());
    }
    @Test void requiereMembresiaYLimitaSala() {
        var v = new Voz();
        assertEquals(410,assertThrows(ResponseStatusException.class,()->v.estado("ranked","1",new Voz.Presencia(false,false))).getStatusCode().value());
        for(int i=0;i<6;i++)v.entrar("ranked",""+i,Map.of("nombre","P"+i));
        assertEquals(429,assertThrows(ResponseStatusException.class,()->v.entrar("ranked","7",Map.of("nombre","P7"))).getStatusCode().value());
    }
}
