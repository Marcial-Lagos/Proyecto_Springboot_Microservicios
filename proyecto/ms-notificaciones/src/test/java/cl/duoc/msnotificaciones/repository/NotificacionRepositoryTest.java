package cl.duoc.msnotificaciones.repository;

import cl.duoc.msnotificaciones.model.Notificacion;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class NotificacionRepositoryTest {
    @Autowired NotificacionRepository repository;
    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void noLeidasFiltraPorUsuarioYEstado() {
        guardar(10L, false);
        guardar(10L, true);
        guardar(20L, false);

        List<Notificacion> resultado = repository.findByUsuarioIdAndLeidaFalse(10L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().isLeida()).isFalse();
        assertThat(resultado.getFirst().getUsuarioId()).isEqualTo(10L);
    }

    private void guardar(Long usuarioId, boolean leida) {
        repository.save(Notificacion.builder()
                .usuarioId(usuarioId)
                .titulo(faker.lorem().sentence(3))
                .mensaje(faker.lorem().sentence())
                .tipo(Notificacion.TipoNotificacion.SISTEMA)
                .leida(leida)
                .build());
    }
}