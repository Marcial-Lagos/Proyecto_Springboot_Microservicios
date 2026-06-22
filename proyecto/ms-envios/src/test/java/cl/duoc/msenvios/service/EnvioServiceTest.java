package cl.duoc.msenvios.service;

import cl.duoc.msenvios.dto.EnvioDTO;
import cl.duoc.msenvios.dto.EnvioRequestDTO;
import cl.duoc.msenvios.exception.ResourceNotFoundException;
import cl.duoc.msenvios.model.Envio;
import cl.duoc.msenvios.repository.EnvioRepository;
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
class EnvioServiceTest {

    @Mock private EnvioRepository repository;
    @InjectMocks private EnvioService service;

    @Test
    void listarMapeaEnviosADto() {
        when(repository.findAll()).thenReturn(List.of(envio(1L, Envio.EstadoEnvio.PENDIENTE)));

        List<EnvioDTO> response = service.listar();

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getPedidoId());
    }

    @Test
    void buscarPorIdDevuelveEnvioExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(envio(1L, Envio.EstadoEnvio.PENDIENTE)));

        EnvioDTO response = service.buscarPorId(1L);

        assertEquals("ENV-ABC12345", response.getCodigoSeguimiento());
    }

    @Test
    void crearGeneraCodigoDeSeguimiento() {
        EnvioRequestDTO request = request();
        when(repository.save(any(Envio.class))).thenAnswer(invocation -> {
            Envio shipment = invocation.getArgument(0);
            shipment.setId(2L);
            return shipment;
        });

        EnvioDTO response = service.crear(request);

        assertEquals(2L, response.getId());
        assertTrue(response.getCodigoSeguimiento().startsWith("ENV-"));
        assertNotNull(response.getFechaEstimada());
    }

    @Test
    void cambiarEstadoEntregadoRegistraFechaDeEntrega() {
        Envio shipment = envio(3L, Envio.EstadoEnvio.EN_CAMINO);
        when(repository.findById(3L)).thenReturn(Optional.of(shipment));
        when(repository.save(shipment)).thenReturn(shipment);

        EnvioDTO response = service.cambiarEstado(3L, "ENTREGADO");

        assertEquals(Envio.EstadoEnvio.ENTREGADO, response.getEstado());
        assertNotNull(response.getFechaEntrega());
    }

    @Test
    void buscarPorCodigoInexistenteLanzaExcepcion() {
        when(repository.findByCodigoSeguimiento("NO-EXISTE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorCodigo("NO-EXISTE"));
    }

    private Envio envio(Long id, Envio.EstadoEnvio estado) {
        return Envio.builder().id(id).pedidoId(1L).direccionOrigen("Origen 123").direccionDestino("Destino 456")
                .codigoSeguimiento("ENV-ABC12345").estado(estado).build();
    }

    private EnvioRequestDTO request() {
        EnvioRequestDTO request = new EnvioRequestDTO();
        request.setPedidoId(1L);
        request.setDireccionOrigen("Origen 123");
        request.setDireccionDestino("Destino 456");
        return request;
    }
}
