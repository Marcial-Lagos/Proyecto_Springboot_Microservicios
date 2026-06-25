package cl.duoc.msinventario.repository;

import cl.duoc.msinventario.model.Inventario;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class InventarioRepositoryTest {

    @Autowired
    private InventarioRepository repository;

    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void findByProductoIdDebeEncontrarInventarioPersistido() {
        Long productoId = Long.parseLong(faker.number().digits(6));
        Inventario inventario = Inventario.builder()
                .productoId(productoId)
                .nombreProducto(faker.commerce().productName())
                .stockActual(10)
                .stockMinimo(5)
                .build();
        repository.save(inventario);

        assertThat(repository.findByProductoId(productoId)).isPresent();
        assertThat(repository.findByProductoId(productoId).orElseThrow().getStockActual()).isEqualTo(10);
        assertThat(repository.findByProductoId(999999L)).isEmpty();
    }
}