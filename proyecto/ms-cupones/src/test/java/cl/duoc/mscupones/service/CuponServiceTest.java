package cl.duoc.mscupones.service;

import cl.duoc.mscupones.dto.CuponDTO;
import cl.duoc.mscupones.dto.CuponRequestDTO;
import cl.duoc.mscupones.exception.ResourceNotFoundException;
import cl.duoc.mscupones.model.Cupon;
import cl.duoc.mscupones.repository.CuponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuponServiceTest {

    @Mock private CuponRepository repository;
    @InjectMocks private CuponService service;

    @Test
    void listarMapeaCuponesADto() {
        when(repository.findAll()).thenReturn(List.of(cupon(1L, "DESCUENTO10", 0, true)));

        List<CuponDTO> response = service.listar();

        assertEquals(1, response.size());
        assertEquals("DESCUENTO10", response.get(0).getCodigo());
        assertTrue(response.get(0).isValido());
    }

    @Test
    void crearNormalizaCodigoEnMayusculas() {
        CuponRequestDTO request = request();
        when(repository.existsByCodigo("descuento10")).thenReturn(false);
        when(repository.save(any(Cupon.class))).thenAnswer(invocation -> {
            Cupon coupon = invocation.getArgument(0);
            coupon.setId(2L);
            return coupon;
        });

        CuponDTO response = service.crear(request);

        assertEquals("DESCUENTO10", response.getCodigo());
        assertTrue(response.isActivo());
    }

    @Test
    void crearConCodigoDuplicadoLanzaError() {
        CuponRequestDTO request = request();
        when(repository.existsByCodigo(request.getCodigo())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.crear(request));

        assertEquals("Código ya existe", exception.getMessage());
        verify(repository, never()).save(any(Cupon.class));
    }

    @Test
    void validarCuponPorcentajeCalculaDescuento() {
        Cupon coupon = cupon(1L, "DESCUENTO10", 0, true);
        when(repository.findByCodigo("DESCUENTO10")).thenReturn(Optional.of(coupon));

        Map<String, Object> response = service.validar("descuento10", new BigDecimal("20000"));

        assertEquals(true, response.get("valido"));
        assertEquals(new BigDecimal("2000"), response.get("descuento"));
    }

    @Test
    void aplicarCuponIncrementaUsosActuales() {
        Cupon coupon = cupon(1L, "DESCUENTO10", 2, true);
        when(repository.findByCodigo("DESCUENTO10")).thenReturn(Optional.of(coupon));

        service.aplicar("descuento10");

        assertEquals(3, coupon.getUsosActuales());
        verify(repository).save(coupon);
    }

    private Cupon cupon(Long id, String codigo, int usos, boolean activo) {
        return Cupon.builder().id(id).codigo(codigo).tipo(Cupon.TipoDescuento.PORCENTAJE)
                .valor(new BigDecimal("10")).montoMinimo(new BigDecimal("1000"))
                .fechaVencimiento(LocalDateTime.now().plusDays(1)).usosMaximos(10)
                .usosActuales(usos).activo(activo).build();
    }

    private CuponRequestDTO request() {
        CuponRequestDTO request = new CuponRequestDTO();
        request.setCodigo("descuento10");
        request.setTipo(Cupon.TipoDescuento.PORCENTAJE);
        request.setValor(new BigDecimal("10"));
        request.setMontoMinimo(new BigDecimal("1000"));
        request.setFechaVencimiento(LocalDateTime.now().plusDays(1));
        request.setUsosMaximos(10);
        return request;
    }
}
