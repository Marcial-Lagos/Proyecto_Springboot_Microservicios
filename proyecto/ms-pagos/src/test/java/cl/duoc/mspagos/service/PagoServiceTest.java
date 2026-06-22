package cl.duoc.mspagos.service;

import cl.duoc.mspagos.dto.PagoDTO;
import cl.duoc.mspagos.dto.PagoRequestDTO;
import cl.duoc.mspagos.model.Pago;
import cl.duoc.mspagos.repository.PagoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock private PagoRepository repository;
    @InjectMocks private PagoService service;

    @Test
    void listarMapeaPagosADto() {
        when(repository.findAll()).thenReturn(List.of(pago(1L, Pago.EstadoPago.APROBADO)));

        List<PagoDTO> response = service.listar();

        assertEquals(1, response.size());
        assertEquals(new BigDecimal("12000"), response.get(0).getMonto());
    }

    @Test
    void buscarPorIdDevuelvePagoExistente() {
        when(repository.findById(1L)).thenReturn(Optional.of(pago(1L, Pago.EstadoPago.APROBADO)));

        PagoDTO response = service.buscarPorId(1L);

        assertEquals(Pago.MetodoPago.WEBPAY, response.getMetodo());
    }

    @Test
    void listarPorPedidoUsaConsultaEspecializada() {
        when(repository.findByPedidoId(5L)).thenReturn(List.of(pago(1L, Pago.EstadoPago.APROBADO)));

        List<PagoDTO> response = service.listarPorPedido(5L);

        assertEquals(1, response.size());
        verify(repository).findByPedidoId(5L);
    }

    @Test
    void procesarPagoLoApruebaYGeneraReferencia() {
        PagoRequestDTO request = request();
        when(repository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago payment = invocation.getArgument(0);
            payment.setId(2L);
            return payment;
        });

        PagoDTO response = service.procesar(request);

        assertEquals(2L, response.getId());
        assertEquals(Pago.EstadoPago.APROBADO, response.getEstado());
        assertTrue(response.getReferencia().startsWith("REF-"));
    }

    @Test
    void reembolsarActualizaEstadoDelPago() {
        Pago payment = pago(3L, Pago.EstadoPago.APROBADO);
        when(repository.findById(3L)).thenReturn(Optional.of(payment));
        when(repository.save(payment)).thenReturn(payment);

        PagoDTO response = service.reembolsar(3L);

        assertEquals(Pago.EstadoPago.REEMBOLSADO, response.getEstado());
        verify(repository).save(payment);
    }

    private Pago pago(Long id, Pago.EstadoPago estado) {
        return Pago.builder().id(id).pedidoId(5L).usuarioId(1L).monto(new BigDecimal("12000"))
                .metodo(Pago.MetodoPago.WEBPAY).estado(estado).referencia("REF-ABC12345").build();
    }

    private PagoRequestDTO request() {
        PagoRequestDTO request = new PagoRequestDTO();
        request.setPedidoId(5L);
        request.setUsuarioId(1L);
        request.setMonto(new BigDecimal("12000"));
        request.setMetodo(Pago.MetodoPago.WEBPAY);
        return request;
    }
}
