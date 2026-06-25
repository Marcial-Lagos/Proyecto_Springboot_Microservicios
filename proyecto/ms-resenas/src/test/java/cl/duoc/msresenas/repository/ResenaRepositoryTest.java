package cl.duoc.msresenas.repository;

import cl.duoc.msresenas.model.Resena;
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
class ResenaRepositoryTest {
    @Autowired ResenaRepository repository;
    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void findByProductoIdRetornaSoloResenasDelProducto() {
        guardar(10L, 1L);
        guardar(20L, 1L);
        guardar(20L, 2L);

        List<Resena> resultado = repository.findByProductoId(20L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).allMatch(r -> r.getProductoId().equals(20L));
    }

    private void guardar(Long productoId, Long usuarioId) {
        repository.save(Resena.builder()
                .productoId(productoId).usuarioId(usuarioId).pedidoId(1L)
                .calificacion(5).comentario(faker.lorem().sentence()).build());
    }
}