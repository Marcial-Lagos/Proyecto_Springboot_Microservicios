package cl.duoc.msresenas.service;

import cl.duoc.msresenas.dto.ResenaDTO;
import cl.duoc.msresenas.dto.ResenaRequestDTO;
import cl.duoc.msresenas.model.Resena;
import cl.duoc.msresenas.repository.ResenaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock private ResenaRepository repository;
    @InjectMocks private ResenaService service;

    @Test
    void listarPorProductoMapeaResenasADto() {
        when(repository.findByProductoId(10L)).thenReturn(List.of(resena(1L, 5)));

        List<ResenaDTO> response = service.listarPorProducto(10L);

        assertEquals(1, response.size());
        assertEquals(5, response.get(0).getCalificacion());
    }

    @Test
    void listarPorUsuarioMapeaResenasADto() {
        when(repository.findByUsuarioId(1L)).thenReturn(List.of(resena(1L, 4)));

        List<ResenaDTO> response = service.listarPorUsuario(1L);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getUsuarioId());
    }

    @Test
    void promedioDevuelveResultadoDelRepositorio() {
        when(repository.promedioCalificacion(10L)).thenReturn(4.5);

        Map<String, Object> response = service.promedio(10L);

        assertEquals(10L, response.get("productoId"));
        assertEquals(4.5, response.get("promedio"));
    }

    @Test
    void crearResenaPersisteDatos() {
        ResenaRequestDTO request = request();
        when(repository.save(any(Resena.class))).thenAnswer(invocation -> {
            Resena review = invocation.getArgument(0);
            review.setId(2L);
            return review;
        });

        ResenaDTO response = service.crear(request);

        assertEquals(2L, response.getId());
        assertEquals("Muy rico", response.getComentario());
        assertNotNull(response.getFechaCreacion());
    }

    @Test
    void eliminarResenaExistenteLlamaAlRepositorio() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }

    private Resena resena(Long id, int calificacion) {
        return Resena.builder().id(id).usuarioId(1L).productoId(10L).pedidoId(100L)
                .calificacion(calificacion).comentario("Muy rico").build();
    }

    private ResenaRequestDTO request() {
        ResenaRequestDTO request = new ResenaRequestDTO();
        request.setUsuarioId(1L);
        request.setProductoId(10L);
        request.setPedidoId(100L);
        request.setCalificacion(5);
        request.setComentario("Muy rico");
        return request;
    }
}
