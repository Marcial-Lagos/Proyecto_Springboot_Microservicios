package cl.duoc.msproductos.repository;

import cl.duoc.msproductos.model.Producto;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository repository;

    private final Faker faker = new Faker(Locale.of("es", "CL"));

    @Test
    void consultasPorActivoYCategoriaDebenFiltrarProductosPersistidos() {
        Producto pizzaActiva = guardarProducto("PIZZAS", true);
        guardarProducto("PIZZAS", false);
        guardarProducto("BEBIDAS", true);

        List<Producto> activos = repository.findByActivoTrue();
        List<Producto> pizzasActivas = repository.findByCategoriaAndActivoTrue("PIZZAS");

        assertThat(activos).hasSize(2);
        assertThat(pizzasActivas).hasSize(1);
        assertThat(pizzasActivas.getFirst().getId()).isEqualTo(pizzaActiva.getId());
        assertThat(pizzasActivas.getFirst().isActivo()).isTrue();
    }

    private Producto guardarProducto(String categoria, boolean activo) {
        Producto producto = Producto.builder()
                .nombre("Producto " + faker.number().digits(6))
                .descripcion(faker.lorem().sentence())
                .precio(new BigDecimal("9990.00"))
                .stock(10)
                .categoria(categoria)
                .imagenUrl("https://example.com/" + faker.number().digits(6) + ".jpg")
                .activo(activo)
                .build();
        return repository.save(producto);
    }
}