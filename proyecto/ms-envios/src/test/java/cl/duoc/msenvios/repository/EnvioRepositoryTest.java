package cl.duoc.msenvios.repository;

import cl.duoc.msenvios.model.Envio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EnvioRepositoryTest {

    @Autowired
    private EnvioRepository repository;

    @Test
    void findByCodigoSeguimientoDebeEncontrarEnvioPersistido() {
        Envio envio = repository.save(Envio.builder()
                .pedidoId(10L)
                .direccionOrigen("Origen 123")
                .direccionDestino("Destino 456")
                .codigoSeguimiento("ENV-TEST01")
                .estado(Envio.EstadoEnvio.PENDIENTE)
                .build());

        assertThat(repository.findByCodigoSeguimiento("ENV-TEST01")).isPresent();
        assertThat(repository.findByCodigoSeguimiento("ENV-TEST01").orElseThrow().getId()).isEqualTo(envio.getId());
        assertThat(repository.findByCodigoSeguimiento("ENV-NOEXISTE")).isEmpty();
    }
}