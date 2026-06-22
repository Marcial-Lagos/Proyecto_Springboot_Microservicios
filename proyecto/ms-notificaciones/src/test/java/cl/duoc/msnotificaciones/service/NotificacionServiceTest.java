package cl.duoc.msnotificaciones.service;

import cl.duoc.msnotificaciones.dto.NotificacionDTO;
import cl.duoc.msnotificaciones.dto.NotificacionRequestDTO;
import cl.duoc.msnotificaciones.exception.ResourceNotFoundException;
import cl.duoc.msnotificaciones.model.Notificacion;
import cl.duoc.msnotificaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock private NotificacionRepository repository;
    @InjectMocks private NotificacionService service;

    @Test
    void listarPorUsuarioMapeaNotificaciones() {
        when(repository.findByUsuarioIdOrderByFechaCreacionDesc(1L)).thenReturn(List.of(notificacion(1L, false)));

        List<NotificacionDTO> response = service.listarPorUsuario(1L);

        assertEquals(1, response.size());
        assertEquals("Pedido recibido", response.get(0).getTitulo());
    }

    @Test
    void noLeidasDevuelveSoloResultadoDelRepositorio() {
        when(repository.findByUsuarioIdAndLeidaFalse(1L)).thenReturn(List.of(notificacion(1L, false)));

        List<NotificacionDTO> response = service.noLeidas(1L);

        assertEquals(1, response.size());
        assertFalse(response.get(0).isLeida());
    }

    @Test
    void crearNotificacionPersisteDatos() {
        NotificacionRequestDTO request = request();
        when(repository.save(any(Notificacion.class))).thenAnswer(invocation -> {
            Notificacion notification = invocation.getArgument(0);
            notification.setId(2L);
            return notification;
        });

        NotificacionDTO response = service.crear(request);

        assertEquals(2L, response.getId());
        assertFalse(response.isLeida());
        assertNotNull(response.getFechaCreacion());
    }

    @Test
    void marcarLeidaActualizaNotificacionExistente() {
        Notificacion notification = notificacion(3L, false);
        when(repository.findById(3L)).thenReturn(Optional.of(notification));
        when(repository.save(notification)).thenReturn(notification);

        NotificacionDTO response = service.marcarLeida(3L);

        assertTrue(response.isLeida());
        verify(repository).save(notification);
    }

    @Test
    void marcarLeidaConIdInexistenteLanzaExcepcion() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.marcarLeida(404L));
    }

    private Notificacion notificacion(Long id, boolean leida) {
        return Notificacion.builder().id(id).usuarioId(1L).titulo("Pedido recibido")
                .mensaje("Tu pedido fue recibido").tipo(Notificacion.TipoNotificacion.PEDIDO).leida(leida).build();
    }

    private NotificacionRequestDTO request() {
        NotificacionRequestDTO request = new NotificacionRequestDTO();
        request.setUsuarioId(1L);
        request.setTitulo("Pedido recibido");
        request.setMensaje("Tu pedido fue recibido");
        request.setTipo(Notificacion.TipoNotificacion.PEDIDO);
        return request;
    }
}
