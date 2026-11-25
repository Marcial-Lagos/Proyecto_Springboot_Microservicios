package cl.duoc.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DirectosTest {
    private static final String SDP = "v=0\r\n";
    private final Directos api = new Directos(HistorialChatTest.crear());

    @Test void chatYContador() {
        var c = api.iniciar("1", Map.of("titulo", "Nexus"));
        api.ofrecer(c.id(), "2", new Directos.Senal(SDP));
        assertEquals(1, api.listar().getFirst().espectadores());
        var m = api.mensaje(c.id(), "2", Map.of("texto", " hola "));
        assertEquals("hola", m.texto());
        assertEquals("2", m.usuario());
        assertEquals(m, api.chat(c.id(),null).getFirst());
        assertEquals(400, assertThrows(ResponseStatusException.class,
            () -> api.mensaje(c.id(), "2", Map.of("texto", " "))).getStatusCode().value());
        api.salir(c.id(),"2");
        assertEquals(0, api.listar().getFirst().espectadores());
    }

    @Test void cicloDeVida() {
        var c = api.iniciar("1", Map.of("titulo", "Nexus"));
        assertEquals(c, api.listar().getFirst());
        api.cerrar(c.id(), "1");
        assertTrue(api.listar().isEmpty());
    }

    @Test void propietarioProtegido() {
        var c = api.iniciar("1", Map.of("titulo", "Nexus"));
        assertEquals(403, assertThrows(ResponseStatusException.class,
            () -> api.cerrar(c.id(), "2")).getStatusCode().value());
        assertEquals(403, assertThrows(ResponseStatusException.class,
            () -> api.ofertas(c.id(), "2")).getStatusCode().value());
    }

    @Test void senalesSeparadasPorEspectador() {
        var c = api.iniciar("1", Map.of("titulo", "Nexus"));
        api.ofrecer(c.id(), "2", new Directos.Senal(SDP));
        api.ofrecer(c.id(), "3", new Directos.Senal(SDP));
        assertEquals(2, api.ofertas(c.id(), "1").size());
        api.responder(c.id(), "2", "1", Map.of("sdp", SDP, "oferta", SDP));
        assertEquals(SDP, api.respuesta(c.id(), "2").sdp());
        assertNull(api.respuesta(c.id(), "3").sdp());
        api.salir(c.id(), "2");
        assertEquals(1, api.ofertas(c.id(), "1").size());
    }

    @Test void rechazaDuplicadosYDatosInvalidos() {
        assertEquals(400, assertThrows(ResponseStatusException.class,
            () -> api.iniciar("1", Map.of("titulo", " "))).getStatusCode().value());
        var c = api.iniciar("1", Map.of("titulo", "Nexus"));
        assertEquals(409, assertThrows(ResponseStatusException.class,
            () -> api.iniciar("1", Map.of("titulo", "Otro"))).getStatusCode().value());
        assertEquals(400, assertThrows(ResponseStatusException.class,
            () -> api.ofrecer(c.id(), "2", new Directos.Senal("invalid"))).getStatusCode().value());
    }
}
